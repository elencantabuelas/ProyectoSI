package agentes;

import behaviours.ProcesarPlanBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;

import static utils.UtilidadesDF.registrarServicio;

public class AgenteEnsamblador extends Agent {

    @Override
    protected void setup() {
        System.out.println("Agente Ensamblador (" + getLocalName() + ") iniciando...");

        // Usando nuestra clase de utilidades
        registrarServicio(this, "ensamblador-plan", "JADE-planificacion-estudios");

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