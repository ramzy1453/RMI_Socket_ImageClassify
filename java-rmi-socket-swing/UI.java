import java.awt.*;
import java.io.File;
import javax.swing.*;

public class UI extends JFrame {
    private JLabel selectedFileLabel;
    private JLabel imageLabel;
    private File selectedFile;
    private JRadioButton socketButton, rmiButton;

    public UI() {
        setTitle("Upload Image & Choose Method");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        // Initialize components
        JButton uploadButton = new JButton("Choisir une image");
        selectedFileLabel = new JLabel("Aucune image selectionnee.");
        imageLabel = new JLabel();
        socketButton = new JRadioButton("Socket", true);
        rmiButton = new JRadioButton("RMI");

        ButtonGroup methodGroup = new ButtonGroup();
        methodGroup.add(socketButton);
        methodGroup.add(rmiButton);

        JButton sendButton = new JButton("Envoyer");
        sendButton.addActionListener(e -> processImage());

        uploadButton.addActionListener(e -> chooseImage());

        // Add components to the frame
        add(uploadButton);
        add(selectedFileLabel);
        add(imageLabel);
        add(socketButton);
        add(rmiButton);
        add(sendButton);

        setVisible(true);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            selectedFileLabel.setText("Selectionnee: " + selectedFile.getName());

            // Display image preview
            ImageIcon icon = new ImageIcon(selectedFile.getPath());
            Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
        }
    }

    private void processImage() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Veuillez selectionner une image.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String method = socketButton.isSelected() ? "Socket" : "RMI";
        JOptionPane.showMessageDialog(this, "L'image sera envoyee via " + method + ".", "Information", JOptionPane.INFORMATION_MESSAGE);

        String imagePath = selectedFile.getAbsolutePath();

        // If the method is Socket, send via Socket
        if ("Socket".equals(method)) {
            sendImageSocket(imagePath);
        } else if ("RMI".equals(method)) {
            sendImageRMI(imagePath);
        }
    }

    private void sendImageSocket(String imagePath) {
        // Add logic here for Socket communication
        System.out.println("Sending image via Socket: " + imagePath);
       
        try {
            // Call the Socket client with the image path
            String[] args = new String[]{imagePath};
            String result = SocketClient.main(args);
    
            // Show the result in a popup
            JOptionPane.showMessageDialog(this, "Resultat de la classification: " + result, "Resultat", JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException e) {
            // Handle any exceptions that might occur
            JOptionPane.showMessageDialog(this, "Erreur Socket: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendImageRMI(String imagePath) {
        try {
            // Call the RMI client with the image path
            String[] args = new String[]{imagePath};
            String result = RmiClient.main(args);
    
            // Show the result in a popup
            JOptionPane.showMessageDialog(this, "Resultat de la classification: " + result, "Resultat", JOptionPane.INFORMATION_MESSAGE);
        } catch (HeadlessException e) {
            // Handle any exceptions that might occur
            JOptionPane.showMessageDialog(this, "Erreur RMI: " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        new UI();
    }
}
