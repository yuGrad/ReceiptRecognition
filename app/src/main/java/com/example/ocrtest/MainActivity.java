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
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    TextRecognizer recognizer;
    Uri uri;
    ImageView imageView;
    InputImage inputImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recognizer  = TextRecognition.getClient(new KoreanTextRecognizerOptions.Builder().build());
        Button imageUploadButton = findViewById(R.id.image_upload_button);
        imageView = findViewById(R.id.image_view);

        imageUploadButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityResult.launch(intent);
        });
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        uri = result.getData().getData();
                        Log.d("TAG", uri.toString());
                        try {
                            inputImage = InputImage.fromFilePath(MainActivity.this, uri);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imageView.setImageBitmap(bitmap);

                            getTextProcessResult(inputImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    void getTextProcessResult(InputImage inputImage){
        Task<Text> result = recognizer.process(inputImage)
                .addOnSuccessListener(visionText -> {
                    // Task completed successfully
                    Log.d("TAG", "" +
                            "\n" +visionText.getText());
//                    int i = 0;
//                    for (Text.TextBlock block : visionText.getTextBlocks()) {
//                        String blockText = block.getText();
//                        Log.d("TAG", i + ": " + blockText);
//                    }

                })
                .addOnFailureListener(
                        e -> {
                            // Task failed with an exception
                            Log.d("TAG", "Why?", e);
                        });
    }
}