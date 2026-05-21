package agentes;

import behaviours.CalcularEsfuerzoBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

public class AgenteEsfuerzo extends Agent {

    @Override
    protected void setup() {
        System.out.println("AgenteEsfuerzo iniciado: " + getLocalName());

        registrarServicioDF();

        addBehaviour(new CalcularEsfuerzoBehaviour(this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println("AgenteEsfuerzo: terminando.");
    }

    private void registrarServicioDF() {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            ServiceDescription sd = new ServiceDescription();
            sd.setType("servicio-esfuerzo");
            sd.setName("servicio-esfuerzo");
            dfd.addServices(sd);

            DFService.register(this, dfd);
            System.out.println("AgenteEsfuerzo: registrado en el DF.");
        } catch (FIPAException e) {
            System.err.println("AgenteEsfuerzo: error al registrar en el DF -> " + e.getMessage());
            doDelete();
        }
    }
}
