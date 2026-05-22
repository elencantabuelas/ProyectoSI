package agentes;

import behaviours.ProcesarPlanBehaviour;
import behaviours.RecibirExamenBehaviour;
import behaviours.RecibirRespuestasCalculoBehaviour;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import ontologia.OntologiaExamenes;

import static utils.UtilidadesDF.registrarServicio;

public class AgenteCoordinador extends Agent {

    private final Codec codec = new SLCodec();
    private final Ontology ontologia = OntologiaExamenes.getInstance();

    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("AgenteCoordinador iniciado: "+getLocalName());

        //llamada al registro
        registrarServicio(this, "servicio-coordinador", "planificacion-estudios");

        addBehaviour(new RecibirExamenBehaviour(this, codec, ontologia));

        //comportamiento de espera de los resultados calculados
        //addBehaviour(new RecibirRespuestasCalculoBehaviour(this));
    }

    @Override
    protected void takeDown() {
        //desregistrarse del DF
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Agente Coordinador (" + getAID().getName() + ") terminando.");
    }
}
