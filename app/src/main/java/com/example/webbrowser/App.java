package com.example.webbrowser;
import android.Manifest;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.Build;
import android.os.Environment;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;

public class App extends Application {
    public static String vid_url_to_download;
    public static WebView webView;
    @Override
    public void onCreate() {
        super.onCreate();

    }

    // javascript interface class

    public static void setBrowser(WebView browser)
    {
        WebSettings mSettings = browser.getSettings();
        mSettings.setJavaScriptEnabled(true);

        mSettings.setDomStorageEnabled(true);
        mSettings.setSaveFormData(true);
        mSettings.setMediaPlaybackRequiresUserGesture(false);
        mSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);


        //browser.setOnSystemUiVisibilityChangeListener();
        browser.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Mobile Safari/537.36");
        //String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";
        //browser.getSettings().setUserAgentString(desktopUserAgent);



        browser.setWebChromeClient(new WebChromeClient());

    }
}
