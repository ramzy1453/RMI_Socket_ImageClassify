import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClassifierService extends Remote {
    String classifyImage(String imagePath) throws RemoteException;
}
