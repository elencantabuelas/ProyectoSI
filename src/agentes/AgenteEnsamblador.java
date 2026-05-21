package agentes;

import behaviours.ProcesarPlanBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class AgenteEnsamblador extends Agent {

    @Override
    protected void setup() {
        System.out.println("Agente Ensamblador (" + getLocalName() + ") iniciando...");


        // REGISTRO EN EL DIRECTORY FACILITATOR para que el Coordinador encuentre al agente
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType("ensamblador-plan"); //tipo de servicio
        sd.setName("JADE-planificacion-estudios");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("Agente Ensamblador registrado correctamente en el DF.");
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        //COMPORTAMIENTO PRINCIPAL
        addBehaviour(new ProcesarPlanBehaviour(this));
    }

    @Override
    protected void takeDown() {
//desregistrarse del DF
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        System.out.println("Agente Ensamblador (" + getAID().getName() + ") terminando.");
    }
}