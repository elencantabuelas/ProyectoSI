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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

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
        List<Examen> examenes = pedirExamenesConInterfaz();

        if (examenes != null)
        {
            enviarExamenes(examenes);
        }
    }

    private double leerDouble(JTextField campo, String nombreCampo)
    {
        try
        {
            return Double.parseDouble(campo.getText().trim());
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException("El campo " + nombreCampo + " debe ser numerico.");
        }
    }

    private int leerEntero(JTextField campo, String nombreCampo)
    {
        try
        {
            return Integer.parseInt(campo.getText().trim());
        }
        catch (NumberFormatException e)
        {
            throw new NumberFormatException("El campo " + nombreCampo + " debe ser un numero entero.");
        }
    }

    private void validarRango(double valor, String nombreCampo, double minimo, double maximo)
    {
        if (valor < minimo || valor > maximo)
        {
            throw new IllegalArgumentException("El campo " + nombreCampo + " debe estar entre " + minimo + " y " + maximo + ".");
        }
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

    private void enviarExamenes(List<Examen> examenes)
    {
        try
        {
            AID coordinador = buscarServicio(myAgent, SERVICIO_COORDINADOR);

            if (coordinador == null)
            {
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

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private List<Examen> pedirExamenesConInterfaz()
    {
        CountDownLatch cierreFormulario = new CountDownLatch(1);
        AtomicReference<List<Examen>> examenesIntroducidos = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> new FormularioExamenes(cierreFormulario, examenesIntroducidos).mostrar());

        try
        {
            cierreFormulario.await();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return null;
        }

        return examenesIntroducidos.get();
    }

    private class FormularioExamenes
    {
        private final CountDownLatch cierreFormulario;
        private final AtomicReference<List<Examen>> examenesIntroducidos;
        private final List<Examen> examenes = new ArrayList<>();

        private JFrame ventana;
        private JPanel contenido;
        private JTextField campoTotal;
        private JTextField campoAsignatura;
        private JTextField campoCreditos;
        private JTextField campoPorcentaje;
        private JTextField campoDificultad;
        private JTextField campoDias;
        private JTextField campoNotaDeseada;
        private int totalExamenes;
        private int examenActual = 1;

        FormularioExamenes(CountDownLatch cierreFormulario, AtomicReference<List<Examen>> examenesIntroducidos)
        {
            this.cierreFormulario = cierreFormulario;
            this.examenesIntroducidos = examenesIntroducidos;
        }

        void mostrar()
        {
            ventana = new JFrame("Datos del examen");
            ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            ventana.addWindowListener(new java.awt.event.WindowAdapter()
            {
                @Override
                public void windowClosed(java.awt.event.WindowEvent e)
                {
                    cierreFormulario.countDown();
                }
            });

            contenido = new JPanel(new BorderLayout(12, 12));
            contenido.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 16));
            ventana.setContentPane(contenido);

            mostrarPanelTotal();
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        }

        private void mostrarPanelTotal()
        {
            campoTotal = new JTextField(12);
            JPanel panelCampos = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = crearGbc();
            agregarCampo(panelCampos, gbc, 0, "Cuantos examenes quieres introducir:", campoTotal);

            JButton botonCancelar = new JButton("Cancel");
            JButton botonContinuar = new JButton("OK");
            botonCancelar.addActionListener(event -> cancelar());
            botonContinuar.addActionListener(event -> leerTotalExamenes());

            actualizarContenido(panelCampos, crearPanelBotones(botonCancelar, botonContinuar));
            ventana.getRootPane().setDefaultButton(botonContinuar);
        }

        private void leerTotalExamenes()
        {
            try
            {
                totalExamenes = Integer.parseInt(campoTotal.getText().trim());

                if (totalExamenes <= 0)
                {
                    throw new IllegalArgumentException("El numero de examenes debe ser mayor que 0.");
                }

                mostrarPanelExamen();
            }
            catch (NumberFormatException e)
            {
                mostrarError("Numero de examenes no valido: introduce un numero entero.");
            }
            catch (IllegalArgumentException e)
            {
                mostrarError("Numero de examenes no valido: " + e.getMessage());
            }
        }

        private void mostrarPanelExamen()
        {
            campoAsignatura = new JTextField(15);
            campoCreditos = new JTextField(15);
            campoPorcentaje = new JTextField(15);
            campoDificultad = new JTextField(15);
            campoDias = new JTextField(15);
            campoNotaDeseada = new JTextField(15);

            JPanel panelCampos = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = crearGbc();
            agregarCampo(panelCampos, gbc, 0, "Asignatura:", campoAsignatura);
            agregarCampo(panelCampos, gbc, 1, "Creditos:", campoCreditos);
            agregarCampo(panelCampos, gbc, 2, "Porcentaje del examen (1-100):", campoPorcentaje);
            agregarCampo(panelCampos, gbc, 3, "Dificultad (1-10):", campoDificultad);
            agregarCampo(panelCampos, gbc, 4, "Dias antes del examen:", campoDias);
            agregarCampo(panelCampos, gbc, 5, "Nota deseada:", campoNotaDeseada);

            JButton botonCancelar = new JButton("Cancel");
            JButton botonSiguiente = new JButton(examenActual == totalExamenes ? "Enviar" : "Siguiente");
            botonCancelar.addActionListener(event -> cancelar());
            botonSiguiente.addActionListener(event -> guardarExamenActual());

            JLabel titulo = new JLabel("Datos del examen " + examenActual + " de " + totalExamenes);
            JPanel panelCentral = new JPanel(new BorderLayout(8, 8));
            panelCentral.add(titulo, BorderLayout.NORTH);
            panelCentral.add(panelCampos, BorderLayout.CENTER);

            actualizarContenido(panelCentral, crearPanelBotones(botonCancelar, botonSiguiente));
            ventana.getRootPane().setDefaultButton(botonSiguiente);
        }

        private void guardarExamenActual()
        {
            try
            {
                Examen examen = construirExamen();
                examenes.add(examen);

                if (examenActual >= totalExamenes)
                {
                    examenesIntroducidos.set(examenes);
                    ventana.dispose();
                    return;
                }

                examenActual++;
                mostrarPanelExamen();
            }
            catch (NumberFormatException e)
            {
                mostrarError("Datos no validos del examen " + examenActual + ": " + e.getMessage());
            }
            catch (IllegalArgumentException e)
            {
                mostrarError("Datos no validos del examen " + examenActual + ": " + e.getMessage());
            }
        }

        private Examen construirExamen()
        {
            String asignatura = campoAsignatura.getText().trim();

            if (asignatura.isEmpty())
            {
                throw new IllegalArgumentException("La asignatura no puede estar vacia.");
            }

            double creditos = leerDouble(campoCreditos, "creditos");
            double porcentajeExamen = leerDouble(campoPorcentaje, "porcentaje del examen");
            int dificultad = leerEntero(campoDificultad, "dificultad");
            int dias = leerEntero(campoDias, "dias antes del examen");
            double notaDeseada = leerDouble(campoNotaDeseada, "nota deseada");

            validarRango(creditos, "creditos", 0, Double.MAX_VALUE);
            validarRango(porcentajeExamen, "porcentaje del examen", 1, 100);
            validarRango(dificultad, "dificultad", 1, 10);
            validarRango(dias, "dias antes del examen", 0, Integer.MAX_VALUE);
            validarRango(notaDeseada, "nota deseada", 0, 10);

            Examen examen = new Examen();
            examen.setAsignatura(asignatura);
            examen.setCreditos(creditos * porcentajeExamen / 100);
            examen.setDificultad(dificultad);
            examen.setDiasAntesExamen(dias);
            examen.setNotaDeseada(notaDeseada);
            return examen;
        }

        private GridBagConstraints crearGbc()
        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            return gbc;
        }

        private JPanel crearPanelBotones(JButton botonCancelar, JButton botonPrincipal)
        {
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBotones.add(botonCancelar);
            panelBotones.add(botonPrincipal);
            return panelBotones;
        }

        private void actualizarContenido(JPanel panelCampos, JPanel panelBotones)
        {
            contenido.removeAll();
            contenido.add(panelCampos, BorderLayout.CENTER);
            contenido.add(panelBotones, BorderLayout.SOUTH);
            ventana.pack();
            ventana.revalidate();
            ventana.repaint();
        }

        private void mostrarError(String mensaje)
        {
            JOptionPane.showMessageDialog(ventana, mensaje, "Datos no validos", JOptionPane.ERROR_MESSAGE);
        }

        private void cancelar()
        {
            ventana.dispose();
        }
    }
}
