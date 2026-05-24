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
    private final Map<String, String> diasRestantesRecibidos = new HashMap<>();
    private final Map<String, String> notasDeseadasRecibidas = new HashMap<>();

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

            if (contenido.startsWith("totalExamenes=")) {
                try {
                    totalPlanesEsperados = Integer.parseInt(contenido.split("=")[1]);
                    System.out.println("[Ensamblador] Se esperan " + totalPlanesEsperados + " planes en total.");
                } catch (Exception e) {
                    System.err.println("[Ensamblador] Error al leer el total de exámenes.");
                }
            } else {
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
                    case "diasRestantes": diasRestantesRecibidos.put(conversationId, valor); break;
                    case "notaDeseada": notasDeseadasRecibidas.put(conversationId, valor); break;
                }
            }
        }

        // esperar 5 piezas de datos
        if (asignaturas.containsKey(conversationId)
                && horasRecibidas.containsKey(conversationId)
                && prioridadesRecibidas.containsKey(conversationId)
                && diasRestantesRecibidos.containsKey(conversationId)
                && notasDeseadasRecibidas.containsKey(conversationId)) {
            
            // logica de validacion
            String asignatura = asignaturas.get(conversationId);
            String horasStr = horasRecibidas.get(conversationId);
            String prioridad = prioridadesRecibidas.get(conversationId);
            String diasStr = diasRestantesRecibidos.get(conversationId);
            String notaStr = notasDeseadasRecibidas.get(conversationId);

            int horasNum = Integer.parseInt(horasStr);
            int diasNum = Integer.parseInt(diasStr);
            double notaNum = Double.parseDouble(notaStr);

            StringBuilder planBuilder = new StringBuilder();
            planBuilder.append("Asignatura: ").append(asignatura).append("\n");
            planBuilder.append("  - Horas recomendadas: ").append(horasNum).append("\n");
            planBuilder.append("  - Prioridad: ").append(prioridad).append("\n");

            // Aplicamos la lógica de validación
            if (horasNum > (diasNum * 12) && notaNum > 5) {
                planBuilder.append("    ¡AVISO!: Las horas de estudio son muy altas para los días disponibles.\n");
                planBuilder.append("    Se recomienda bajar la nota deseada para hacer el plan más realista.\n");
            } else if (horasNum > (diasNum * 12) && notaNum <= 5) {
                planBuilder.append("    ¡ALERTA CRÍTICA!: El plan de estudio para esta asignatura es inviable.\n");
                planBuilder.append("    Considere dejar la carrera y postular a una FP.\n");
            }

            planesCompletados.add(planBuilder.toString());
            System.out.println("[Ensamblador] Plan para '" + asignatura + "' completado y añadido a la lista. (" + planesCompletados.size() + "/" + totalPlanesEsperados + ")");

            // Limpiar los mapas para este plan
            asignaturas.remove(conversationId);
            horasRecibidas.remove(conversationId);
            prioridadesRecibidas.remove(conversationId);
            diasRestantesRecibidos.remove(conversationId);
            notasDeseadasRecibidas.remove(conversationId);

            if (totalPlanesEsperados > 0 && planesCompletados.size() >= totalPlanesEsperados) {
                mostrarResultadosFinalesYApagar();
            }
        }
    }

    private void mostrarResultadosFinalesYApagar() {
        StringBuilder sb = new StringBuilder("Se han generado todos los planes de estudio:\n\n");
        for (String plan : planesCompletados) {
            sb.append(plan).append("\n");
        }

        JOptionPane.showMessageDialog(
                null,
                sb.toString(),
                "Planes de Estudio Generados",
                JOptionPane.INFORMATION_MESSAGE
        );

        planesCompletados.clear();
        totalPlanesEsperados = 0;

        try {
            System.out.println("[Ensamblador] Todo el trabajo completado. Apagando la plataforma...");
            myAgent.getContainerController().kill();
            System.exit(0);
        } catch (StaleProxyException e) {
            System.err.println("[Ensamblador] Error al intentar apagar el contenedor: " + e.getMessage());
        }
    }
}