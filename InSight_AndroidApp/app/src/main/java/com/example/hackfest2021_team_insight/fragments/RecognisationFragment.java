package com.example.hackfest2021_team_insight.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.extensions.HdrImageCaptureExtender;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

import com.example.hackfest2021_team_insight.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RecognisationFragment extends Fragment {

    private final int REQUEST_CODE_PERMISSIONS = 1001;
    private final int REQ_CODE = 100;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final String fragName = "Recognition";
    LayoutInflater inflater;
    View v;
    ImageView cancel, btSpeak, btPause;
    EditText etText;
    private PreviewView mPreviewView;
    private ImageView captureImage, captureImage1, captureImage2;
    private ImageView previewImage;
    private CardView capture1, capture2;
    private String imageURL;
    private ProgressBar progressBar;
    private TextToSpeech textToSpeech;
    private ImageView assistant;
    private TextView textViewResults;

    public RecognisationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        // Inflate the layout for this fragment
        mPreviewView = view.findViewById(R.id.camera);
        captureImage = view.findViewById(R.id.captureImg);
        captureImage1 = view.findViewById(R.id.captureImg1);
        captureImage2 = view.findViewById(R.id.captureImg2);
        previewImage = view.findViewById(R.id.preview_image);
        capture1 = view.findViewById(R.id.capture1);
        capture2 = view.findViewById(R.id.capture2);
        progressBar = view.findViewById(R.id.progressBar2);
        assistant = view.findViewById(R.id.assistant);
        textViewResults = view.findViewById(R.id.textViewResults);

        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if (i != TextToSpeech.ERROR) {
                    // To Choose language of speech
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        if (fragName == "Read") {
            captureImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_camera_24));
            capture1.setVisibility(View.GONE);
            capture2.setVisibility(View.GONE);
        } else if (fragName == "Explore") {
            captureImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_baseline_camera_24));
            captureImage1.setImageDrawable(getActivity().getDrawable(R.drawable.ic_explore));
            capture1.setVisibility(View.VISIBLE);
            capture2.setVisibility(View.GONE);
        } else {
            captureImage.setImageDrawable(getActivity().getDrawable(R.drawable.ic_face));
            captureImage1.setImageDrawable(getActivity().getDrawable(R.drawable.ic_color));
            captureImage2.setImageDrawable(getActivity().getDrawable(R.drawable.ic_rupee));
            capture1.setVisibility(View.VISIBLE);
            capture2.setVisibility(View.VISIBLE);
        }

        assistant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                    textToSpeech.speak("Sorry your device not supported", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if ((resultCode == Activity.RESULT_OK) && (data != null)) {
                    ArrayList result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textViewResults.setText((String) result.get(0));
                    textToSpeech.speak(textViewResults.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (allPermissionsGranted()) {
            startCamera(); //start camera if permission has been granted by user
        } else {
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private boolean allPermissionsGranted() {

        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(getContext(), "Permissions not granted by the user.", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    public void startCamera() {

        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {

                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);

                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {

        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .build();

        ImageCapture.Builder builder = new ImageCapture.Builder();

        //Vendor-Extensions (The CameraX extensions dependency in build.gradle)
        HdrImageCaptureExtender hdrImageCaptureExtender = HdrImageCaptureExtender.create(builder);

        // Query if extension is available (optional).
        if (hdrImageCaptureExtender.isExtensionAvailable(cameraSelector)) {
            // Enable the extension if available.
            hdrImageCaptureExtender.enableExtension(cameraSelector);
        }

        final ImageCapture imageCapture = builder
                .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                .build();
        preview.setSurfaceProvider(mPreviewView.getSurfaceProvider());
        cameraProvider.unbindAll();
        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);


        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri tempUri = getImageUri(getActivity(), mPreviewView.getBitmap());
                previewImage.setVisibility(View.VISIBLE);
                mPreviewView.setVisibility(View.GONE);
                previewImage.setImageBitmap(mPreviewView.getBitmap());
                captureImage.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                FirebaseStorage storage = FirebaseStorage.getInstance();
                final StorageReference photosRef = storage.getReference().child("photos/" + new Random().nextInt());

                photosRef.putFile(tempUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        photosRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imageURL = uri.toString();
                                Log.d("IMAGE_URL", imageURL);

                                if (fragName != "Read" && fragName != "Explore") {
                                    faceDetection(imageURL);
                                }
                                //Toast.makeText(getContext(), imageURL, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });

        captureImage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/viz_images"), mDateFormat.format(new Date()) + ".jpg");

                Toast.makeText(getContext(), "Capture successfully", Toast.LENGTH_SHORT).show();
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                FirebaseApp.initializeApp(getContext());
                                Uri tempUri = getImageUri(getActivity(), mPreviewView.getBitmap());
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                final StorageReference photosRef = storage.getReference().child("photos/" + new Random().nextInt());


                                photosRef.putFile(tempUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        photosRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageURL = uri.toString();
                                            }
                                        });
                                    }
                                });

                                if (fragName != "Explore") {
                                    colorDetection(imageURL);
                                }

                                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                                View v = inflater.inflate(R.layout.diolag_layout, null);
                                ImageView cancel = v.findViewById(R.id.imageView4);
                                //get the spinner from the xml.
                                Spinner dropdown = v.findViewById(R.id.spinner);
                                String[] items = new String[]{"Select Language", "English", "Hindi"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                                dropdown.setAdapter(adapter);

                                previewImage.setVisibility(View.VISIBLE);
                                mPreviewView.setVisibility(View.GONE);
                                previewImage.setImageBitmap(mPreviewView.getBitmap());
                                captureImage.setEnabled(false);
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setCancelable(false);
                                alertDialog.setView(v);
                                alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                alertDialog.getWindow().setBackgroundDrawable(null);
                                alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                                alertDialog.show();

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        captureImage.setEnabled(true);
                                        previewImage.setVisibility(View.GONE);
                                        mPreviewView.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });
            }
        });

        captureImage2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                File file = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/viz_images"), mDateFormat.format(new Date()) + ".jpg");

                Toast.makeText(getContext(), "Capture successfully", Toast.LENGTH_SHORT).show();
                ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
                imageCapture.takePicture(outputFileOptions, executor, new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                Uri tempUri = getImageUri(getActivity(), mPreviewView.getBitmap());
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                final StorageReference photosRef = storage.getReference().child("photos/" + new Random().nextInt());


                                photosRef.putFile(tempUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        photosRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageURL = uri.toString();
                                            }
                                        });
                                    }
                                });

                                currencyDetection(imageURL);

                                LayoutInflater inflater = (getActivity()).getLayoutInflater();
                                View v = inflater.inflate(R.layout.diolag_layout, null);
                                ImageView cancel = v.findViewById(R.id.imageView4);
                                //get the spinner from the xml.
                                Spinner dropdown = v.findViewById(R.id.spinner);
                                String[] items = new String[]{"Select Language", "English", "Hindi"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                                dropdown.setAdapter(adapter);

                                previewImage.setVisibility(View.VISIBLE);
                                mPreviewView.setVisibility(View.GONE);
                                previewImage.setImageBitmap(mPreviewView.getBitmap());
                                captureImage.setEnabled(false);
                                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setCancelable(false);
                                alertDialog.setView(v);
                                alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                                alertDialog.getWindow().setBackgroundDrawable(null);
                                alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                                alertDialog.show();

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        captureImage.setEnabled(true);
                                        previewImage.setVisibility(View.GONE);
                                        mPreviewView.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                });
            }
        });
    }

    private void currencyDetection(String imageURL) {
    }

    private void colorDetection(String imageURL) {
    }

    private void faceDetection(String imageU) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    String content = "{\r\"url\":\"" + imageU + "\"\r}";

                    OkHttpClient client = new OkHttpClient();

                    MediaType mediaType = MediaType.parse("application/json");
                    RequestBody body = RequestBody.create(mediaType, content);

                    Request request = new Request.Builder()
                            .url("https://microsoft-face1.p.rapidapi.com/detect?detectionModel=detection_01&returnFaceAttributes=age%2C%20gender&returnFaceId=true&recognitionModel=recognition_01")
                            .post(body)
                            .addHeader("content-type", "application/json")
                            .addHeader("x-rapidapi-key", "d7c524c39cmsh6cdf18b939e46c0p1becb7jsnec525a922f65")
                            .addHeader("x-rapidapi-host", "microsoft-face1.p.rapidapi.com")
                            .build();

                    Response response = client.newCall(request).execute();
                    String jsonData = response.body().string();
                    Log.d("API_RESPONSE", jsonData);

                    JSONArray jj = new JSONArray(jsonData);

                    String resp = "Age: ";

                    for (int i = 0; i < jj.length(); i++) {
                        JSONObject jsonObject = jj.getJSONObject(i);
                        JSONObject jsonObj = jsonObject.getJSONObject("faceAttributes");

                        resp = resp + jsonObj.get("age") + ", Gender: " + jsonObj.get("gender");
                    }

                    if (resp.equals("") || resp.equals("Age: ")) {
                        resp = "No Face detected";
                    }

                    Log.d("RESPONSE", resp);
                    String finalResp1 = resp;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);
                            inflater = (getActivity()).getLayoutInflater();
                            v = inflater.inflate(R.layout.diolag_layout, null);
                            cancel = v.findViewById(R.id.imageView4);
                            etText = v.findViewById(R.id.editTextTextPersonName);
                            btSpeak = v.findViewById(R.id.imageView5);
                            btPause = v.findViewById(R.id.imageView6);
                            etText.setMovementMethod(new ScrollingMovementMethod());
                            etText.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                                    InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                            etText.setText(finalResp1);
                            Spinner dropdown = v.findViewById(R.id.spinner);
                            String[] items = new String[]{"Select Language", "English", "Hindi"};
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                            dropdown.setAdapter(adapter);

                            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setCancelable(false);
                            alertDialog.setView(v);
                            alertDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                            alertDialog.getWindow().setBackgroundDrawable(null);
                            alertDialog.getWindow().setGravity(Gravity.BOTTOM);
                            alertDialog.show();

                            btSpeak.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!etText.getText().equals("")) {
                                        textToSpeech.speak(etText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                }
                            });

                            btPause.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!etText.getText().equals("") && textToSpeech.isSpeaking()) {
                                        textToSpeech.stop();
                                    }
                                }
                            });

                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alertDialog.dismiss();
                                    captureImage.setEnabled(true);
                                    previewImage.setVisibility(View.GONE);
                                    mPreviewView.setVisibility(View.VISIBLE);
                                    textToSpeech.stop();
                                }
                            });
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        Bitmap OutImage = Bitmap.createScaledBitmap(inImage, 800, 800, false);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), OutImage, "Title" + new Random().nextInt(), null);
        return Uri.parse(path);
    }

}