# -*- coding: utf-8 -*-
"""
Created on Sat May  1 20:42:08 2021

@author: Ishal
"""
from flask import Flask, request, jsonify
import os
from werkzeug.utils import secure_filename
import cv2
from datetime import datetime
from random import randint
import numpy as np
import torch
import torch.nn as nn
from torchvision import transforms,models
from PIL import Image

app = Flask(__name__)
uploads_dir = "C:/Users/Ishal/Currency detector/instance/uploads"
output_dir = "C:/Users/Ishal/Currency detector/instance/output"

def get_model(classes=7):
    model = models.resnet50(pretrained=True)
    features = model.fc.in_features
    model.fc = nn.Linear(in_features = features, out_features = classes)
    return model

device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

model = get_model()
model.to(device)
model.load_state_dict(torch.load("C:/Users/Ishal/Currency detector/model_currency (2).pth"))

transform = transforms.Compose([
    transforms.Resize((224,224)),
    transforms.ToTensor(),
    transforms.Normalize(mean=[0.485, 0.456, 0.406],std=[0.229, 0.224, 0.225])
])

@app.route('/upload/', methods=['POST'])
def upload_image():
    file = request.files['file']
    if not file:
        return {'error': 'Missing file'}, 400
    
    now = datetime.now()
    filename = now.strftime("%Y%m%d_%H%M%S") + "_" + str(randint(000, 999))
    file.save(os.path.join(uploads_dir, secure_filename(filename + '.jpg')))
    image = cv2.imread(uploads_dir + '/' + filename + '.jpg')
    img = cv2.cvtColor(image, cv2.COLOR.BGR2RGB)
    img = Image.fromarray(img)
    img = transform(img)
    img = img.unsqueeze(0)
    img = img.to(device, dtype=torch.float)
    output= model(img)
    
    _,predictions = torch.max(output, 1)
    predictions = predictions.detach().cpu().numpy().tolist()
    dict = ['10', '100', '20', '200', '2000', '500', '50']
    
        
    response = {
        'Currency': dict[predictions[0]],
    }

    return jsonify({"data": response}), 200


if __name__ == '__main__':
    app.run(debug=True)
