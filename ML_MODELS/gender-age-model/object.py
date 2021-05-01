import cv2
import numpy as np

camera = cv2.VideoCapture(1)
camera.set(10,80)

face_recognition = cv2.CascadeClassifier("C:/Users/Ishal/Documents/Rakuten/Object Detection/stop_data.xml")

while True:
    success, image = camera.read()
    gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    objects = face_recognition.detectMultiScale(gray_image, 
                                   minSize =(20, 20))
    
    for index, (x, y, w, h) in enumerate(objects):
        cv2.rectangle(image, (x,y), (x+w, y+h), (255,255,255),2)
        cv2.putText(image, "Face "+str(index+1), (x,y), cv2.FONT_HERSHEY_COMPLEX, 1, (255,255,255),2)
    
    cv2.imshow("Video", image)
    #out.write(image)
    
    if cv2.waitKey(1) & 0xFF == ord('e'):
        break;
        
#video_cap.release()
#out.release()

cv2.destroyAllWindows()