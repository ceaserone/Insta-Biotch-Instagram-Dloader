package com.synack.instabiotch;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.URLUtil;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText urlBox;
    private final Set<String> downloadedMediaUrls = new LinkedHashSet<>();
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1
        );

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#111111"));
        root.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(10), dp(8), dp(10), dp(8));
        topBar.setBackgroundColor(Color.parseColor("#181818"));
        topBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        urlBox = new EditText(this);
        urlBox.setHint("Paste Instagram URL...");
        urlBox.setSingleLine(true);
        urlBox.setTextColor(Color.WHITE);
        urlBox.setHintTextColor(Color.parseColor("#AAAAAA"));
        urlBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        urlBox.setPadding(dp(12), dp(10), dp(12), dp(10));

        GradientDrawable urlBg = new GradientDrawable();
        urlBg.setColor(Color.parseColor("#242424"));
        urlBg.setCornerRadius(dp(12));
        urlBg.setStroke(dp(1), Color.parseColor("#353535"));
        urlBox.setBackground(urlBg);

        LinearLayout.LayoutParams urlParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        urlParams.setMargins(0, 0, dp(8), 0);
        urlBox.setLayoutParams(urlParams);

        Button goBtn = new Button(this);
        goBtn.setText("GO");
        goBtn.setTextColor(Color.WHITE);
        goBtn.setAllCaps(false);
        goBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        goBtn.setPadding(dp(14), dp(10), dp(14), dp(10));

        GradientDrawable goBg = new GradientDrawable();
        goBg.setColor(Color.parseColor("#2E2E2E"));
        goBg.setCornerRadius(dp(12));
        goBg.setStroke(dp(1), Color.parseColor("#4A4A4A"));
        goBtn.setBackground(goBg);

        topBar.addView(urlBox);
        topBar.addView(goBtn);

        webView = new WebView(this);
        LinearLayout.LayoutParams webParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        webView.setLayoutParams(webParams);

        LinearLayout bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.CENTER_VERTICAL);
        bottomBar.setPadding(dp(10), dp(8), dp(10), dp(8));
        bottomBar.setBackgroundColor(Color.argb(102, 0, 0, 0));
        bottomBar.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        Button imagesBtn = new Button(this);
        imagesBtn.setText("Images");
        imagesBtn.setTextColor(Color.WHITE);
        imagesBtn.setAllCaps(false);
        imagesBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        Button videosBtn = new Button(this);
        videosBtn.setText("Videos");
        videosBtn.setTextColor(Color.WHITE);
        videosBtn.setAllCaps(false);
        videosBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);

        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(Color.parseColor("#252525"));
        btnBg.setCornerRadius(dp(12));
        btnBg.setStroke(dp(1), Color.parseColor("#4A4A4A"));

        GradientDrawable btnBg2 = new GradientDrawable();
        btnBg2.setColor(Color.parseColor("#252525"));
        btnBg2.setCornerRadius(dp(12));
        btnBg2.setStroke(dp(1), Color.parseColor("#4A4A4A"));

        imagesBtn.setBackground(btnBg);
        videosBtn.setBackground(btnBg2);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        btnParams.setMargins(dp(4), 0, dp(4), 0);

        imagesBtn.setLayoutParams(btnParams);
        videosBtn.setLayoutParams(btnParams);

        bottomBar.addView(imagesBtn);
        bottomBar.addView(videosBtn);

        root.addView(topBar);
        root.addView(webView);
        root.addView(bottomBar);

        setContentView(root);

        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setMediaPlaybackRequiresUserGesture(false);
        ws.setLoadWithOverviewMode(true);
        ws.setUseWideViewPort(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl("https://www.instagram.com");

        goBtn.setOnClickListener(v -> {
            String url = urlBox.getText().toString().trim();
            if (url.isEmpty()) return;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            urlBox.setText(url);
            webView.loadUrl(url);
        });

        imagesBtn.setOnClickListener(v -> downloadImagesFromCurrentPage());
        videosBtn.setOnClickListener(v -> downloadVideosFromCurrentPage());

        handleIncomingIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIncomingIntent(intent);
    }

    private void handleIncomingIntent(Intent intent) {
        if (intent == null) return;

        Uri data = intent.getData();
        if (data != null) {
            String incomingUrl = data.toString();
            urlBox.setText(incomingUrl);
            webView.loadUrl(incomingUrl);
            return;
        }

        String action = intent.getAction();
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);

        if (Intent.ACTION_SEND.equals(action) && sharedText != null) {
            sharedText = sharedText.trim();
            if (sharedText.startsWith("http://") || sharedText.startsWith("https://")) {
                urlBox.setText(sharedText);
                webView.loadUrl(sharedText);
            }
        }
    }

    private void downloadImagesFromCurrentPage() {
        String js =
                "(function(){" +
                        "var out=[];" +
                        "document.querySelectorAll('img').forEach(function(el){" +
                        " var u=el.currentSrc||el.src;" +
                        " if(u&&u.startsWith('http')) out.push(u);" +
                        "});" +
                        "var meta=document.querySelector('meta[property=\\\"og:image\\\"]');" +
                        "if(meta&&meta.content&&meta.content.startsWith('http')) out.push(meta.content);" +
                        "return JSON.stringify(out);" +
                        "})();";

        webView.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                handleJsMediaResult(value, false);
            }
        });
    }

    private void downloadVideosFromCurrentPage() {
        String js =
                "(function(){" +
                        "var out=[];" +
                        "document.querySelectorAll('video').forEach(function(el){" +
                        " var u=el.currentSrc||el.src;" +
                        " if(u&&u.startsWith('http')) out.push(u);" +
                        "});" +
                        "var meta=document.querySelector('meta[property=\\\"og:video\\\"]');" +
                        "if(meta&&meta.content&&meta.content.startsWith('http')) out.push(meta.content);" +
                        "return JSON.stringify(out);" +
                        "})();";

        webView.evaluateJavascript(js, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                handleJsMediaResult(value, true);
            }
        });
    }

    private void handleJsMediaResult(String raw, boolean videoMode) {
        if (raw == null || raw.equals("null") || raw.length() < 2) {
            Toast.makeText(this, "No media found", Toast.LENGTH_SHORT).show();
            return;
        }

        String cleaned = raw;

        if (cleaned.startsWith("\"") && cleaned.endsWith("\"")) {
            cleaned = cleaned.substring(1, cleaned.length() - 1);
        }

        cleaned = cleaned.replace("\\u003C", "<")
                .replace("\\n", "")
                .replace("\\/", "/")
                .replace("\\\"", "\"")
                .trim();

        if (cleaned.equals("[]")) {
            Toast.makeText(this, "No media found", Toast.LENGTH_SHORT).show();
            return;
        }

        Set<String> links = new LinkedHashSet<>();
        String[] parts = cleaned.split(",");

        for (String part : parts) {
            String s = part.trim();

            if (s.startsWith("[")) s = s.substring(1);
            if (s.endsWith("]")) s = s.substring(0, s.length() - 1);
            if (s.startsWith("\"")) s = s.substring(1);
            if (s.endsWith("\"")) s = s.substring(0, s.length() - 1);

            s = s.trim();

            if (s.startsWith("http://") || s.startsWith("https://")) {
                links.add(s);
            }
        }

        if (links.isEmpty()) {
            Toast.makeText(this, "No media found", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = 0;
        int skipped = 0;

        for (String link : links) {
            if (downloadedMediaUrls.contains(link)) {
                skipped++;
                continue;
            }
            if (enqueueDownload(link, videoMode)) {
                downloadedMediaUrls.add(link);
                count++;
            }
        }

        if (count == 0 && skipped > 0) {
            Toast.makeText(this, "Already downloaded from this exact URL", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Downloading " + count + " file(s)" + (skipped > 0 ? " | skipped " + skipped : ""), Toast.LENGTH_SHORT).show();
    }

    private boolean enqueueDownload(String url, boolean videoMode) {
        try {
            String username = getBestFolderName();
            String fileName = buildFileName(url, videoMode);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setTitle(fileName);
            request.setDescription("Downloading media...");
            request.setNotificationVisibility(
                    DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            );
            request.setAllowedOverMetered(true);
            request.setAllowedOverRoaming(true);
            request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    "instabiotch/" + username + "/" + fileName
            );

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(this, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private String buildFileName(String url, boolean videoMode) {
        String cleanUrl = url == null ? "" : url.trim();
        String pathGuess = Uri.parse(cleanUrl).getLastPathSegment();

        if (pathGuess == null || pathGuess.trim().isEmpty()) {
            pathGuess = (videoMode ? "video_" : "image_") + System.currentTimeMillis();
        }

        int q = pathGuess.indexOf('?');
        if (q != -1) {
            pathGuess = pathGuess.substring(0, q);
        }

        pathGuess = sanitizeFileName(pathGuess);
        String extension = extractExtension(pathGuess);

        if (extension.isEmpty()) {
            extension = guessExtensionFromUrl(cleanUrl, videoMode);
            pathGuess = pathGuess + extension;
        }

        if (!hasKnownMediaExtension(pathGuess)) {
            pathGuess = removeExistingExtension(pathGuess) + guessExtensionFromUrl(cleanUrl, videoMode);
        }

        return pathGuess;
    }

    private String guessExtensionFromUrl(String url, boolean videoMode) {
        String lower = url == null ? "" : url.toLowerCase(Locale.US);

        if (lower.contains(".jpeg")) return ".jpeg";
        if (lower.contains(".jpg")) return ".jpg";
        if (lower.contains(".png")) return ".png";
        if (lower.contains(".gif")) return ".gif";
        if (lower.contains(".webp")) return ".webp";
        if (lower.contains(".webm")) return ".webm";
        if (lower.contains(".m3u8")) return ".m3u8";
        if (lower.contains(".mp4")) return ".mp4";

        String extFromUrl = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extFromUrl != null && !extFromUrl.isEmpty()) {
            extFromUrl = extFromUrl.toLowerCase(Locale.US);
            if (extFromUrl.equals("jpg") || extFromUrl.equals("jpeg") || extFromUrl.equals("png") ||
                    extFromUrl.equals("gif") || extFromUrl.equals("webp") || extFromUrl.equals("mp4") ||
                    extFromUrl.equals("webm") || extFromUrl.equals("m3u8")) {
                return "." + extFromUrl;
            }
        }

        String guessedName = URLUtil.guessFileName(url, null, null);
        String lowerName = guessedName == null ? "" : guessedName.toLowerCase(Locale.US);
        if (lowerName.endsWith(".jpeg")) return ".jpeg";
        if (lowerName.endsWith(".jpg")) return ".jpg";
        if (lowerName.endsWith(".png")) return ".png";
        if (lowerName.endsWith(".gif")) return ".gif";
        if (lowerName.endsWith(".webp")) return ".webp";
        if (lowerName.endsWith(".webm")) return ".webm";
        if (lowerName.endsWith(".m3u8")) return ".m3u8";
        if (lowerName.endsWith(".mp4")) return ".mp4";

        return videoMode ? ".mp4" : ".jpg";
    }

    private boolean hasKnownMediaExtension(String fileName) {
        String lower = fileName.toLowerCase(Locale.US);
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") ||
                lower.endsWith(".gif") || lower.endsWith(".webp") || lower.endsWith(".mp4") ||
                lower.endsWith(".webm") || lower.endsWith(".m3u8");
    }

    private String extractExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot == -1 || dot == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dot).toLowerCase(Locale.US);
    }

    private String removeExistingExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot == -1) return fileName;
        return fileName.substring(0, dot);
    }

    private String sanitizeFileName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "file_" + System.currentTimeMillis();
        }
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    private String getBestFolderName() {
        String profileName = getCurrentUsernameFromUrl();
        if (!profileName.equals("unknown_user")) {
            return profileName;
        }

        String ownerFromMeta = getOwnerHintFromCurrentUrl();
        if (!ownerFromMeta.equals("unknown_user")) {
            return ownerFromMeta;
        }

        return "user" + (random.nextInt(24) + 1);
    }

    private String getCurrentUsernameFromUrl() {
        try {
            String currentUrl = webView.getUrl();
            if (currentUrl == null || currentUrl.trim().isEmpty()) {
                return "unknown_user";
            }

            Uri uri = Uri.parse(currentUrl);
            String path = uri.getPath();
            if (path == null || path.trim().isEmpty()) {
                return "unknown_user";
            }

            String[] parts = path.split("/");
            for (String part : parts) {
                if (part == null || part.trim().isEmpty()) continue;

                String p = part.trim();
                if (p.equals("p") || p.equals("reel") || p.equals("reels") || p.equals("stories") ||
                        p.equals("explore") || p.equals("accounts") || p.equals("tv") || p.equals("direct")) {
                    return "unknown_user";
                }
                return sanitizeFolderName(p);
            }
        } catch (Exception ignored) {
        }
        return "unknown_user";
    }

    private String getOwnerHintFromCurrentUrl() {
        try {
            String currentUrl = webView.getUrl();
            if (currentUrl == null) return "unknown_user";

            String lower = currentUrl.toLowerCase(Locale.US);
            if (lower.contains("/stories/")) {
                String[] parts = Uri.parse(currentUrl).getPath().split("/");
                for (int i = 0; i < parts.length; i++) {
                    if ("stories".equals(parts[i]) && i + 1 < parts.length) {
                        return sanitizeFolderName(parts[i + 1]);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return "unknown_user";
    }

    private String sanitizeFolderName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "unknown_user";
        }
        return name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }

    private int dp(int value) {
        return Math.round(
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        value,
                        getResources().getDisplayMetrics()
                )
        );
    }
}
