package behaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.List;

import static utils.UtilidadesDF.buscarServicio;

public class CalcularUrgenciaBehaviour extends CyclicBehaviour {

    private int examenesEsperados = -1;
    private final List<String[]> examenesRecibidos = new ArrayList<>();
    private final List<String> conversationIds = new ArrayList<>();

    public CalcularUrgenciaBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        // Filtramos para recibir solo mensajes de tipo REQUEST
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage mensaje = myAgent.receive(mt);

        if (mensaje != null) {
            String contenido = mensaje.getContent();
            System.out.println("AgenteUrgencia recibio petición de " + mensaje.getSender().getLocalName() + " -> " + contenido);

            try {
                String[] partes = contenido.split(",");
                examenesEsperados = Integer.parseInt(partes[0].trim());

                // Almacenamos los datos para procesarlos cruzados cuando los tengamos todos
                examenesRecibidos.add(partes);
                conversationIds.add(mensaje.getConversationId());

                // Nos aseguramos de haber recibido todos los exámenes de la planificación
                if (examenesRecibidos.size() == examenesEsperados) {
                    System.out.println("AgenteUrgencia: Todos los exámenes recibidos (" + examenesEsperados + "). Procesando solapamientos temporales...");

                    for (int i = 0; i < examenesRecibidos.size(); i++) {
                        String[] p = examenesRecibidos.get(i);
                        String cid = conversationIds.get(i);

                        String asignatura = p[1].trim();
                        double notaDeseada = Double.parseDouble(p[2].trim());
                        int diasRestantes = Integer.parseInt(p[3].trim());

                        // COMPROBACIÓN DE SOLAPAMIENTO
                        // Un examen el mismo día suma 1.0 a la carga. A medida que se alejan, la carga disminuye hasta 0.
                        double cargaSolapada = 1.0; // Contamos este examen al 100%
                        double margenInfluencia = 7.0; // Un examen a 7 o más días de diferencia ya no añade estrés

                        for (int j = 0; j < examenesRecibidos.size(); j++) {
                            if (i != j) {
                                int otrosDias = Integer.parseInt(examenesRecibidos.get(j)[3].trim());
                                int diffDias = Math.abs(diasRestantes - otrosDias);
                                
                                double contribucion = Math.max(0.0, 1.0 - (diffDias / margenInfluencia));
                                cargaSolapada += contribucion;
                            }
                        }

                        int diasSeguros = Math.max(1, diasRestantes);
                        double factorExigencia = notaDeseada / 5.0;

                        // Cálculo del índice de estrés
                        double estresBase = 10.0 / diasSeguros; 
                        double indiceEstres = estresBase * factorExigencia * cargaSolapada;

                        String prioridad = calcularPrioridad(indiceEstres);
                        System.out.printf("AgenteUrgencia calculo -> Asignatura: %s | Carga Solapada: %.2f | Índice Estrés: %.2f | Prioridad: %s\n", asignatura, cargaSolapada, indiceEstres, prioridad);

                        // Enviar cada mensaje individualmente al ensamblador
                        AID agenteEnsamblador = buscarServicio(myAgent, "ensamblador-plan");
                        if (agenteEnsamblador != null) {
                            ACLMessage msgEnsamblador = new ACLMessage(ACLMessage.INFORM);
                            msgEnsamblador.addReceiver(agenteEnsamblador);
                            msgEnsamblador.setConversationId(cid);
                            msgEnsamblador.setContent("asignatura=" + asignatura + ";prioridad=" + prioridad);
                            myAgent.send(msgEnsamblador);
                        }
                    }

                    // Vaciamos la memoria
                    examenesEsperados = -1;
                    examenesRecibidos.clear();
                    conversationIds.clear();
                }

            } catch (Exception e) {
                System.err.println("AgenteUrgencia: Error al procesar los datos (" + contenido + ")");
                e.printStackTrace();
            }
        } else {
            block();
        }
    }

    private String calcularPrioridad(double indice) {
        if (indice >= 12.0) return "CRÍTICA"; 
        if (indice >= 7.0) return "ALTA";    
        if (indice >= 3.5) return "MEDIA";   
        return "BAJA";                       
    }
}