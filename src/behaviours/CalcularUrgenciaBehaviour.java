package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CalcularUrgenciaBehaviour extends CyclicBehaviour {

    public CalcularUrgenciaBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        // Filtramos para recibir solo mensajes de tipo REQUEST
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage mensaje = myAgent.receive(mt);

        if (mensaje != null) {
            // Extraemos los dias pendientes
            String contenido = mensaje.getContent();
            System.out.println("AgenteUrgencia recibio petición de " + mensaje.getSender().getLocalName() + ". Dias restantes: " + contenido);

            try {
                int diasRestantes = Integer.parseInt(contenido);
                String prioridad;

                // Calculamos la urgencia
                if (diasRestantes <= 3) {
                    prioridad = "ALTA";
                } else if (diasRestantes >= 10) {
                    prioridad = "BAJA";
                } else {
                    prioridad = "MEDIA";
                }

                System.out.println("AgenteUrgencia calculo prioridad: " + prioridad);

                // Preparamos la respuesta (INFORM) al coordinador
                ACLMessage respuesta = mensaje.createReply();
                respuesta.setPerformative(ACLMessage.INFORM);
                respuesta.setContent(prioridad);

                // Enviamos el mensaje de vuelta
                myAgent.send(respuesta);

            } catch (NumberFormatException e) {
                System.err.println("AgenteUrgencia: Error al leer los dias restantes (" + contenido + ")");
            }
        } else {
            block();
        }
    }
}