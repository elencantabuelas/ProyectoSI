package ontologia;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;
import jade.content.onto.Ontology;
import vocabulario.Vocabulario;

public class OntologiaExamenes extends BeanOntology implements Vocabulario
{
    private static final Ontology instancia = new OntologiaExamenes();

    public static Ontology getInstance()
    {
        return instancia;
    }

    private OntologiaExamenes()
    {
        super(ONTOLOGY_NAME);

        try
        {
            add(Examen.class);
        }
        catch (BeanOntologyException e)
        {
            e.printStackTrace();
        }
    }
}