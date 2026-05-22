package agentes;

import behaviours.CalcularUrgenciaBehaviour;
import jade.core.Agent;

import jade.domain.DFService;
import jade.domain.FIPAException;

import static utils.UtilidadesDF.registrarServicio;

public class AgenteUrgencia extends Agent {

    @Override
    protected void setup() {
        System.out.println("AgenteUrgencia iniciado: " + getLocalName());

        // Usando la clase de utilidades:
        registrarServicio(this, "calculo-urgencia", "servicio-urgencia");

        // Añadimos el comportamiento externo
        addBehaviour(new CalcularUrgenciaBehaviour(this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        System.out.println("AgenteUrgencia: terminando.");
    }
}