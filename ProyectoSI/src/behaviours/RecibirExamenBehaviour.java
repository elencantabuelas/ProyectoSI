package behaviours;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ontologia.Examen;
import ontologia.SolicitarPlanificacion;
import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.OneShotBehaviour;

public class RecibirExamenBehaviour extends CyclicBehaviour {

    private final Codec codec;
    private final Ontology ontologia;

    private static final String NOMBRE_ESFUERZO = "AgenteEsfuerzo";
    private static final String NOMBRE_URGENCIA = "AgenteUrgencia";

    public RecibirExamenBehaviour(Agent agente, Codec codec, Ontology ontologia)
    {
        super(agente);
        this.codec = codec;
        this.ontologia = ontologia;
    }


    @Override
    public void action() {

        //solo recibirá mensajes tipo Request y que usen la ontología nuestra
        MessageTemplate mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST), //request
                MessageTemplate.MatchOntology(ontologia.getName()) //ontologia
        );

        ACLMessage mensaje = myAgent.receive(mt); //se recibe el mensaje

        if(mensaje != null){
            try {
                //extraemos la ontologia (el mensaje)
                ContentElement ce = myAgent.getContentManager().extractContent(mensaje);
                //extraemos la solicitud de la ontologia
                SolicitarPlanificacion solicitud = (SolicitarPlanificacion) ((Action) ce).getAction();
                Examen examen = solicitud.getExamen();

                //comportamiento paralelo
                ParallelBehaviour pb = new ParallelBehaviour(myAgent, ParallelBehaviour.WHEN_ALL);
                pb.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        //envio a AgenteEsfuerzo
                        ACLMessage msgEsfuerzo = new ACLMessage(ACLMessage.REQUEST);
                        msgEsfuerzo.addReceiver(new AID(NOMBRE_ESFUERZO, AID.ISLOCALNAME));

                        //le enviamos creditos y dificultad
                        String contenido = examen.getCreditos() + "," + examen.getDificultad();
                        msgEsfuerzo.setContent(contenido);

                        // como habrá más conversaciones con el agente le pongo un ID a la conversación, para encontrarlo después
                        msgEsfuerzo.setConversationId("calculo-" + examen.getAsignatura());

                        myAgent.send(msgEsfuerzo);
                    }
                });
                pb.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        //envio a AgenteUrgencia
                        ACLMessage msgUrgencia = new ACLMessage(ACLMessage.REQUEST);
                        msgUrgencia.addReceiver(new AID(NOMBRE_URGENCIA, AID.ISLOCALNAME));

                        //le enviamos los dias que faltan
                        String contenido = String.valueOf(examen.getDiasAntesExamen());
                        msgUrgencia.setContent(contenido);

                        msgUrgencia.setConversationId("calculo-" + examen.getAsignatura());

                        myAgent.send(msgUrgencia);
                    }
                });

                myAgent.addBehaviour(pb);

            }catch (Exception e) {
                System.out.println("coordinador no pudo obtener el mensaje: " + e.getMessage());
                e.printStackTrace();

            }
        }
        else{
            block();
        }


    }
}
