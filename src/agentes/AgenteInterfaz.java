package agentes;

import behaviours.EnviarExamenBehaviour;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import ontologia.OntologiaExamenes;

public class AgenteInterfaz extends Agent
{
    private final Codec codec = new SLCodec();
    private final Ontology ontologia = OntologiaExamenes.getInstance();

    @Override
    protected void setup()
    {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        System.out.println("AgenteInterfaz iniciado: " + getLocalName());

        addBehaviour(new EnviarExamenBehaviour(this, codec, ontologia));
    }
}
