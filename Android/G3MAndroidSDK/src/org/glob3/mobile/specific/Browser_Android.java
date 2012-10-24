

package org.glob3.mobile.specific;

import org.glob3.mobile.generated.URL;

import android.annotation.SuppressLint;
import android.webkit.WebView;


public class Browser_Android {

   //private final Activity _activity;
   private final WebView _webView;


   //public Browser_Android(final Activity activity) {
   public Browser_Android(final WebView webView) {
      //_activity = activity;
      _webView = webView;
      //      _webView = new WebView(_activity.getApplicationContext());
      //      _activity.setContentView(_webView);

   }


   @SuppressLint("SetJavaScriptEnabled")
   public void openInBrowser(final URL targetUrl) {

      _webView.getSettings().setAllowFileAccess(true);
      _webView.getSettings().setJavaScriptEnabled(true);
      _webView.getSettings().setAppCacheEnabled(true);
      //WebSettings.
      //_webView.setAlwaysDrawnWithCacheEnabled(true);
      //_webView.setClickable(false);
      //_webView.getSettings().setSupportZoom(true);
      //_webView.getSettings().setBuiltInZoomControls(true);
      //_webView.getSettings().setLightTouchEnabled(true);
      //_webView.getSettings().setUseWideViewPort(true);

      //      _webView.setWebChromeClient(new WebChromeClient() {
      //         @Override
      //         public boolean onJsAlert(final WebView view,
      //                                  final String url,
      //                                  final String message,
      //                                  final JsResult result) {
      //            return super.onJsAlert(view, url, message, result);
      //         }
      //      });

      _webView.loadUrl(targetUrl.getPath());
   }

   //   public Browser_Android(final Activity activity) {
   //      _activity = activity;
   //   }
   //
   //
   //   public void openInBrowser(final URL url) {
   //
   //
   //      //      final android.webkit.WebView wv = (android.webkit.WebView) this.findViewById(R.id.webview);
   //      //      wv.loadUrl(url.getPath());
   //
   //      final Uri uri = Uri.parse(url.getPath());
   //      //final Uri uri = Uri.parse("file:///android_asset/planarpanoramic.html");
   //      final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
   //      _activity.startActivity(intent);
   //
   //   }
}
