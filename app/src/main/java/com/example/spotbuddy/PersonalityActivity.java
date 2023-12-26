package com.example.spotbuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class PersonalityActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality);

        // Initialize WebView
        WebView webView = findViewById(R.id.personality_web_personality);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl("https://www.16personalities.com/free-personality-test");
    }

    private class MyWebViewClient extends WebViewClient {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // Handle external URLs with ACTION_VIEW intent
            Uri url = request.getUrl();
            if (url.getHost().equals("https://www.16personalities.com/")) {
                return false;
            } else {
                return false;
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            // Check if user has reached desired page
            if (url.contains("https://www.16personalities.com/intj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "INTJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/intp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "INTP");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/entj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ENTJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/entp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ENTP");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/infj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "INFJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/infp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "INFP");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/enfj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ENFJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/enfp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ENFP");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/istj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ISTJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/isfj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ISFJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/estj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ESTJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/esfj-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ESFJ");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/istp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ISTP");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/isfp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ISFP");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/estp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ESTP");
                setResult(RESULT_OK, intent);
                finish();

            }else if (url.contains("https://www.16personalities.com/esfp-personality")) {
                // Redirect user back to original activity
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("personality", "ESFP");
                setResult(RESULT_OK, intent);
                finish();

            }
        }
    }
}

