

package org.glob3.mobile.g3mandroidplanarviewerdemo;

import java.io.UnsupportedEncodingException;
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
      setContentView(R.layout.planar_viewer_activity);

      final WebView wv = (WebView) this.findViewById(R.id.webView1);
      //final WebView wv = new WebView(getApplicationContext());

      final Browser_Android ba = new Browser_Android(wv);

      URL url = null;
      try {
         url = new URL("file:///android_asset/www/planarpanoramic.html?url="
                       + URLEncoder.encode("http://192.168.1.20/caminomontana", "UTF-8"));
      }
      catch (final UnsupportedEncodingException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      ba.openInBrowser(url);

   }


   //   public class BrowserActivity
   //            extends
   //               Activity {
   //
   //      private 
   //      @Override
   //      public void onCreate(final Bundle savedInstanceState) {
   //         super.onCreate(savedInstanceState);
   //         final WebView wv = new WebView(getApplicationContext());
   //         
   //      }
   //   }


   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {
      getMenuInflater().inflate(R.menu.planar_viewer_activity, menu);
      return true;
   }
}
