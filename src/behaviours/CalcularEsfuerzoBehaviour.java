package behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CalcularEsfuerzoBehaviour extends CyclicBehaviour {

    private static final int HORAS_POR_CREDITO = 10;

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
                double creditosExamen = Double.parseDouble(partes[0].trim());
                int dificultad = Integer.parseInt(partes[1].trim());

                int horas = calcularHoras(creditosExamen, dificultad);

                System.out.println("Asignatura:" + asignatura + " créditosExamen=" + creditosExamen + " dificultad=" + dificultad + " → horas recomendadas=" + horas);

                ACLMessage respuesta = mensaje.createReply();
                respuesta.setPerformative(ACLMessage.INFORM);
                respuesta.setContent(String.valueOf(horas));
                respuesta.setConversationId(convId);
                myAgent.send(respuesta);

                System.out.println("INFORM enviado al Coordinador" + " convId=" + convId + ", horas=" + horas);

            } catch (Exception e) {
                System.err.println("Error al procesar mensaje -> " + e.getMessage());
                e.printStackTrace();
            }

        } else {
            block();
        }
    }

    private int calcularHoras(double creditosExamen, int dificultad) {
        double horasBase = creditosExamen * HORAS_POR_CREDITO;

        double multiplicador;
        if (dificultad <= 3) {
            multiplicador = 0.5;
        } else if (dificultad <= 6) {
            multiplicador = 1.0;
        } else if (dificultad <= 9) {
            multiplicador = 1.5;
        } else {
            multiplicador = 2.0;
        }

        return (int) Math.round(horasBase * multiplicador);
    }
}
