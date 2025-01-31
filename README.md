# Hand gestures Classification with Flask and Java RMI/Socket

This project is designed to demonstrate an image classification pipeline using multiple components. It includes a Jupyter notebook for training a machine learning model, a Flask app for creating the server-side logic, and a Java-based client interface using Swing, RMI, and Socket for interacting with the server.

## Project Structure

The project is divided into the following folders:

1. **Jupyter Notebook**: Contains the Jupyter notebook used for image classification.
2. **Flask App**: Contains the Flask app which serves as the backend for handling image classification requests.
3. **Java RMI/Socket Interface**: Includes the Java client code for interacting with the Flask server using RMI (Remote Method Invocation) and Socket communication. The Java client uses Swing for the GUI.

---

## Requirements

Before running the project, make sure you have the following installed:

### 1. **Python Requirements**:

- Python 3.x (Recommended version: 3.8 or above)
- Install the dependencies listed in the `requirements.txt` file:

```bash
pip install -r requirements.txt
```

`requirements.txt` includes:

- Flask: Core web framework
- Flask-WTF: Form handling extension for Flask
- WTForms: Required by Flask-WTF for form handling
- TensorFlow: For machine learning and image classification
- NumPy: For array and matrix operations
- OpenCV: For image processing

### 2. **Java Requirements**:

- Java 8 or later
- Apache Maven (Optional if you are building dependencies)

---

## Flask App

### Purpose:

The Flask app handles incoming image classification requests and provides an API to interact with the machine learning model.

### Run Flask App:

1. In the terminal, navigate to the Flask app directory and run:

```bash
python app.py
```

The server will start on `http://127.0.0.1:5000/`.

### Flask Routes:

- **`/`**: Handles the image upload, processes it, and returns the classification result.

### Flask Form:

The Flask app uses a simple form with:

- **FileField**: Allows the user to select and upload an image.
- **SubmitField**: Submits the image for classification.

---

## Image Classification Notebook (Jupyter)

### Purpose:

This Jupyter notebook is used for training a machine learning model to classify images. The model is built using TensorFlow and is later used by the Flask app for classification.

### Steps:

1. Load and preprocess image data.
2. Build a neural network model using TensorFlow.
3. Train the model with labeled data (you can modify the dataset as needed).
4. Save the trained model for later use.

### To run the notebook:

1. Open the notebook in Jupyter or JupyterLab.
2. Run the cells in order to preprocess the data, train the model, and save it as a `.h5` file.

---

## Java RMI/Socket Client

### Purpose:

This component provides a GUI for the user to select an image and send it to the Flask app for classification. The interface uses either **RMI** (Remote Method Invocation) or **Socket** for communication between the Java client and the Flask server.

### RMI:

RMI is a Java API that allows objects to communicate over a network. In this project, the RMI client sends image paths to the Flask server for classification.

#### Steps to Run:

1. **Start the RMI server**:
   - Run the RMI server class to expose the service.
   - Start the RMI registry on port `1099`.
2. **Run the Java client**:
   - The client will prompt for an image file path.
   - The image will be sent to the Flask app for classification.

### Socket:

The Socket client sends the image file directly over a TCP/IP socket to the server and receives the classification result in response.

#### Steps to Run:

1. Start the Socket server on port `5001`.
2. The client sends the image as byte data to the server, which processes it and returns the result.

---

## Running the Project

### Step 1: Train the Image Classification Model (Optional)

If you have not already trained the model, you can run the Jupyter notebook to train and save the model. Make sure to save the trained model as `model.h5` and place it in the `flask_app/` directory.

### Step 2: Run the Flask Server

In the `flask_app/` directory, run the Flask app:

```bash
python app.py
```

This starts the server, which listens for image classification requests.

### Step 3: Run the Java Client (RMI or Socket)

- **RMI**: Use the `RmiClient.java` file to send a path to the image to the Flask server for classification.
- **Socket**: Use the `SocketClient.java` file to send an image file directly to the Flask server for classification.

Before running the Java client, you need to compile the Java files. Use the following command in the terminal to compile all Java files:

```bash
javac -target 1.8 -source 1.8 *.java
start rmiregistry 1099
java RmiServer
java SocketServer
```

The Socket server and the RMI server should be on two separate terminals

You can use either RMI or Socket communication depending on your choice in the Java Swing interface.

---
