import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiClient {
    public static String main(String[] args) {
        if (args.length == 0) {
            System.err.println("Veuillez fournir le chemin de l'image en argument.");
            return "Veuillez fournir le chemin de l'image en argument";
        }

        try {
            // Se connecter au registre RMI
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Recuperer le service distant
            ClassifierService service = (ClassifierService) registry.lookup("ClassifierService");

            // Recuperer le chemin de l'image depuis les arguments
            String imagePath = args[0];

            System.out.println("Chemin de l'image: " + imagePath);

            // Envoyer l'image au serveur RMI et recuperer la prediction
            String result = service.classifyImage(imagePath);
            
            
            return result;
        } catch (java.rmi.NotBoundException e) {
            System.err.println("Service not bound: " + e.getMessage());
            return "Service not bound";
        } catch (java.rmi.RemoteException e) {
            System.err.println("Remote exception: " + e.getMessage());
            return "Remote exception";
        }  
    }
}
