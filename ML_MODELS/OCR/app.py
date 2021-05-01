# -*- coding: utf-8 -*-
"""
Created on Sat May  1 15:06:20 2021

@author: Ishal
"""
from flask import Flask, request, jsonify
import os
from werkzeug.utils import secure_filename
from easyocr import Reader
import cv2
from datetime import datetime
from random import randint

app = Flask(__name__)
uploads_dir = "C:/Users/Ishal/OCR/instance/uploads"
output_dir = "C:/Users/Ishal/OCR/instance/output"


@app.route('/upload/', methods=['POST'])
def upload_image():
    file = request.files['file']
    if not file:
        return {'error': 'Missing file'}, 400
    
    now = datetime.now()
    filename = now.strftime("%Y%m%d_%H%M%S") + "_" + str(randint(000, 999))
    file.save(os.path.join(uploads_dir, secure_filename(filename + '.jpg')))
    images = cv2.imread(uploads_dir + '/' + filename + '.jpg')
   
    reader = Reader(['en'])
    results = reader.readtext(images)
    
    text_array = []
    confidence_level = []
    for result in results:
        text_array.append(result[1])
        confidence_level.append(result[2])
        
    response = {
        'text': text_array, 
        'confidence': confidence_level, 
    }

    return jsonify({"data": response}), 200


if __name__ == '__main__':
    app.run(debug=True)
