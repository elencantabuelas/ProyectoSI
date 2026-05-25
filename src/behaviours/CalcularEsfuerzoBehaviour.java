package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import static utils.UtilidadesDF.buscarServicio;

/**
 * Fórmula: horasBase = creditos × 10; horas = horasBase × dificultad × nota
 *
 * Factor dificultad:
 *   1-3  → × 0.5
 *   4-6  → × 1.0
 *   7-9  → × 1.5
 *   10   → × 2.0
 *
 * Factor nota deseada:
 *   5-6  → × 1.0
 *   7-8  → × 1.25
 *   9-10 → × 1.5
 */

public class CalcularEsfuerzoBehaviour extends CyclicBehaviour {

    private static final int HORAS_POR_CREDITO = 10;
    private static final String SERVICIO_ENSAMBLADOR = "ensamblador-plan";

    private static final MessageTemplate MT =
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

    public CalcularEsfuerzoBehaviour(Agent agente) {
        super(agente);
    }

    @Override
    public void action() {
        ACLMessage mensaje = myAgent.receive(MT);

        if (mensaje != null) {
            String contenido = mensaje.getContent();
            String convId    = mensaje.getConversationId();

            if (convId == null || !convId.startsWith("calculo-")) {
                return;
            }

            try {
                // Formato: "totalExamenes,asignatura,notaDeseada,creditos,dificultad"
                String[] partes    = contenido.split(",");
                int totalExamenes  = Integer.parseInt(partes[0].trim());
                String asignatura  = partes[1].trim();
                double notaDeseada = Double.parseDouble(partes[2].trim());
                double creditos    = Double.parseDouble(partes[3].trim());
                int dificultad     = Integer.parseInt(partes[4].trim());

                int horas = calcularHoras(creditos, dificultad, notaDeseada);

                AID ensamblador = buscarServicio(myAgent, SERVICIO_ENSAMBLADOR);
                if (ensamblador != null) {

                    //envio aparte del total de examenes
                    ACLMessage msgTotal = new ACLMessage(ACLMessage.INFORM);
                    msgTotal.addReceiver(ensamblador);
                    msgTotal.setContent("totalExamenes=" + totalExamenes);
                    myAgent.send(msgTotal);

                    ACLMessage msgEnsamblador = new ACLMessage(ACLMessage.INFORM);
                    msgEnsamblador.addReceiver(ensamblador);
                    msgEnsamblador.setContent("asignatura=" + asignatura + ";horas=" + horas);
                    msgEnsamblador.setConversationId(convId);
                    myAgent.send(msgEnsamblador);

                } else {
                    System.err.println("AgenteEsfuerzo: No se encontró el AgenteEnsamblador en el DF.");
                }

            } catch (Exception e) {
                System.err.println("AgenteEsfuerzo: Error al procesar mensaje -> " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            block();
        }
    }

    private int calcularHoras(double creditos, int dificultad, double notaDeseada) {
        double horasBase = creditos * HORAS_POR_CREDITO;

        // Factor por dificultad
        double factorDificultad;
        if (dificultad <= 3) {
            factorDificultad = 0.5;
        } else if (dificultad <= 6) {
            factorDificultad = 1.0;
        } else if (dificultad <= 9) {
            factorDificultad = 1.5;
        } else {
            factorDificultad = 2.0;
        }

        // Factor por nota deseada
        double factorNota;
        if (notaDeseada <= 6) {
            factorNota = 1.0;
        } else if (notaDeseada <= 8) {
            factorNota = 1.25;
        } else {
            factorNota = 1.5;
        }

        return (int) Math.round(horasBase * factorDificultad * factorNota);
    }
}
