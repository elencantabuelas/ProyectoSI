package behaviours;

import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import ontologia.Examen;
import ontologia.SolicitarPlanificacion;
import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.OneShotBehaviour;

import java.util.List;

import static utils.UtilidadesDF.buscarServicio;

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
                List<Examen> examenes = solicitud.getListaExamenes().getExamenes();

                int totalExamenes = examenes.size();


                //comportamiento paralelo
                ParallelBehaviour pb = new ParallelBehaviour(myAgent, ParallelBehaviour.WHEN_ALL);
                pb.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        //envio a AgenteEsfuerzo
                        AID agenteEsfuerzo = buscarServicio(myAgent, "calculo-esfuerzo");
                        if (agenteEsfuerzo != null) {

                            for (Examen examen : examenes) {
                                ACLMessage msgEsfuerzo = new ACLMessage(ACLMessage.REQUEST);
                                msgEsfuerzo.addReceiver(agenteEsfuerzo);
                                String contenido = totalExamenes + "," +
                                        examen.getAsignatura() + "," +
                                        examen.getNotaDeseada() + "," +
                                        examen.getCreditos() + "," +
                                        examen.getDificultad();

                                msgEsfuerzo.setContent(contenido);
                                msgEsfuerzo.setConversationId("calculo-" + examen.getAsignatura());

                                myAgent.send(msgEsfuerzo);

                            }

                        }
                    }
                });

                pb.addSubBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action() {
                        //envio a AgenteUrgencia
                        AID agenteUrgencia = buscarServicio(myAgent, "calculo-urgencia");
                        if(agenteUrgencia != null){
                            for (Examen examen : examenes) {
                                ACLMessage msgUrgencia = new ACLMessage(ACLMessage.REQUEST);
                                msgUrgencia.addReceiver(agenteUrgencia);
                                String contenido = totalExamenes + "," +
                                                    examen.getAsignatura() + "," +
                                                    examen.getNotaDeseada() + "," +
                                                    examen.getDiasAntesExamen();

                                msgUrgencia.setContent(contenido);
                                msgUrgencia.setConversationId("calculo-" + examen.getAsignatura());

                                myAgent.send(msgUrgencia);

                                }
                            }
                        }
                    });

                myAgent.addBehaviour(pb);

            }catch (Exception e) {
                System.err.println("coordinador no pudo obtener el mensaje: " + e.getMessage());
                e.printStackTrace();

            }
        }
        else{
            block();
        }

    }
}
