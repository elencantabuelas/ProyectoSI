package ontologia;

import jade.content.Concept;

public class Examen implements Concept
{
    private String asignatura;
    private int creditos;
    private int dificultad;
    private int diasAntesExamen;

    public Examen()
    {
    }

    public String getAsignatura()
    {
        return asignatura;
    }

    public void setAsignatura(String asignatura)
    {
        this.asignatura = asignatura;
    }

    public int getCreditos()
    {
        return creditos;
    }

    public void setCreditos(int creditos)
    {
        this.creditos = creditos;
    }

    public int getDificultad()
    {
        return dificultad;
    }

    public void setDificultad(int dificultad)
    {
        this.dificultad = dificultad;
    }

    public int getDiasAntesExamen()
    {
        return diasAntesExamen;
    }

    public void setDiasAntesExamen(int diasAntesExamen)
    {
        this.diasAntesExamen = diasAntesExamen;
    }
}
