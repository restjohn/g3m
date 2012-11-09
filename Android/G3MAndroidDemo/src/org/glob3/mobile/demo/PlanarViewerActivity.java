

package org.glob3.mobile.demo;

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
      final String markUrl = getIntent().getStringExtra("markUrl");
      final WebView webView = (WebView) findViewById(R.id.webView);

      final Browser_Android ba = new Browser_Android(webView);
      ba.openInBrowser(markUrl);
   }


   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {
      return true;
   }

}
