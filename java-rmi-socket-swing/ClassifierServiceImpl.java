import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class ClassifierServiceImpl extends UnicastRemoteObject implements ClassifierService {

    // URL du serveur Flask
    private static final String FLASK_SERVER_URL = "http://127.0.0.1:5000/";

    protected ClassifierServiceImpl() throws RemoteException {
        super();
    }

    @Override
    public String classifyImage(String imagePath) throws RemoteException {
        try {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                return "Erreur : Fichier introuvable";
            }

            // Ouvrir la connexion HTTP vers Flask
            URL url = new URL(FLASK_SERVER_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            String boundary = "----WebKitFormBoundary" + UUID.randomUUID().toString();
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            
            try (OutputStream outputStream = conn.getOutputStream()) {
                // Debut du corps de la requête avec le boundary
                String boundaryPrefix = "--" + boundary;
                String lineEnd = "\r\n";
            
                // En-tête pour le fichier
                outputStream.write((boundaryPrefix + lineEnd).getBytes(StandardCharsets.UTF_8));
                outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + imageFile.getName() + "\"" + lineEnd).getBytes(StandardCharsets.UTF_8));
                outputStream.write(("Content-Type: image/jpeg" + lineEnd).getBytes(StandardCharsets.UTF_8));
                outputStream.write(lineEnd.getBytes(StandardCharsets.UTF_8));
            
                // Envoi du fichier image
                try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            
                // Fin du fichier et du corps de la requête
                outputStream.write((lineEnd + boundaryPrefix + "--" + lineEnd).getBytes(StandardCharsets.UTF_8));
            }
            

            // Lire la reponse du serveur Flask
            int responseCode = conn.getResponseCode();

            System.err.println("responseCode = " + responseCode);
            if (responseCode == 200) {
                // Lire le resultat (prediction)

                try (InputStream inputStream = conn.getInputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    StringBuilder response = new StringBuilder();
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        response.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
                    }
                    return response.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "Erreur lors de la lecture de la reponse : " + e.getMessage();
                }


            } else {
                return "Erreur du serveur Flask : " + responseCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur : " + e.getMessage();
        }
    }
}
