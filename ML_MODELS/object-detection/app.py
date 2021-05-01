# -*- coding: utf-8 -*-
"""
Created on Sat May  1 05:20:41 2021

@author: Ishal
"""
from flask import Flask, request, jsonify
import os
from werkzeug.utils import secure_filename

from yolo import process
from datetime import datetime
from random import randint


app = Flask(__name__)
uploads_dir = "C:/Users/Ishal/Object detection/instance/uploads"
output_dir = "C:/Users/Ishal/Object detection/instance/output"


@app.route('/upload/', methods=['POST'])
def upload_image():
    file = request.files['file']
    if not file:
        return {'error': 'Missing file'}, 400
    
    now = datetime.now()
    filename = now.strftime("%Y%m%d_%H%M%S") + "_" + str(randint(000, 999))
    file.save(os.path.join(uploads_dir, secure_filename(filename + '.jpg')))
    objects_count, objects_confidence = process(uploads_dir, output_dir, filename)
    
    response = {
        'objects_count': objects_count, 
        'objects_confidence': objects_confidence, 
    }

    return jsonify({"data": response}), 200


if __name__ == '__main__':
    app.run(debug=True)