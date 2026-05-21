package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;

public class RecibirRespuestasCalculoBehaviour extends CyclicBehaviour {


    //la respuesta de los diferentes Agentes deberemos guardarlas en un mapa ya que nos pueden venir por separado las respuestas de ambas
    //y hasta que la respuesta de los dos sobre una asignatura no sea contestado no le enviaremos nada al AgenteEnsamblador
    private HashMap<String, String> mensajesEsfuerzo = new HashMap<>();
    private HashMap<String, String> mensajesUrgencia = new HashMap<>();

    public RecibirRespuestasCalculoBehaviour(Agent agente) {
        super(agente);
    }

    @Override
    public void action() {
        // FILTRO: Solo escuchamos mensajes INFORM que tengan un ConversationId que empiece por "calculo-"
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage mensaje = myAgent.receive(mt);

        if (mensaje != null) {

            //el id del mensaje de vuelta, debe ser el mismo que enviamos.
            String conversationId = mensaje.getConversationId();
            if (conversationId != null && conversationId.startsWith("calculo-")) {

                //sacamos la asignatura, el sender y el content
                String asignatura = conversationId.replace("calculo-", "");
                String emisor = mensaje.getSender().getLocalName();
                String contenido = mensaje.getContent();

                System.out.println("COORDINADOR: Recibido INFORM de " + emisor + " para " + asignatura + " -> [" + contenido + "]");

                //depende del emisor guardamos en un map u otro
                if (emisor.equals("AgenteEsfuerzo")) {
                    mensajesEsfuerzo.put(asignatura, contenido); //guardaremos las horas que se necesitan
                } else if (emisor.equals("AgenteUrgencia")) {
                    mensajesUrgencia.put(asignatura, contenido); //guardaremos la prioridad
                }

                //cuando tengamos los dos mensajes de respuesta de los agentes de la misma asignatura...
                if (mensajesEsfuerzo.containsKey(asignatura) && mensajesUrgencia.containsKey(asignatura)) {

                    String horas = mensajesEsfuerzo.get(asignatura);
                    String prioridad = mensajesUrgencia.get(asignatura);

                    System.out.println("\n=======================================================");
                    System.out.println("COORDINADOR: ¡Datos completos para " + asignatura.toUpperCase() + "!");
                    System.out.println(" -> Horas necesarias: " + horas);
                    System.out.println(" -> Prioridad: " + prioridad);
                    System.out.println("=======================================================");

                    //le enviamos lo necesario al Ensamblador
                    enviarAlEnsamblador(asignatura, horas, prioridad);

                    mensajesEsfuerzo.remove(asignatura);
                    mensajesUrgencia.remove(asignatura);
                }
            }
        } else {
            block();
        }
    }

    private void enviarAlEnsamblador(String asignatura, String horas, String prioridad) {
        ACLMessage msgEnsamblador = new ACLMessage(ACLMessage.REQUEST);
        msgEnsamblador.addReceiver(new AID("AgenteEnsamblador", AID.ISLOCALNAME));

        // le enviamos al Ensamblador los datos
        String contenidoFinal = asignatura + "," + horas + "," + prioridad;
        msgEnsamblador.setContent(contenidoFinal);
        System.out.println("Enviando al ensamblador: "+contenidoFinal);
        myAgent.send(msgEnsamblador);
    }
}
