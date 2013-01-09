

package org.glob3.mobile.demo;

import java.net.URLEncoder;

import org.glob3.mobile.generated.URL;
import org.glob3.mobile.specific.Browser_Android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebView;


public class PlanarViewerActivity
         extends
            Activity {

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_planar_viewer_webview);
      final WebView webView = (WebView) findViewById(R.id.webView);
      final String markUrl = getIntent().getStringExtra("markUrl");
      final URL url = new URL("file:///android_asset/www/planarpanoramic.html?url=" + URLEncoder.encode(markUrl), false);
      final Browser_Android ba = new Browser_Android(webView);
      ba.openInBrowser(url);
   }


   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {
      return true;
   }

}
