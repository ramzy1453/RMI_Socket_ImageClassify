import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class SocketClient {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 5001;

    public static String main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java SocketClient <image_path>");
            System.exit(1);
        }
        File imageFile = new File(
            args[0]
        ); // Change le chemin vers ton image

        try (Socket socket = new Socket(SERVER_IP, SERVER_PORT);
             OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream();
             FileInputStream fileInputStream = new FileInputStream(imageFile)) {

            byte[] imageBytes = new byte[(int) imageFile.length()];
            fileInputStream.read(imageBytes);

            // Envoyer la taille de l'image en premier (4 octets - int)
            outputStream.write(ByteBuffer.allocate(4).putInt(imageBytes.length).array());
            outputStream.flush();

            // Envoyer l'image
            outputStream.write(imageBytes);
            outputStream.flush();
            System.out.println("Image envoyee au serveur.");

            // Lire la reponse du serveur
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String response = reader.readLine();
            
            return response;

        } catch (IOException e) {

            return e.getMessage();
        }
    }
}
