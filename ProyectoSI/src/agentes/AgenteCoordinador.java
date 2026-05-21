package agentes;

import behaviours.RecibirExamenBehaviour;
import behaviours.RecibirRespuestasCalculoBehaviour;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import ontologia.OntologiaExamenes;

public class AgenteCoordinador extends Agent {

    private final Codec codec = new SLCodec();
    private final Ontology ontologia = OntologiaExamenes.getInstance();

    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("AgenteCoordinador iniciado: "+getLocalName());

        //comportamiento de espera de los examenes
        addBehaviour(new RecibirExamenBehaviour(this, codec, ontologia));

        //comportamiento de espera de los resultados calculados
        addBehaviour(new RecibirRespuestasCalculoBehaviour(this));
    }
}
