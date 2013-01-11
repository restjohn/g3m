

package org.glob3.mobile.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.glob3.mobile.generated.BSONGenerator;
import org.glob3.mobile.generated.BSONParser;
import org.glob3.mobile.generated.IBufferDownloadListener;
import org.glob3.mobile.generated.IByteBuffer;
import org.glob3.mobile.generated.ITimer;
import org.glob3.mobile.generated.JSONBaseObject;
import org.glob3.mobile.generated.JSONGenerator;
import org.glob3.mobile.generated.TimeInterval;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.specific.G3MBuilder_Android;
import org.glob3.mobile.specific.G3MWidget_Android;
import org.glob3.mobile.specific.Timer_Android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;


public class G3MBSONActivity
         extends
            Activity {

   private final String        TAG         = "G3MBSON";

   private final static String FILE_SERVER = "http://glob3m.glob3mobile.com/test/";

   private G3MWidget_Android   _widget;
   private ITimer              _timer;


   @Override
   protected void onCreate(final Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_g3_mbson);

      final G3MBuilder_Android builder = new G3MBuilder_Android(this);
      _widget = builder.createWidget();

      transformBSON2JSON();
      //      transformJSON2BSON();
   }


   private void transformBSON2JSON() {

      final String fileName = "ACCESS-A.2011020104.nc7.slice10.bson";
      //      final String fileName = "ACCESS-test.bson";

      _timer = new Timer_Android();
      _timer.start();

      _widget.getG3MContext().getDownloader().requestBuffer(//
               new URL(FILE_SERVER + fileName, false), //
               100000000L, //
               TimeInterval.fromDays(30), //
               new IBufferDownloadListener() {

                  @Override
                  public void onError(final URL url) {
                     Log.e(TAG, "error downloading");
                  }


                  @Override
                  public void onDownload(final URL url,
                                         final IByteBuffer buffer) {
                     final JSONBaseObject jsonBO = parseBSON(buffer);
                     generateJSON(jsonBO);
                  }


                  @Override
                  public void onCanceledDownload(final URL url,
                                                 final IByteBuffer data) {

                  }


                  @Override
                  public void onCancel(final URL url) {

                  }
               }, //
               true);
   }


   private void transformJSON2BSON() {

      //    final String fileName = "seymour-plane.json";
      //      final String fileName = "boundary_lines_land.geojson";
      //      final String fileName = "test.json";
      final String fileName = "ACCESS-test.json";

      _timer = new Timer_Android();
      _timer.start();

      _widget.getG3MContext().getDownloader().requestBuffer(//
               new URL(FILE_SERVER + fileName, false), //
               100000000L, //
               TimeInterval.fromDays(30), //
               new IBufferDownloadListener() {

                  @Override
                  public void onError(final URL url) {
                     Log.e(TAG, "error downloading");
                  }


                  @Override
                  public void onDownload(final URL url,
                                         final IByteBuffer buffer) {
                     final JSONBaseObject jsonBO = parseJSON(buffer);
                     final IByteBuffer bufBSON = generateBSON(jsonBO);
                     saveBSON(bufBSON, "test.bson");
                  }


                  @Override
                  public void onCanceledDownload(final URL url,
                                                 final IByteBuffer data) {

                  }


                  @Override
                  public void onCancel(final URL url) {

                  }
               }, //
               true);
   }


   private JSONBaseObject parseBSON(final IByteBuffer buffer) {
      Log.i(TAG, "BEGIN BSONParser.parse(buffer)");
      final TimeInterval beginTime = _timer.now();
      final JSONBaseObject bsonObj = BSONParser.parse(buffer);
      final long elapsedTime = _timer.now().milliseconds() - beginTime.milliseconds();
      Log.i(TAG, "END BSONParser.parse(buffer):" + elapsedTime + "ms");

      return bsonObj;
   }


   private JSONBaseObject parseJSON(final IByteBuffer buffer) {
      Log.i(TAG, "BEGIN _jsonParser.parse(buffer)");
      final TimeInterval beginTime = _timer.now();
      final JSONBaseObject jsonObj = _widget.getG3MContext().getJSONParser().parse(buffer);
      final long elapsedTime = _timer.now().milliseconds() - beginTime.milliseconds();
      Log.i(TAG, "END _jsonParser.parse(buffer) " + elapsedTime + "ms");

      return jsonObj;
   }


   private IByteBuffer generateBSON(final JSONBaseObject jsonObj) {
      Log.i(TAG, "BEGIN BSONGenerator.generate(jsonObj)");
      final TimeInterval beginTime = _timer.now();
      final IByteBuffer buffer = BSONGenerator.generate(jsonObj);
      final long elapsedTime = _timer.now().milliseconds() - beginTime.milliseconds();
      Log.i(TAG, "END BSONGenerator.generate(jsonObj): " + elapsedTime + "ms");

      return buffer;
   }


   private String generateJSON(final JSONBaseObject jsonObj) {
      Log.i(TAG, "BEGIN JSONGenerator.generate(jsonObj)");
      final TimeInterval beginTime = _timer.now();
      final String jsonString = JSONGenerator.generate(jsonObj);
      final long elapsedTime = _timer.now().milliseconds() - beginTime.milliseconds();
      Log.i(TAG, "END JSONGenerator.generate(jsonObj): " + elapsedTime + "ms");

      return jsonString;
   }


   private void saveBSON(final IByteBuffer buffer,
                         final String fileName) {
      try {
         final File file = new File(getExternalFilesDir(null), fileName);
         final OutputStream os = new FileOutputStream(file);
         for (int i = 0; i < buffer.size(); i++) {
            os.write(buffer.get(i));
         }
         os.close();
         Log.i(TAG, "File saved");
      }
      catch (final IOException e) {
         e.printStackTrace();
      }
   }
}
