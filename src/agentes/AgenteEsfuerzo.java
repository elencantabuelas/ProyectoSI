package agentes;

import behaviours.CalcularEsfuerzoBehaviour;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;

import static utils.UtilidadesDF.registrarServicio;

public class AgenteEsfuerzo extends Agent {

    @Override
    protected void setup() {
        System.out.println("AgenteEsfuerzo iniciado: " + getLocalName());

        // Arreglado el tipo de servicio para coincidir con el del Coordinador y usando funcion de UtilidadesDF
        registrarServicio(this, "calculo-esfuerzo", "servicio-esfuerzo");

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
}
