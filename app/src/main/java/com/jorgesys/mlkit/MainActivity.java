package com.jorgesys.mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.IOException;
import java.util.List;

//https://developers.google.com/ml-kit/vision/face-detection/android?hl=de#java

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MlKit";
    Bitmap defaultBitmap;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defaultBitmap = getImage();
        imageView = findViewById(R.id.imageView);
        try {
            imageView.setImageDrawable(Drawable.createFromStream(getAssets().open("helloween.jpg"),null));
        } catch (IOException e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        final InputImage image = InputImage.fromBitmap(defaultBitmap,0);
        final FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
                Task<List<Face>> result = detector.process(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<Face>>() {
                                            @Override
                                            public void onSuccess(List<Face> faces) {
                                                Log.i(TAG, "onSuccess() faces: " + faces.size());
                                                processFaceList(faces);
                                            }
                                        }
                                ).addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Exception " + e.getMessage());
                                    }
                                }
                        );



            }
        });
    }

    private void processFaceList(List<Face> faces) {
        Bitmap temporaryBitmap = Bitmap.createBitmap(defaultBitmap.getHeight(), defaultBitmap.getWidth(),Bitmap.Config.RGB_565);

        Canvas canvas = new Canvas(temporaryBitmap);
        canvas.drawBitmap(defaultBitmap,0,0,null);
        for (Face face : faces) {
            Rect faceBounds = face.getBoundingBox();
            float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
            float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
            Paint paint = new Paint();
            paint.setColor(Color.GREEN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(3);

            canvas.drawRect(faceBounds,paint);

            Paint rectPaint = new Paint();
            rectPaint.setStrokeWidth(2);
            rectPaint.setColor(Color.CYAN);
            rectPaint.setStyle(Paint.Style.STROKE);

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and nose available):
            Log.i(TAG,"Face similing? " + face.getSmilingProbability());
            for (FaceLandmark faceLandmark : face.getAllLandmarks()) {
                int x = (int) (faceLandmark.getPosition().x);
                int y = (int) (faceLandmark.getPosition().y);
                Log.i(TAG,"Face FaceLandmark: " + getLandMark(faceLandmark.getLandmarkType()) + " , position x: "+ x + ", y: "+ y);


                float radius = 2.0f;
                canvas.drawCircle(x, y, radius, rectPaint);
            }

            
        }
        imageView.setImageBitmap(temporaryBitmap);
    }

    private String getLandMark(int landmarkType){

 switch (landmarkType){
     case 0:
         return "MOUTH_BOTTOM";
     case 1:
         return "LEFT_CHEEK";
     case 2:
         return "2";
     case 3:
         return "LEFT_EAR";
     case 4:
         return "LEFT_EYE";
     case 5:
         return "MOUTH_LEFT";
     case 6:
         return "NOSE_BASE";
     case 7:
         return "RIGHT_CHEEK";
     case 8:
         return "8";
     case 9:
         return "RIGHT_EAR";
     case 10:
         return "RIGHT_EYE";
     case 11:
         return "MOUTH_RIGHT";
     default:
         return "Not defined!";
     }

    }

    public boolean isPortrait = true;

    public boolean isPortrait(){

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        int height = displaymetrics.heightPixels;
        if(width<height){
            isPortrait = true;
        }
        else{
            isPortrait = false;
        }

        return isPortrait;

    }


    public Bitmap getImage(){
        try {
            return BitmapFactory.decodeStream(getAssets().open("helloween.jpg"));
        } catch (IOException e) {
            Log.e("getImage() ", e.getMessage());
        }
        return null;
    }
}