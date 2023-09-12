package com.example.ocrtest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button imageUploadButton;
    ImageView imageView;
    TextView contentTextView;
    TextView resultTextView;

    InputImage inputImage;
    TextRecognizer recognizer;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위젯 init
        recognizer = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
        imageUploadButton = findViewById(R.id.image_upload_button);
        imageView = findViewById(R.id.image_view);
        contentTextView = findViewById(R.id.contentTextView);
        resultTextView = findViewById(R.id.resultTextView);

        imageUploadButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityResult.launch(intent);
        });
    }

    private void changeView(Bitmap bitmap, String textResult) {
        imageUploadButton.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.VISIBLE);
        contentTextView.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.VISIBLE);

        imageView.setImageBitmap(bitmap);
        resultTextView.setText(textResult);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                uri = result.getData().getData();
                Log.d("TAG", uri.toString());
                try {
                    inputImage = InputImage.fromFilePath(MainActivity.this, uri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                    getTextProcessResult(inputImage, new TextRecognitionCallback() {
                        @Override
                        public void onSuccess(String text) {
                            Log.d("TAG", "\n" + text);
                            changeView(bitmap, text);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.d("TAG", "Why?", e);
                            changeView(bitmap, "텍스트 인식 Error");
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    void getTextProcessResult(InputImage inputImage, TextRecognitionCallback callback) {
        recognizer.process(inputImage).addOnSuccessListener(visionText -> {
            // Task completed successfully
            callback.onSuccess(visionText.getText());

        }).addOnFailureListener(e -> {
            // Task failed with an exception
            callback.onFailure(e);
        });
    }
}