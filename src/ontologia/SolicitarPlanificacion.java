package ontologia;

import jade.content.AgentAction;

public class SolicitarPlanificacion implements AgentAction {

    private Examen examen;

    public SolicitarPlanificacion()
    {
    }

    public Examen getExamen()
    {
        return examen;
    }

    public void setExamen(Examen examen)
    {
        this.examen = examen;
    }
}
