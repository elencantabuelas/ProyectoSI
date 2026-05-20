package ontologia;

import jade.content.Concept;

public class Examen implements Concept
{
    private String asignatura;
    private int creditos;
    private int dificultad;
    private int horasEstudio;

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

    public int getHorasEstudio()
    {
        return horasEstudio;
    }

    public void setHorasEstudio(int horasEstudio)
    {
        this.horasEstudio = horasEstudio;
    }
}
