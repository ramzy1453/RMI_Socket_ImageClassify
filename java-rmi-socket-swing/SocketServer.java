import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SocketServer {

    private static final int PORT = 5001; // Port d'ecoute du serveur Java
    private static final String FLASK_SERVER_URL = "http://127.0.0.1:5000/"; // URL de ton serveur Flask

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serveur Java en ecoute sur le port " + PORT + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouvelle connexion reçue de " + clientSocket.getInetAddress());

                // Gerer le client dans un thread separe
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try (
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream()
            ) {
                // Lire la taille de l'image (4 octets - int)
                byte[] sizeBuffer = new byte[4];
                inputStream.read(sizeBuffer, 0, 4);
                int fileSize = ByteBuffer.wrap(sizeBuffer).getInt();

                System.out.println("Taille de l'image reçue: " + fileSize + " octets");

                // Lire l'image exactement à la bonne taille
                byte[] imageData = new byte[fileSize];
                int totalBytesRead = 0;
                while (totalBytesRead < fileSize) {
                    int bytesRead = inputStream.read(imageData, totalBytesRead, fileSize - totalBytesRead);
                    if (bytesRead == -1) break;
                    totalBytesRead += bytesRead;
                }

                System.out.println("Image reçue, envoi au serveur Flask...");

                // Envoyer l'image au serveur Flask
                String flaskResponse = sendToFlask(imageData);

                // Envoyer la reponse de Flask au client
                outputStream.write(flaskResponse.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                System.out.println("Reponse envoyee au client.");

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        private String sendToFlask(byte[] imageData) throws IOException {
            String boundary = "----JavaClientBoundary";
            URL url = new URL(FLASK_SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        
            try (OutputStream outputStream = connection.getOutputStream()) {
                PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), true);
                
                // En-tête multipart pour le fichier
                writer.append("--" + boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"image.jpg\"").append("\r\n");
                writer.append("Content-Type: image/jpeg").append("\r\n\r\n");
                writer.flush();
        
                // Envoyer les donnees de l'image
                outputStream.write(imageData);
                outputStream.flush();
        
                // Fin du formulaire
                writer.append("\r\n").append("--" + boundary + "--").append("\r\n");
                writer.flush();
            }
        
            // Lire la reponse du serveur Flask
            StringBuilder response = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        
            return response.toString();
        }
        
     }
}
