package ontologia;

import jade.content.Concept;

public class Examen implements Concept
{
    private String asignatura;
    private double creditos;
    private int dificultad;
    private int diasAntesExamen;
    private double notaDeseada;

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

    public double getCreditos()
    {
        return creditos;
    }

    public void setCreditos(double creditos)
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

    public double getNotaDeseada()
    {
        return notaDeseada;
    }

    public void setNotaDeseada(double notaDeseada)
    {
        this.notaDeseada = notaDeseada;
    }

}
