package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Map;

public class ProcesarPlanBehaviour extends CyclicBehaviour {

    // Mapas para guardar el estado de los planes en proceso, usando el ID de conversación como clave.
    private final Map<String, String> asignaturas = new HashMap<>();
    private final Map<String, String> horasRecibidas = new HashMap<>();
    private final Map<String, String> prioridadesRecibidas = new HashMap<>();

    public ProcesarPlanBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        // Esperamos mensajes de tipo INFORM que contengan datos para el plan.
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String conversationId = msg.getConversationId();
            String contenido = msg.getContent();
            System.out.println("[Ensamblador] Recibido dato parcial de " + msg.getSender().getLocalName() + " para la conversación: " + conversationId);
            System.out.println("[Ensamblador] Contenido: " + contenido);

            if (conversationId == null) {
                // Si no hay ID de conversación, no podemos procesarlo.
                return;
            }

            // El contenido esperado es del tipo "clave=valor", por ejemplo, "asignatura=Sistemas Inteligentes"
            String[] partes = contenido.split("=");
            if (partes.length == 2) {
                String clave = partes[0].trim();
                String valor = partes[1].trim();

                // Guardamos el dato en el mapa correspondiente
                switch (clave) {
                    case "asignatura":
                        asignaturas.put(conversationId, valor);
                        break;
                    case "horas":
                        horasRecibidas.put(conversationId, valor);
                        break;
                    case "prioridad":
                        prioridadesRecibidas.put(conversationId, valor);
                        break;
                }
            }

            // --- Comprobación de si tenemos todos los datos ---
            if (asignaturas.containsKey(conversationId) && horasRecibidas.containsKey(conversationId) && prioridadesRecibidas.containsKey(conversationId)) {
                
                // ¡Tenemos toda la información!
                String asignatura = asignaturas.get(conversationId);
                String horas = horasRecibidas.get(conversationId);
                String prioridad = prioridadesRecibidas.get(conversationId);

                // --- Lógica de Ensamblaje del Plan ---
                System.out.println("\n-------------------------------------------");
                System.out.println("      PLAN DE ESTUDIO ENSAMBLADO         ");
                System.out.println("-------------------------------------------");
                System.out.println("Asignatura: " + asignatura);
                System.out.println("-> Horas de estudio recomendadas: " + horas);
                System.out.println("-> Nivel de prioridad: " + prioridad);
                System.out.println("-------------------------------------------\n");

                // Limpiamos los mapas para esta conversación para no volver a procesarla.
                asignaturas.remove(conversationId);
                horasRecibidas.remove(conversationId);
                prioridadesRecibidas.remove(conversationId);
            }

        } else {
            // Si no hay mensajes, el comportamiento se bloquea hasta que llegue uno nuevo.
            block();
        }
    }
}