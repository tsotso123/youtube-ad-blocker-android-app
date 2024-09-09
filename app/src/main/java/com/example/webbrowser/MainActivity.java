package com.example.webbrowser;


import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.Manifest;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    WebView browser;
    private static final int REQUEST_CODE = 1;
    RelativeLayout rootLayout;

    String temp_url;
    public void setBrowser()
    {
        WebSettings mSettings = browser.getSettings();
        mSettings.setJavaScriptEnabled(true);

        mSettings.setDomStorageEnabled(true);
        mSettings.setSaveFormData(true);
        mSettings.setMediaPlaybackRequiresUserGesture(false);
        mSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        browser.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Mobile Safari/537.36");
        //String desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36";
        //browser.getSettings().setUserAgentString(desktopUserAgent);

        // Prevent the YouTube app from launching
        browser.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
                String url = request.getUrl().toString();

                // Check if the URL is a YouTube URL
                if (url.contains("youtube.com") || url.contains("youtu.be")) {
                    // Load the YouTube URL within the WebView
                    view.loadUrl(url);
                    return false;
                }

                // Allow other URLs to be handled normally
                return super.shouldOverrideUrlLoading(view, request);
            }
            @Override
            public void onLoadResource(WebView view, String url) {

                System.out.println("URL:"+url);
                // to change, if url is not youtube.com, and doesnt contain "searchquery" and its not a sign in page, and if it contains "youtube.com" its probably a video
                if (url.contains("detailpage")&&url.contains("youtube.com"))
                {

                    try {
                        Button download = findViewById(R.id.download);
                        download.setVisibility(View.VISIBLE);
                    }
                    catch (Exception e)
                    {

                    }
                }





//                browser.evaluateJavascript(
//                        "document.addEventListener('click', function(event) {\n" +
//                        "    // Overriding preventDefault()\n" +
//                        "    Object.defineProperty(event, 'defaultPrevented', { get: function() { return false; } });\n" +
//                        "});\n",null);
//                browser.evaluateJavascript(
//                        "document.getElementsByClassName('ytp-ad-skip-button-modern ytp-button')[0].addEventListener('click', function(event) {\n" +
//                        "    event.stopImmediatePropagation(); // Stops immediate propagation\n" +
//                        "});\n",null);




                // was - //have Ads at 16 times video speed
                // now - have ads video be at end, automatically skipping
                browser.evaluateJavascript(
                        "element = document.getElementsByClassName('ytp-ad-player-overlay-progress-bar')[0];\n" +
                                "if (typeof(element) != 'undefined' && element != null)\n" +
                                "{\n" +
                                "    videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];\n" +
                                "    videoElement.currentTime = videoElement.duration;\n" +
//                                "    videoElement.playbackRate = 16;\n" +
//                                "    window.addEventListener('click', function (event) {\n" +
//                                "      // (note: not cross-browser)\n" +
//                                "      var event2 = new CustomEvent('click2', {detail: {original: event}});\n" +
//                                "      event.target.dispatchEvent(event2);\n" +
//                                "      event.stopPropagation();\n" +
//                                "    }, true);\n"+
//                                "   document.getElementsByClassName('ytp-ad-skip-button-modern ytp-button')[0].click();\n"+
                                "}",null);
                browser.evaluateJavascript(
                        "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
                                "videoElement.muted = false;"+
                                "videoElement.play();",null);

//                browser.evaluateJavascript(
//                        "setTimeout(function() {\n" +
//                                "    // Your code to execute after 0.4 seconds\n" +
//                                "    console.log(\"Executing after 0.4 seconds\");\n" +
//                                "    // Example: Changing the background color after 0.4 seconds\n" +
//                                "    document.getElementsByClassName('ytp-ad-skip-button-modern ytp-button')[0].click();\n" +
//                                "}, 400);\n",null);

                // click skip:
                //document.getElementsByClassName('ytp-skip-ad-button')[0].click()


            }
        });

        //setting the javascript interface
        browser.addJavascriptInterface(new WebAppInterface(this), "Android");

        browser.setWebChromeClient(new WebChromeClient());
        // Handle file downloads
        browser.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    //downloadFile("video",url,browser.getSettings().getUserAgentString());
                }
            }
        });

    }
    private final AudioManager.OnAudioFocusChangeListener audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    // Resume playback
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:
                    // Stop playback
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                    // Pause playback
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    // Lower the volume
                    break;
            }
        }
    };
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        // this function is being called by the js
        @JavascriptInterface
        public void downloadBlob(String base64Data, String filename) {
            try {
                byte[] data = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File file = new File(path, filename);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(data);
                    fos.flush();
                }
                // Trigger a media scan to make the file visible in the Downloads app
                MediaScannerConnection.scanFile(mContext, new String[]{file.toString()}, null, null);
                Toast.makeText(mContext, "File downloaded: " + filename, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(mContext, "Download failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
//    private void downloadFile(String url) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse(url));
//        startActivity(intent);
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted! Try downloading again.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;

    //
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupAudioFocus() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .build();

        audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(false)
                .setWillPauseWhenDucked(false)
                .setOnAudioFocusChangeListener(audioFocusChangeListener)
                .build();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            setupAudioFocus();
//        }


        setContentView(R.layout.activity_main);
//        browser = findViewById(R.id.browser);
//        setBrowser();
//        browser.loadUrl("www.youtube.com");

        rootLayout = findViewById(R.id.rootLayout);

        // Start the WebViewService
        Intent serviceIntent = new Intent(this, WebViewService.class);
        startService(serviceIntent);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                {
                    if (WebViewService.browser != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                rootLayout.addView(WebViewService.browser);
                                browser = WebViewService.browser;
                                setBrowser();
                            }
                        });
                        break;
                    }
                }
            }
        }).start();


//        new AudioManager.OnAudioFocusChangeListener() {
//            @Override
//            public void onAudioFocusChange(int focusChange) {
//                System.out.println("");
//            }
//        };

        Button download = findViewById(R.id.download);
        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.vid_url_to_download = browser.getUrl();
                //start DownloadService 1  - putExtra("index",i);
                //start DownloadService 2 ..
                //etc

//                browser.evaluateJavascript(
//                        "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
//                                "videoElement.muted = true;"+
//                                "videoElement.pause();",null);

                File cacheDir = getApplicationContext().getCacheDir();
                try {
                    listFilesRecursively(cacheDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
//                if (cacheDir.isDirectory()) {
//                    for (File file : cacheDir.listFiles()) {
//                        // Perform operations on cache files, such as listing or deleting
//                        for (int i =0;i<file.listFiles().length;i++)
//                        {
//                            System.out.println("Cache file: " + file.listFiles()[i]+" last mod:"+file.listFiles()[i].lastModified());
//                            if (file.listFiles()[i].listFiles()!=null)
//                            {
//                                for (int j=0;j<file.listFiles()[i].listFiles().length;j++)
//                                {
//                                    System.out.println("    --Cache file: " + file.listFiles()[i].listFiles()[j]);
//                                    if (file.listFiles()[i].listFiles()[j].listFiles()!=null)
//                                    {
//                                        for (int j=0;j<file.listFiles()[i].listFiles()[j].length;j++)
//                                        {
//                                            System.out.println("    --Cache file: " + file.listFiles()[i].listFiles()[j]);
//
//                                        }
//                                    }
//                                }
//                            }
//
//
//                        }
//
//
//
//                    }
//                }


//                int services_to_start = 1;
//                for (int i =0;i<services_to_start;i++)
//                {
//                    Intent serviceIntent = new Intent(MainActivity.this, DownloadService.class);
//                    serviceIntent.putExtra("index",i);
//                    startService(serviceIntent);
//                }




//                browser.evaluateJavascript(
//                        "// Select the video element\n" +
//                                "const videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];\n" +
//                                "\n" +
//                                "// Array to store recorded chunks\n" +
//                                "let recordedChunks = [];\n" +
//                                "\n" +
//                                "// Create a MediaStream from the video element\n" +
//                                "const stream = videoElement.captureStream();\n" +
//                                "\n" +
//                                "// Create a MediaRecorder instance with the stream\n" +
//                                "const mediaRecorder = new MediaRecorder(stream,{\n" +
//                                "    audioBitsPerSecond : 5000000, // 128kbps audio bitrate\n" +
//                                "    videoBitsPerSecond : 2500000, // 2.5Mbps video bitrate\n" +
//                                "});\n" +
//                                "\n" +
//                                "// Event to handle when data is available\n" +
//                                "mediaRecorder.ondataavailable = function(event) {\n" +
//                                "  if (event.data.size > 0) {\n" +
//                                "    \n" +
//                                "      try {\n" +
//                                "      recordedChunks.push(event.data);\n" +
//                                "    } catch (error) {\n" +
//                                "      console.error(error);\n" +
//                                "      // Expected output: ReferenceError: nonExistentFunction is not defined\n" +
//                                "      // (Note: the exact output may be browser-dependent)\n" +
//                                "    }\n" +
//                                "\n" +
//                                "  }\n" +
//                                "};\n" +
//                                "videoElement.onratechange = null;\n" +
//                                "videoElement.playbackRate = 1;\n" +
//                                "videoElement.play();\n" +
//                                "// Start recording\n" +
//                                "mediaRecorder.start();\n" +
//                                "\n" +
//                                "console.log('Recording started...');\n" +
//                                "\n" +
//                                "// Stop recording after 10 seconds (adjust as needed)\n" +
//                                "setTimeout(function() {\n" +
//                                "    //videoElement.pause();\n" +
//                                "    videoElement.currentTime = videoElement.duration-2;\n" +
//                                "    setTimeout(function () {\n" +
//                                "          mediaRecorder.stop();\n" +
//                                "          console.log('Recording stopped.');\n" +
//                                "        \n" +
//                                "          // Download the recorded video\n" +
//                                "          const blob = new Blob(recordedChunks, { type: 'video/webm' });\n" +
//                                "          const url = URL.createObjectURL(blob);\n" +
//
//                                //this is to handle blob as file
//                                "const reader = new FileReader();\n" +
//                                "            reader.onloadend = function() {\n" +
//                                "                const base64Data = reader.result.split(',')[1]; // Split to get base64 string\n" +
//                                "                Android.downloadBlob(base64Data, 'vid.webmp');\n" +
//                                "            };\n" +
//                                "            reader.readAsDataURL(blob);\n" +
//                                "    return;"+
//                                //
//
//                                "        \n" +
//                                "          const a = document.createElement('a');\n" +
//                                "          a.style.display = 'none';\n" +
//                                "          a.href = url;\n" +
//                                "          a.download = 'recorded-video.webm';\n" +
//                                "          document.body.appendChild(a);\n" +
//                                "          \n" +
//                                "          a.click();\n" +
//                                "        \n" +
//                                "          // Cleanup\n" +
//                                "          setTimeout(function() {\n" +
//                                "            document.body.removeChild(a);\n" +
//                                "            window.URL.revokeObjectURL(url);\n" +
//                                "          }, 100);\n" +
//                                "    },3000);\n" +
//                                "  \n" +
//                                "}, 10000);  // Stop recording after 10 seconds (adjust as needed)",null);
                if (true)
                {
                    return;
                }
                // Attach the WebView from the service to the activity's layout

                browser.evaluateJavascript(
                        "// Select the video element\n" +
                                "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];\n" +
                                "\n" +
                                "// Array to store recorded chunks\n" +
                                "recordedChunks = [];\n" +
                                "\n" +
                                "// Create a MediaStream from the video element\n" +
                                "stream = videoElement.captureStream();\n" +
                                "\n" +
                                "// Create a MediaRecorder instance with the stream\n" +
                                "mediaRecorder = new MediaRecorder(stream,{\n" +
                                "    audioBitsPerSecond : 5000000, // 128kbps audio bitrate\n" +
                                "    videoBitsPerSecond : 2500000, // 2.5Mbps video bitrate\n" +
                                "});\n" +
                                "\n" +
                                "// Event to handle when data is available\n" +
                                "mediaRecorder.ondataavailable = function(event) {\n" +
                                "  if (event.data.size > 0) {\n" +
                                "    recordedChunks.push(event.data);\n" +
                                "  }\n" +
                                "};\n" +
                                "videoElement.onratechange = null;\n" +
                                "videoElement.playbackRate = 1;\n" +
                                "videoElement.play();\n" +
                                "videoElement.currentTime = 0;\n"+
                                "// Start recording\n" +
                                "mediaRecorder.start();\n" +
                                "\n" +
                                "console.log('Recording started...');\n" +
                                "\n" +
                                "// Stop recording after 10 seconds (adjust as needed)\n" +
                                "setTimeout(function() {\n" +
                                //"  videoElement.currentTime = videoElement.duration;"+
                                "  mediaRecorder.stop();\n" +
                                "  console.log('Recording stopped.');\n" +
                                "\n" +
                                "  // Download the recorded video\n" +
                                "  const blob = new Blob(recordedChunks, { type: 'video/webm' });\n" +
                                "  const url = URL.createObjectURL(blob);\n" +

                                //this is to handle blob as file
                                "const reader = new FileReader();\n" +
                                "            reader.onloadend = function() {\n" +
                                "                const base64Data = reader.result.split(',')[1]; // Split to get base64 string\n" +
                                "                Android.downloadBlob(base64Data, 'vid.webmp');\n" +
                                "            };\n" +
                                "            reader.readAsDataURL(blob);\n" +
                                "    return;"+
                                //

                                "\n" +
                                "  const a = document.createElement('a');\n" +
                                "  a.style.display = 'none';\n" +
                                "  a.href = url;\n" +
                                "  a.download = 'recorded-video.webm';\n" +
                                "  document.body.appendChild(a);\n" +
                                "  \n" +
                                "window.addEventListener('click', function (event) {\n" +
                                "    // (note: not cross-browser)\n" +
                                "    var event2 = new CustomEvent('click2', {detail: {original: event}});\n" +
                                "    event.target.dispatchEvent(event2);\n" +
                                "    event.stopPropagation();\n" +
                                "    }, true);"+
                                "  a.click();\n" +
                                "\n" +
                                "  // Cleanup\n" +
                                "  setTimeout(function() {\n" +
                                "    document.body.removeChild(a);\n" +
                                "    window.URL.revokeObjectURL(url);\n" +
                                "  }, 100);\n" +
                                "},10000 );  // Stop recording after 10 seconds (adjust as needed)",null);
            }//videoElement.duration/1*1000+1000
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back is pressed... Finishing the activity
                browser.goBack();
            }
        });
    }
    private void listFilesRecursively(File directory) throws IOException {
        File targetDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "files");
        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        if (directory != null && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // If the file is a directory, call the method recursively
                        listFilesRecursively(file);
                    } else {
                        // If the file is a file, log its path
                        System.out.println("File: " + file.getAbsolutePath());
                        copyFile(file, new File(targetDir, file.getName()));

                    }
                }
            }
        }
    }
    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        try (FileInputStream in = new FileInputStream(sourceFile); FileOutputStream out = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        // probably better to reload and put vid cur time to last

        /////
//        if (temp_url==null )
//        {
//            System.out.println(temp_url+" :browser: "+browser.getUrl());
//            temp_url = browser.getUrl();
//
////            browser.evaluateJavascript(
////                    "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
////                            "videoElement.muted = false;"+
////                            "videoElement.play();"+
////                            "window.addEventListener('visibilitychange', function (event) {\n" +
////                            "    // (note: not cross-browser)\n" +
////                            "    var event2 = new CustomEvent('visibilitychange2', {detail: {original: event}});\n" +
////                            "    event.target.dispatchEvent(event2);\n" +
////                            "    event.stopImmediatePropagation();\n" +
////                            "    }, true);"+
////                            "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
////                            "videoElement.muted = false;"+
////                            "videoElement.play();",null);
//
//            System.out.println("URL REAL:"+browser.getUrl());
//            browser.evaluateJavascript(
//                    "setInterval(function() {\n" +
//                            "window.addEventListener('visibilitychange', function (event) {\n" +
//                            "    // (note: not cross-browser)\n" +
//                            "    var event2 = new CustomEvent('visibilitychange2', {detail: {original: event}});\n" +
//                            "    event.target.dispatchEvent(event2);\n" +
//                            "    event.stopImmediatePropagation();\n" +
//                            "    }, true);"+
//                            "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
//                            "videoElement.muted = false;"+
//                            "videoElement.play();" +
//                            "}, 100);",null);
//
//            System.out.println("listeners");
//
//        }
//        else
//        {
//
//        }
        /////

        //browser.reload();

//        browser.evaluateJavascript(
//                "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
//                        "videoElement.muted = false;"+
//                        "videoElement.play();",null);

        //browser.evaluateJavascript("scroll(0,2000);",null);
        System.out.println("DID ");
//        browser.post(new Runnable() {
//            @Override
//            public void run() {
//                browser.evaluateJavascript(
//                        "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
//                                "videoElement.muted = false;"+
//                                "videoElement.play();",null);
//                //browser.evaluateJavascript("scroll(0,2000);",null);
//                System.out.println("DID ");
//            }
//        });


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//
//                try {
//                    browser.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            browser.evaluateJavascript(
//                                    "videoElement = document.getElementsByClassName('video-stream html5-main-video')[0];"+
//                                            "videoElement.muted = false;"+
//                                            "videoElement.play();",null);
//                            //browser.evaluateJavascript("scroll(0,2000);",null);
//                            System.out.println("DID ");
//                        }
//                    });
//
//                }
//                catch (Exception e)
//                {
//                    throw e;
//                }
//
//            }
//        }).start();

    }
}
