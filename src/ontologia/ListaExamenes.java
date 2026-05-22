package ontologia;

import jade.content.Concept;

import java.util.List;

public class ListaExamenes implements Concept
{
    private List<Examen> examenes;

    public ListaExamenes()
    {
    }

    public List<Examen> getExamenes()
    {
        return examenes;
    }

    public void setExamenes(List<Examen> examenes)
    {
        this.examenes = examenes;
    }
}
