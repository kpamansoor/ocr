package com.google.android.gms.samples.vision.ocrreader;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.util.Locale;

public class DetailTextActivity extends AppCompatActivity {

    private EditText tvText;
    private MaterialFancyButton btnCopy, btnPlay, btnGoogle, btnCall, btnWhatsApp, btnEmail, btnShare;
    private String text;
    private TextToSpeech tts;
    private LinearLayout llBut1;
    Context mActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_text);

        initialize();
        new CheckForEmailAndPhone().execute();
        // ca-app-pub-1243068719441957/7927748298
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        tvText.setText(text);

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(DetailTextActivity.this, "Text copied to clipboad", Toast.LENGTH_SHORT).show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", tvText.getText());
                clipboard.setPrimaryClip(clip);
            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts.speak(tvText.getText(), TextToSpeech.QUEUE_ADD, null, "DEFAULT");
            }
        });
        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://api.whatsapp.com/send?phone="+tvText.getText()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("http://www.google.com/#q=" + tvText.getText()); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT < 23) {
                    phoneCall();
                }else {

                    if (ActivityCompat.checkSelfPermission(mActivity,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                        phoneCall();
                    }else {
                        final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
                        //Asking request Permissions
                        ActivityCompat.requestPermissions(DetailTextActivity.this, PERMISSIONS_STORAGE, 9);
                    }
                }
            }


        });

        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
                i.setType("text/Message");
                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{tvText.getText().toString()});
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(mActivity, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(android.content.Intent.EXTRA_SUBJECT,"OCR Camera");
                i.putExtra(android.content.Intent.EXTRA_TEXT, tvText.getText());
                startActivity(Intent.createChooser(i,"Share via"));
            }
        });


    }

    private class CheckForEmailAndPhone extends AsyncTask<String, Void, String> {
        boolean mobile = false,email = false;
        @Override
        protected String doInBackground(String... params) {


            if(android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                email = true;
            }
            if(Patterns.PHONE.matcher(text).matches()){
                mobile = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(mobile){
                llBut1.setWeightSum(2);
                btnCall.setVisibility(View.VISIBLE);
                btnWhatsApp.setVisibility(View.VISIBLE);
            }
            if(email){
                llBut1.setWeightSum(1);
                btnEmail.setVisibility(View.VISIBLE);
            }
            if(mobile && email){
                llBut1.setWeightSum(3);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean permissionGranted = false;
        switch(requestCode){
            case 9:
                permissionGranted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                break;
        }
        if(permissionGranted){
            phoneCall();
        }else {
            Toast.makeText(mActivity, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private void phoneCall() {
        if (ActivityCompat.checkSelfPermission(mActivity,
                Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+text));
            mActivity.startActivity(callIntent);
        }else{
            Toast.makeText(mActivity, "You don't assign permission.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initialize() {
        mActivity = DetailTextActivity.this;
        text = getIntent().getExtras().getString("text");

        tvText = (EditText) findViewById(R.id.tvText);
        btnCopy = (MaterialFancyButton) findViewById(R.id.btn_copy);
        btnPlay = (MaterialFancyButton) findViewById(R.id.btn_play);
        btnGoogle = (MaterialFancyButton) findViewById(R.id.btn_google);
        btnCall = (MaterialFancyButton) findViewById(R.id.btn_call);
        btnWhatsApp = (MaterialFancyButton) findViewById(R.id.btn_whatsapp);
        btnEmail = (MaterialFancyButton) findViewById(R.id.btn_email);
        btnShare = (MaterialFancyButton) findViewById(R.id.btn_share);

        llBut1 = (LinearLayout) findViewById(R.id.llBut2);

        TextToSpeech.OnInitListener listener =
                new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(final int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            Log.d("OnInitListener", "Text to speech engine started successfully.");
                            tts.setLanguage(Locale.US);
                        } else {
                            Log.d("OnInitListener", "Error starting the text to speech engine.");
                        }
                    }
                };

        tts = new TextToSpeech(this.getApplicationContext(), listener);
    }
}
