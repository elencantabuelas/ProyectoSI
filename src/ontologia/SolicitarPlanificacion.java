package ontologia;

import jade.content.AgentAction;

public class SolicitarPlanificacion implements AgentAction {

    private ListaExamenes listaExamenes;

    public SolicitarPlanificacion()
    {
    }

    public ListaExamenes getListaExamenes()
    {
        return listaExamenes;
    }

    public void setListaExamenes(ListaExamenes listaExamenes)
    {
        this.listaExamenes = listaExamenes;
    }
}
