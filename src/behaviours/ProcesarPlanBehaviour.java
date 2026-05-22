package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ProcesarPlanBehaviour extends CyclicBehaviour {

    public ProcesarPlanBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        // Espera a recibir un mensaje con el performative REQUEST
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String contenido = msg.getContent();
            System.out.println("\n[Ensamblador] Petición de plan recibida de " + msg.getSender().getLocalName());
            System.out.println("[Ensamblador] Datos recibidos: " + contenido);

            // El contenido esperado es "asignatura,horas,prioridad"
            String[] partes = contenido.split(",");

            if (partes.length == 3) {
                String asignatura = partes[0].trim();
                String horas = partes[1].trim();
                String prioridad = partes[2].trim();

                // --- Lógica de Ensamblaje del Plan ---
                System.out.println("-------------------------------------------");
                System.out.println("          PLAN DE ESTUDIO GENERADO         ");
                System.out.println("-------------------------------------------");
                System.out.println("Asignatura: " + asignatura);
                System.out.println("-> Horas de estudio recomendadas: " + horas);
                System.out.println("-> Nivel de prioridad: " + prioridad);
                System.out.println("-------------------------------------------");


                // Enviar una confirmación al coordinador
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Plan de estudio para '" + asignatura + "' generado y mostrado en consola.");
                myAgent.send(reply);

            } else {
                System.err.println("[Ensamblador] ERROR: Formato de mensaje incorrecto.");
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("El formato del mensaje era incorrecto. Se esperaba 'asignatura,horas,prioridad'.");
                myAgent.send(reply);
            }
        } else {
            // Si no hay mensajes, el comportamiento se bloquea hasta que llegue uno
            block();
        }
    }
}