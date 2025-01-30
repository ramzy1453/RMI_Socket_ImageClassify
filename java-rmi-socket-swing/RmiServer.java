import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RmiServer {
    public static void main(String[] args) {
        try {
            // Demarrer le registre RMI
            Registry registry = LocateRegistry.createRegistry(1099);
            
            // Instancier l'objet distant
            ClassifierServiceImpl service = new ClassifierServiceImpl();
            
            // Publier l'objet dans le registre
            registry.rebind("ClassifierService", service);

            System.out.println("Serveur RMI demarre...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
