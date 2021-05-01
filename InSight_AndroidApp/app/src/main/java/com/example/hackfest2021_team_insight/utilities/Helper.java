package com.example.hackfest2021_team_insight.utilities;

import android.speech.tts.TextToSpeech;
import android.widget.ImageView;

public class Helper {
    public static void performClick(ImageView imageView) {
        //initiate the imageView
        imageView.performClick();
        imageView.setPressed(true);
        imageView.invalidate();
        // delay completion till animation completes
        imageView.postDelayed(new Runnable() {  //delay imageView
            public void run() {
                imageView.setPressed(false);
                imageView.invalidate();
                //any other associated action
            }
        }, 800);  // .8secs delay time
    }

    public static void speakOutText(String s, TextToSpeech textToSpeech) {
        textToSpeech.speak(s, TextToSpeech.QUEUE_FLUSH, null);
    }
}
