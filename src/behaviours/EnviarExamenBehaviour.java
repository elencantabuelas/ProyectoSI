package behaviours;

import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import ontologia.Examen;
import ontologia.ListaExamenes;
import ontologia.SolicitarPlanificacion;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import static utils.UtilidadesDF.buscarServicio;

public class EnviarExamenBehaviour extends OneShotBehaviour
{
    private static final String SERVICIO_COORDINADOR = "servicio-coordinador";

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
        int totalExamenes = Integer.parseInt(pedirDato("Cuantos examenes quieres introducir:"));

        List<Examen> examenes = new ArrayList<>();

        for (int i = 1; i <= totalExamenes; i++)
        {
            Examen examen = pedirDatosExamen(i);
            examenes.add(examen);
        }

        enviarExamenes(examenes);
    }

    private Examen pedirDatosExamen(int numeroExamen)
    {
        JTextField campoAsignatura = new JTextField(15);
        JTextField campoCreditos = new JTextField(15);
        JTextField campoPorcentaje = new JTextField(15);
        JTextField campoDificultad = new JTextField(15);
        JTextField campoDias = new JTextField(15);
        JTextField campoNotaDeseada = new JTextField(15);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(panel, gbc, 0, "Asignatura:", campoAsignatura);
        agregarCampo(panel, gbc, 1, "Creditos:", campoCreditos);
        agregarCampo(panel, gbc, 2, "Porcentaje del examen:", campoPorcentaje);
        agregarCampo(panel, gbc, 3, "Dificultad (1-10):", campoDificultad);
        agregarCampo(panel, gbc, 4, "Dias antes del examen:", campoDias);
        agregarCampo(panel, gbc, 5, "Nota deseada:", campoNotaDeseada);

        int opcion = JOptionPane.showConfirmDialog(
                null,
                panel,
                "Datos del examen " + numeroExamen,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (opcion != JOptionPane.OK_OPTION)
        {
            throw new IllegalArgumentException("Entrada cancelada por el usuario.");
        }

        String asignatura = campoAsignatura.getText().trim();

        if (asignatura.isEmpty())
        {
            throw new IllegalArgumentException("La asignatura no puede estar vacia.");
        }

        double creditos = Double.parseDouble(campoCreditos.getText());
        double porcentajeExamen = Double.parseDouble(campoPorcentaje.getText());

        Examen examen = new Examen();
        examen.setAsignatura(asignatura);
        examen.setCreditos(creditos * porcentajeExamen / 100);
        examen.setDificultad(Integer.parseInt(campoDificultad.getText()));
        examen.setDiasAntesExamen(Integer.parseInt(campoDias.getText()));
        examen.setNotaDeseada(Double.parseDouble(campoNotaDeseada.getText()));

        return examen;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JTextField campo)
    {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private String pedirDato(String mensaje)
    {
        String valor = JOptionPane.showInputDialog(null, mensaje, "Datos del examen", JOptionPane.QUESTION_MESSAGE);

        if (valor == null)
        {
            throw new IllegalArgumentException("Entrada cancelada por el usuario.");
        }

        return valor;
    }

    private void enviarExamenes(List<Examen> examenes)
    {
        try
        {
            AID coordinador = buscarServicio(myAgent, SERVICIO_COORDINADOR);

            if (coordinador == null)
            {
                System.out.println("No se pudo enviar la lista de examenes porque no se encontro el agente coordinador en el DF.");
                return;
            }

            ListaExamenes listaExamenes = new ListaExamenes();
            listaExamenes.setExamenes(examenes);

            SolicitarPlanificacion solicitud = new SolicitarPlanificacion();
            solicitud.setListaExamenes(listaExamenes);

            ACLMessage mensaje = new ACLMessage(ACLMessage.REQUEST);
            mensaje.addReceiver(coordinador);
            mensaje.setLanguage(codec.getName());
            mensaje.setOntology(ontologia.getName());

            myAgent.getContentManager().fillContent(mensaje, new Action(coordinador, solicitud));
            myAgent.send(mensaje);

            System.out.println("Lista de " + examenes.size() + " examenes enviada al agente coordinador mediante ontologia.");
        }
        catch (Exception e)
        {
            System.out.println("Error al enviar los examenes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
