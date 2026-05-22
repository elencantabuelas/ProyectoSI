package utils;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class UtilidadesDF {

    //métod estático para buscar el servicio
    public static AID buscarServicio(Agent miAgente, String tipoServicio) {

        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipoServicio);
        template.addServices(sd);

        try {
            // miAgente ejecuta el métod
            DFAgentDescription[] result = DFService.search(miAgente, template); //filtro estrícto, distingue entre mayusculas
            if (result.length > 0) {
                return result[0].getName(); // devolvemos el primero
            } else {
                System.out.println(miAgente.getLocalName() + " [ERROR]: No encontró el servicio: " + tipoServicio);
            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        return null;
    }

    // para registrar Servicios y ahorrar código
    //le pasamos el agente creado, el servicio que va a ejercer y su nombre
    public static void registrarServicio(Agent miAgente, String tipoServicio, String nombreServicio) {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(miAgente.getAID());

        ServiceDescription sd = new ServiceDescription();
        sd.setType(tipoServicio); //tipo de servicio guardado
        sd.setName(nombreServicio); //nombre del servicio
        dfd.addServices(sd);

        try {
            DFService.register(miAgente, dfd); //registrado
            System.out.println(miAgente.getLocalName() + " registrado en el DF con el servicio: " + tipoServicio);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}