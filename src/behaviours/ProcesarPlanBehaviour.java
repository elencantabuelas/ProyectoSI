package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.StaleProxyException;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcesarPlanBehaviour extends CyclicBehaviour {

    //Variables de Estado para Acumular Resultados
    private int totalPlanesEsperados = 0;
    private final List<String> planesCompletados = new ArrayList<>();

    // Mapas para guardar las piezas de cada plan en proceso
    private final Map<String, String> asignaturas = new HashMap<>();
    private final Map<String, String> horasRecibidas = new HashMap<>();
    private final Map<String, String> prioridadesRecibidas = new HashMap<>();

    public ProcesarPlanBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            String contenido = msg.getContent();
            System.out.println("[Ensamblador] Recibido INFORM de " + msg.getSender().getLocalName());
            System.out.println("[Ensamblador] Contenido: " + contenido);

            // procesar mensajes
            if (contenido.startsWith("totalExamenes=")) {
                //Mensaje del Coordinador con el total de planes a esperar
                try {
                    totalPlanesEsperados = Integer.parseInt(contenido.split("=")[1]);
                    System.out.println("[Ensamblador] Se esperan " + totalPlanesEsperados + " planes en total.");
                } catch (Exception e) {
                    System.err.println("[Ensamblador] Error al leer el total de exámenes.");
                }
            } else {
                //Mensaje de los agentes Esfurzo y Urgencia
                String conversationId = msg.getConversationId();
                if (conversationId == null) return;
                procesarDatosParciales(conversationId, contenido);
            }

        } else {
            block();
        }
    }

    private void procesarDatosParciales(String conversationId, String contenido) {
        String[] bloques = contenido.split(";");
        for (String bloque : bloques) {
            String[] par = bloque.split("=");
            if (par.length == 2) {
                String clave = par[0].trim();
                String valor = par[1].trim();
                switch (clave) {
                    case "asignatura": asignaturas.put(conversationId, valor); break;
                    case "horas": horasRecibidas.put(conversationId, valor); break;
                    case "prioridad": prioridadesRecibidas.put(conversationId, valor); break;
                }
            }
        }

        // Compruevo si un plan esta completo
        if (asignaturas.containsKey(conversationId) && horasRecibidas.containsKey(conversationId) && prioridadesRecibidas.containsKey(conversationId)) {
            
            String asignatura = asignaturas.get(conversationId);
            String horas = horasRecibidas.get(conversationId);
            String prioridad = prioridadesRecibidas.get(conversationId);

            // Formatear y añadir el plan completado a la lista
            String planFormateado = "Asignatura: " + asignatura + "\n"
                                  + "  - Horas recomendadas: " + horas + "\n"
                                  + "  - Prioridad: " + prioridad + "\n";
            planesCompletados.add(planFormateado);
            System.out.println("[Ensamblador] Plan para '" + asignatura + "' completado y añadido a la lista. (" + planesCompletados.size() + "/" + totalPlanesEsperados + ")");

            // Limpiar los mapas para este plan
            asignaturas.remove(conversationId);
            horasRecibidas.remove(conversationId);
            prioridadesRecibidas.remove(conversationId);

            // Compruevo si todos los planes han llegado
            if (totalPlanesEsperados > 0 && planesCompletados.size() >= totalPlanesEsperados) {
                mostrarResultadosFinalesYApagar();
            }
        }
    }

    private void mostrarResultadosFinalesYApagar() {
        // Construir el string final con todos los planes
        StringBuilder sb = new StringBuilder("Se han generado todos los planes de estudio:\n\n");
        for (String plan : planesCompletados) {
            sb.append(plan).append("\n");
        }

        // Mostrar la ventana emergente final
        JOptionPane.showMessageDialog(
                null,
                sb.toString(),
                "Planes de Estudio Generados",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Limpiar estado
        planesCompletados.clear();
        totalPlanesEsperados = 0;

        // Iniciar el apagado de la plataforma JADE y el programa
        try {
            System.out.println("[Ensamblador] Todo el trabajo completado. Apagando la plataforma...");
            myAgent.getContainerController().kill();
            System.out.println("Cerrando la aplicación...");
            System.exit(0);
        } catch (StaleProxyException e) {
            System.err.println("[Ensamblador] Error al intentar apagar el contenedor: " + e.getMessage());
        }
    }
}