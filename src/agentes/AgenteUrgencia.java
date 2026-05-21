package agentes;

import behaviours.CalcularUrgenciaBehaviour;
import jade.core.Agent;

public class AgenteUrgencia extends Agent {

    @Override
    protected void setup() {
        System.out.println("AgenteUrgencia iniciado: " + getLocalName());

        // Añadimos el comportamiento externo
        addBehaviour(new CalcularUrgenciaBehaviour(this));
    }
}