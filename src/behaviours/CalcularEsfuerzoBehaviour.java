package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CalcularEsfuerzoBehaviour extends CyclicBehaviour {

    // Factor de horas de estudio por (crédito × punto de dificultad)
    private static final double FACTOR = 1.5;

    private static final MessageTemplate MT = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);

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

            String asignatura = convId.replace("calculo-", "");

            try {
                String[] partes = contenido.split(",");
                int creditos = Integer.parseInt(partes[0].trim());
                int dificultad = Integer.parseInt(partes[1].trim());
                int horas = calcularHoras(creditos, dificultad);

                System.out.println("AgenteEsfuerzo: [" + asignatura + "]" + " créditos=" + creditos + " dificultad=" + dificultad + " → horas estimadas=" + horas);

                // Responder al Coordinador con INFORM
                ACLMessage respuesta = mensaje.createReply();
                respuesta.setPerformative(ACLMessage.INFORM);
                respuesta.setContent(String.valueOf(horas));
                respuesta.setConversationId(convId);

                myAgent.send(respuesta);

                System.out.println("AgenteEsfuerzo: INFORM enviado al Coordinador" + " [convId=" + convId + ", horas=" + horas + "]");

            } catch (Exception e) {
                System.err.println("AgenteEsfuerzo: error al procesar mensaje -> " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            block();
        }
    }

    /**
     * Fórmula de estimación de horas de estudio:
     *
     * Base: creditos * FACTOR  → horas "base" proporcionales al peso de la asignatura
     * Multiplicador de dificultad: escala 1-10, normalizada a rango [0.5 … 2.0]
     * dificultad=1  → × 0.5  (muy fácil, menos horas)
     * dificultad=5  → × 1.0  (media)
     * dificultad=10 → × 2.0  (muy difícil, el doble)
     *
     * Resultado mínimo garantizado: 1 hora.
     */
    private int calcularHoras(int creditos, int dificultad) {
        // Normalizar dificultad: [1..10] → [0.5..2.0]
        double factorDificultad = 0.5 + (dificultad - 1) * (1.5 / 9.0);

        double horas = creditos * FACTOR * factorDificultad;

        // Redondear al entero más cercano, mínimo 1
        return Math.max(1, (int) Math.round(horas));
    }
}
