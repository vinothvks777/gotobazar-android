
package in.gototop.gotobazar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

    private static final String HOME = "https://gototop.in/";

    private WebView webView;
    private ProgressBar progress;

    private final int TEAL = Color.rgb(10, 68, 89);
    private final int YELLOW = Color.rgb(244, 163, 0);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(TEAL);
        getWindow().setNavigationBarColor(TEAL);

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        // Website area
        FrameLayout webContainer = new FrameLayout(this);

        webView = new WebView(this);

        progress = new ProgressBar(
                this, null,
                android.R.attr.progressBarStyleHorizontal
        );

        progress.setMax(100);
        progress.setProgressTintList(
                android.content.res.ColorStateList.valueOf(YELLOW)
        );

        webContainer.addView(
                webView,
                new FrameLayout.LayoutParams(-1, -1)
        );

        FrameLayout.LayoutParams progressParams =
                new FrameLayout.LayoutParams(-1, dp(3), Gravity.TOP);

        webContainer.addView(progress, progressParams);

        mainLayout.addView(
                webContainer,
                new LinearLayout.LayoutParams(
                        -1, 0, 1
                )
        );

        // Bottom navigation
        LinearLayout bottomBar = new LinearLayout(this);
        bottomBar.setOrientation(LinearLayout.HORIZONTAL);
        bottomBar.setGravity(Gravity.CENTER);
        bottomBar.setBackgroundColor(TEAL);
        bottomBar.setPadding(dp(12), dp(7), dp(12), dp(7));

        Button homeButton = new Button(this);
        homeButton.setText("⌂  HOME");
        homeButton.setTextColor(Color.WHITE);
        homeButton.setTextSize(15);
        homeButton.setAllCaps(false);
        homeButton.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(YELLOW)
        );

        bottomBar.addView(
                homeButton,
                new LinearLayout.LayoutParams(-1, dp(48))
        );

        mainLayout.addView(
                bottomBar,
                new LinearLayout.LayoutParams(-1, dp(62))
        );

        setContentView(mainLayout);

        // WebView settings
        WebSettings settings = webView.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setSupportMultipleWindows(false);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(true);
        settings.setMixedContentMode(
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        );

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance()
                .setAcceptThirdPartyCookies(webView, true);

        webView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Loading progress
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(
                    WebView view, int newProgress) {

                progress.setProgress(newProgress);

                progress.setVisibility(
                        newProgress >= 100
                                ? View.GONE : View.VISIBLE
                );
            }
        });

        // Website navigation
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {

                return routeUrl(request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, String url) {

                return routeUrl(url);
            }

            @Override
            public void onPageStarted(
                    WebView view, String url, Bitmap favicon) {

                progress.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(
                    WebView view, String url) {

                progress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedSslError(
                    WebView view,
                    SslErrorHandler handler,
                    SslError error) {

                // Keep SSL security enabled.
                handler.cancel();
            }
        });

        // Home button
        homeButton.setOnClickListener(v -> {
            webView.loadUrl(HOME);
        });

        // Load website
        if (savedInstanceState == null) {
            webView.loadUrl(HOME);
        } else {
            webView.restoreState(savedInstanceState);
        }
    }

    // Keep normal websites inside the app
    private boolean routeUrl(String value) {

        Uri uri = Uri.parse(value);

        String scheme = uri.getScheme() == null
                ? "" : uri.getScheme().toLowerCase();

        if (scheme.equals("https") || scheme.equals("http")) {
            return false;
        }

        // External phone, email and payment actions
        if (scheme.equals("tel") ||
                scheme.equals("mailto") ||
                scheme.equals("sms") ||
                scheme.equals("upi")) {

            try {
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } catch (Exception ignored) {
            }

            return true;
        }

        return true;
    }

    private int dp(int value) {
        return (int) (
                value * getResources()
                        .getDisplayMetrics().density
        );
    }

    @Override
    public void onBackPressed() {

        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (webView != null) {
            webView.saveState(outState);
        }
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {
            webView.destroy();
            webView = null;
        }

        super.onDestroy();
    }
}
