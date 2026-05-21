package behaviours;

import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import ontologia.Examen;
import ontologia.SolicitarPlanificacion;

import java.util.Scanner;

public class EnviarExamenBehaviour extends OneShotBehaviour
{
    private static final String NOMBRE_COORDINADOR = "AgenteCoordinador";

    private final Codec codec;
    private final Ontology ontologia;

    public EnviarExamenBehaviour(Agent agente, Codec codec, Ontology ontologia)
    {
        super(agente);
        this.codec = codec;
        this.ontologia = ontologia;
    }

    @Override
    public void action()
    {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Cuantos examenes quieres introducir: ");
        int totalExamenes = Integer.parseInt(scanner.nextLine());

        for (int i = 1; i <= totalExamenes; i++)
        {
            System.out.println("Introduce los datos del examen " + i + ":");

            Examen examen = pedirDatosExamen(scanner);
            enviarExamen(examen);
        }
    }

    private Examen pedirDatosExamen(Scanner scanner)
    {
        Examen examen = new Examen();

        System.out.print("Asignatura: ");
        examen.setAsignatura(scanner.nextLine());

        System.out.print("Creditos: ");
        examen.setCreditos(Integer.parseInt(scanner.nextLine()));

        System.out.print("Dificultad(1-10): ");
        examen.setDificultad(Integer.parseInt(scanner.nextLine()));

        System.out.print("Dias que quedan antes del examen: ");
        examen.setDiasAntesExamen(Integer.parseInt(scanner.nextLine()));

        return examen;
    }

    private void enviarExamen(Examen examen)
    {
        try
        {
            AID coordinador = new AID(NOMBRE_COORDINADOR, AID.ISLOCALNAME);

            SolicitarPlanificacion solicitud = new SolicitarPlanificacion();
            solicitud.setExamen(examen);

            ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);
            mensaje.addReceiver(coordinador);
            mensaje.setLanguage(codec.getName());
            mensaje.setOntology(ontologia.getName());

            myAgent.getContentManager().fillContent(mensaje, new Action(coordinador, solicitud));
            myAgent.send(mensaje);

            System.out.println("Examen enviado al AgenteCoordinador mediante ontologia.");
        }
        catch (Exception e)
        {
            System.out.println("Error al enviar el examen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
