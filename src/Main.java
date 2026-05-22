import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class Main {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl();
        p.setParameter(Profile.MAIN_HOST, "localhost");
        p.setParameter(Profile.GUI, "true"); // Para lanzar la GUI de JADE y ver los agentes
        AgentContainer mainContainer = rt.createMainContainer(p);
        System.out.println("Main Container created.");

        try {
            // Lanzar los agentes
            AgentController acCoordinador = mainContainer.createNewAgent("coordinador", "agentes.AgenteCoordinador", null);
            AgentController acEsfuerzo = mainContainer.createNewAgent("esfuerzo", "agentes.AgenteEsfuerzo", null);
            AgentController acUrgencia = mainContainer.createNewAgent("urgencia", "agentes.AgenteUrgencia", null);
            AgentController acEnsamblador = mainContainer.createNewAgent("ensamblador", "agentes.AgenteEnsamblador", null);
            AgentController acInterfaz = mainContainer.createNewAgent("interfaz", "agentes.AgenteInterfaz", null);

            acCoordinador.start();
            acEsfuerzo.start();
            acUrgencia.start();
            acEnsamblador.start();
            acInterfaz.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}