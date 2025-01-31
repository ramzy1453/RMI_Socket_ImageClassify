from flask import Flask, render_template, request, jsonify
from flask_wtf import FlaskForm
from wtforms import FileField, SubmitField
import os
from wtforms.validators import InputRequired
import tensorflow as tf
import numpy as np
import cv2
import threading
from concurrent.futures import ThreadPoolExecutor

app = Flask(__name__)
app.config['SECRET_KEY'] = 'supersecretkey'
app.config['UPLOAD_FOLDER'] = 'static'

model = tf.keras.models.load_model('ressources/SignLanguage.h5')

executor = ThreadPoolExecutor(max_workers=4)  # Adjust max_workers based on your server's capacity

alphabet_map = {i: chr(65 + i) for i in range(26)}  

class UploadFileForm(FlaskForm):
    file = FileField("File", validators=[InputRequired()])
    submit = SubmitField("Upload File")

def preprocess_image(image_path):
    try:
        image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE) 
        image = cv2.resize(image, (28, 28))  
        image = np.expand_dims(image, axis=-1)
        image = np.expand_dims(image, axis=0) 
        return image
    except Exception as e:
        print(f"Error preprocessing image: {e}")
        return None

def classify_image(file_path):
    img_array = preprocess_image(file_path)
    if img_array is None:
        return "Error: Unable to preprocess image"
    
    predictions = model.predict(img_array)
    predicted_class = np.argmax(predictions, axis=1)[0]
    return alphabet_map.get(predicted_class, "Unknown")

@app.route('/', methods=['GET', 'POST'])
def home():
    print(f"Processing on Thread ID: {threading.get_ident()}")
    with open('log.txt', 'a') as f:
        f.write(f"Processing on Thread ID: {threading.get_ident()}\n")
    
    if request.method == 'POST':
        file = request.files.get('file')
        if file:
            file_path = os.path.join(app.config['UPLOAD_FOLDER'], file.filename)
            file.save(file_path)

            predicted_alphabet = classify_image(file_path)

            return predicted_alphabet
    
    return 'Please send a POST request with an image file.'

if __name__ == '__main__':
    os.makedirs(app.config['UPLOAD_FOLDER'], exist_ok=True)
    app.run(debug=True, threaded=True)
