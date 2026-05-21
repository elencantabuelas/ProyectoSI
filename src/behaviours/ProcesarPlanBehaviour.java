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
        //FILTRADO DE MENSAJES
        // el coordinador envia un REQUES
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

        //RECEPCION
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {

            String contenido = msg.getContent();
            System.out.println("\n[Ensamblador] Mensaje recibido de " + msg.getSender().getLocalName());
            System.out.println("[Ensamblador] Contenido: " + contenido);

            //deveria de llegar como asignatura,horas,prioridad
            String[] partes = contenido.split(",");

            if (partes.length == 3) {
                String asignatura = partes[0].trim();
                String horas = partes[1].trim();
                String prioridad = partes[2].trim();

                // me falta hacer la logica para almacenar y ordenar las asignaturas
                
                System.out.println("[Ensamblador] Procesando -> Asignatura: " + asignatura +
                        " | Horas: " + horas + " | Prioridad: " + prioridad);

                //RESPUESTA
                // createReply() que copia el campo ConversationId automáticamente
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.INFORM);
                reply.setContent("Plan guardado para " + asignatura);

                myAgent.send(reply);
                System.out.println("[Ensamblador] Confirmación enviada con ID de conversación: "
                        + reply.getConversationId());
            } else {
                System.err.println("[Ensamblador] ERROR: Formato de mensaje incorrecto. " +
                        "Se esperaba 'asignatura,horas,prioridad' y llegó: " + contenido);
            }
        } else {
            //BLOQUEO
            //si no hay mensajes, pausamos asta que llegue uno nuevo
            block();
        }
    }
}