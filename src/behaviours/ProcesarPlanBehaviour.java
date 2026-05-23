package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;

public class ProcesarPlanBehaviour extends CyclicBehaviour {

    // Mapas para guardar el estado de los planes en proceso, usando el ID de conversación como clave
    private final Map<String, String> asignaturas = new HashMap<>();
    private final Map<String, String> horasRecibidas = new HashMap<>();
    private final Map<String, String> prioridadesRecibidas = new HashMap<>();

    public ProcesarPlanBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        // Esperamos mensajes de tipo INFORM que contengan datos para el plan
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String conversationId = msg.getConversationId();
            String contenido = msg.getContent();
            System.out.println("[Ensamblador] Recibido dato de " + msg.getSender().getLocalName() + " para la conversación: " + conversationId);
            System.out.println("[Ensamblador] Contenido: " + contenido);

            if (conversationId == null) {
                return; // Ignorar mensajes sin ID de conversación
            }

            // El contenido esperado es "clave1=valor1;clave2=valor2"
            String[] bloques = contenido.split(";");

            for (String bloque : bloques) {
                String[] par = bloque.split("=");
                if (par.length == 2) {
                    String clave = par[0].trim();
                    String valor = par[1].trim();

                    // se guarda el dato
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
            }

            // --- compruebo de si tenemos todos los datos ---
            if (asignaturas.containsKey(conversationId) && horasRecibidas.containsKey(conversationId) && prioridadesRecibidas.containsKey(conversationId)) {
                
                String asignatura = asignaturas.get(conversationId);
                String horas = horasRecibidas.get(conversationId);
                String prioridad = prioridadesRecibidas.get(conversationId);

                //mensaje a mostrar
                String mensajeResultado = "Asignatura: " + asignatura + "\n"
                                        + "Horas de estudio recomendadas: " + horas + "\n"
                                        + "Nivel de prioridad: " + prioridad;

                //mensaje en una ventana emergente
                JOptionPane.showMessageDialog(
                        null,
                        mensajeResultado,
                        "Plan de Estudio Generado",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // limpio los mapas de la comverzacion
                asignaturas.remove(conversationId);
                horasRecibidas.remove(conversationId);
                prioridadesRecibidas.remove(conversationId);
            }

        } else {
            // Si no hay mensajes el comportamiento se bloquea hasta que llegue uno nuevo
            block();
        }
    }
}