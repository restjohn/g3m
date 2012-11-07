

package org.glob3.mobile.g3mandroidplanarviewerdemo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.glob3.mobile.generated.URL;
import org.glob3.mobile.specific.Browser_Android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;


public class PlanarVActivity
         extends
            Activity {

   @Override
   public void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //setContentView(R.layout.planar_viewer_activity);
      //final WebView wv = (WebView) this.findViewById(R.id.webView1);

      //final MyWebView wv = new MyWebView(this);
      final WebView wv = new WebView(this);

      setContentView(wv);
      //getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

      final Browser_Android ba = new Browser_Android(wv);

      URL url = null;
      try {
         url = new URL("file:///android_asset/www/planarpanoramic.html?url="
                       + URLEncoder.encode("http://192.168.1.20/caminomontana", "UTF-8"), false);
      }
      catch (final UnsupportedEncodingException e) {
         Log.e("UnsupportedEncodingException: ", e.getMessage());
      }

      ba.openInBrowser(url);
   }


   @Override
   public boolean onCreateOptionsMenu(final Menu menu) {
      getMenuInflater().inflate(R.menu.activity_planar_v, menu);
      return true;
   }

   //   private class MyWebView
   //            extends
   //               WebView {
   //
   //      public MyWebView(final Context context) {
   //         super(context);
   //      }
   //
   //
   //      @Override
   //      public boolean onTouchEvent(final MotionEvent event) {
   //
   //         Log.i("onTouchEvent: ", event.toString());
   //         //super.onTouchEvent(event);
   //         return super.onTouchEvent(event);
   //         //return true;
   //      }
   //
   //   }
}
