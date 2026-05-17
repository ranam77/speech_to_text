package com.ranamustafa.aiconsultion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    Button talk_inapp_btn;
    Button talk_ar_inapp_btn;
    Button talk_standard_system_btn;
    Button talk_ar_standard_system_btn;
    TextView outputtxt;
    ImageView talkimg ;
    Intent recognizerIntent;
    SpeechRecognizer speechRecognizer;

    @Override
    protected void onResume() {
        super.onResume();
        askForPermission();
    }

    private boolean askForPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
            return false;
        } else {
            return true;
        }
    }

    private void startVoiceRecognition(int talktype, SpeechLanguage selectedLanguage) {
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage.getValue());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguage.getValue());
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        if (talktype == 1) {
            if (speechRecognizer != null) {
                try {
                    speechRecognizer.startListening(recognizerIntent);
                } catch (Exception e) {
                }
            }
        } else if (talktype == 2) {
            if (selectedLanguage == SpeechLanguage.ARABIC_EGYPT) {
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث الان بالعربية");
            } else  if (selectedLanguage == SpeechLanguage.ENGLISH_US) {
                recognizerIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Talk now in English");
            }
            try {
                startActivityForResult(recognizerIntent, 2);
            } catch (Exception e) {
                Toast.makeText(this, "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        outputtxt = findViewById(R.id.outputtxt);
        talk_inapp_btn =  findViewById(R.id.talk_inapp_btn);
        talk_ar_inapp_btn = findViewById(R.id.talk_ar_inapp_btn);
        talk_standard_system_btn = findViewById(R.id.talk_standard_system_btn);
        talk_ar_standard_system_btn = findViewById(R.id.talk_ar_standard_system_btn);
        talkimg = findViewById(R.id.talkimg);
        talk_inapp_btn.setOnClickListener(this);
        talk_ar_inapp_btn.setOnClickListener(this);
        talk_standard_system_btn.setOnClickListener(this);
        talk_ar_standard_system_btn.setOnClickListener(this);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
            }

            @Override
            public void onBeginningOfSpeech() {
                outputtxt.setText("");
                talkimg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRmsChanged(float v) {
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
            }

            @Override
            public void onEndOfSpeech() {
                talkimg.setVisibility(View.GONE);
            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle results) {
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                ArrayList<String> matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String partialText = matches.get(0);
                    outputtxt.append(partialText);
                }
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                outputtxt.setText(result.get(0));
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (askForPermission()) {
            if (view == talk_inapp_btn) {
                SpeechLanguage selectedLanguage = SpeechLanguage.ENGLISH_US;
                startVoiceRecognition(1, selectedLanguage);
            } else if (view == talk_ar_inapp_btn) {
                SpeechLanguage selectedLanguage = SpeechLanguage.ARABIC_EGYPT;
                startVoiceRecognition(1, selectedLanguage);
            } else if (view == talk_standard_system_btn) {
                SpeechLanguage selectedLanguage = SpeechLanguage.ENGLISH_US;
                startVoiceRecognition(2, selectedLanguage);
            } else if (view == talk_ar_standard_system_btn) {
                SpeechLanguage selectedLanguage = SpeechLanguage.ARABIC_EGYPT;
                startVoiceRecognition(2, selectedLanguage);
            }
        } else {
            Toast.makeText(this, "Permission Denied Go to Settings to Grant.", Toast.LENGTH_SHORT).show();
        }
    }

    public enum SpeechLanguage {
        ENGLISH_US("en-US", "English (United States)"),
        ENGLISH_UK("en-GB", "English (United Kingdom)"),
        ENGLISH_GENERAL("en", "English (General)"),
        ARABIC_EGYPT("ar-EG", "العربية (مصر)"),
        ARABIC_SAUDI("ar-SA", "العربية (السعودية)"),
        ARABIC_UAE("ar-AE", "العربية (الإمارات)"),
        ARABIC_GENERAL("ar", "العربية (عام)");
        private final String value;
        private final String displayName;
        SpeechLanguage(String value, String displayName) {
            this.value = value;
            this.displayName = displayName;
        }
        public String getValue() {
            return value;
        }
        public String getDisplayName() {
            return displayName;
        }
    }
}
