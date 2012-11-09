

package org.glob3.mobile.g3mandroidplanarviewerdemo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

import org.glob3.mobile.generated.Angle;
import org.glob3.mobile.generated.GLErrorRenderer;
import org.glob3.mobile.generated.GTask;
import org.glob3.mobile.generated.Geodetic3D;
import org.glob3.mobile.generated.ICameraConstrainer;
import org.glob3.mobile.generated.IFactory;
import org.glob3.mobile.generated.IImage;
import org.glob3.mobile.generated.LayerSet;
import org.glob3.mobile.generated.Mark;
import org.glob3.mobile.generated.MarkTouchListener;
import org.glob3.mobile.generated.MarksRenderer;
import org.glob3.mobile.generated.PeriodicalTask;
import org.glob3.mobile.generated.QuadShape;
import org.glob3.mobile.generated.Renderer;
import org.glob3.mobile.generated.Sector;
import org.glob3.mobile.generated.ShapesRenderer;
import org.glob3.mobile.generated.SimpleCameraConstrainer;
import org.glob3.mobile.generated.URL;
import org.glob3.mobile.generated.UserData;
import org.glob3.mobile.generated.WMSLayer;
import org.glob3.mobile.generated.WMSServerVersion;
import org.glob3.mobile.specific.G3MBaseActivity;
import org.glob3.mobile.specific.G3MWidget_Android;

import android.app.AlertDialog;
import android.content.Intent;


public class G3MAndroidDemoActivity
         extends
            G3MBaseActivity {

   @Override
   protected void initializeWidget(final G3MWidget_Android widget) {
      final LayerSet layerSet = new LayerSet();

      final boolean useBing = false;
      if (useBing) {
         final WMSLayer bing = new WMSLayer("ve", new URL("http://worldwind27.arc.nasa.gov/wms/virtualearth?", false),
                  WMSServerVersion.WMS_1_1_0, Sector.fullSphere(), "image/png", "EPSG:4326", "", false, null);
         layerSet.addLayer(bing);
      }

      final boolean usePnoa = false;
      if (usePnoa) {
         final WMSLayer pnoa = new WMSLayer("PNOA", new URL("http://www.idee.es/wms/PNOA/PNOA", false),
                  WMSServerVersion.WMS_1_1_0, Sector.fromDegrees(21, -18, 45, 6), "image/png", "EPSG:4326", "", true, null);
         layerSet.addLayer(pnoa);
      }

      final boolean useOSM = true;
      if (useOSM) {
         //         final WMSLayer osm = new WMSLayer( //
         //                  "osm", //
         //                  new URL("http://wms.latlon.org/"), //
         //                  WMSServerVersion.WMS_1_1_0, //
         //                  Sector.fromDegrees(-85.05, -180.0, 85.5, 180.0), //
         //                  "image/jpeg", //
         //                  "EPSG:4326", //
         //                  "", //
         //                  false, //
         //                  null);
         //         layerSet.addLayer(osm);

         final WMSLayer osm = new WMSLayer( //
                  "osm_auto:all", //
                  new URL("http://129.206.228.72/cached/osm", false), //
                  WMSServerVersion.WMS_1_1_0, //
                  Sector.fromDegrees(-85.05, -180.0, 85.05, 180.0), //
                  "image/jpeg", //
                  "EPSG:4326", //
                  "", //
                  false, //
                  null);
         layerSet.addLayer(osm);
      }

      //  WMSLayer *vias = new WMSLayer("VIAS",
      //                                "http://idecan2.grafcan.es/ServicioWMS/Callejero",
      //                                WMS_1_1_0,
      //                                "image/gif",
      //                                Sector::fromDegrees(22.5,-22.5, 33.75, -11.25),
      //                                "EPSG:4326",
      //                                "",
      //                                true,
      //                                Angle::nan(),
      //                                Angle::nan());
      //  layerSet->addLayer(vias);

      //  WMSLayer *osm = new WMSLayer("bing",
      //                               "bing",
      //                               "http://wms.latlon.org/",
      //                               WMS_1_1_0,
      //                               "image/jpeg",
      //                               Sector::fromDegrees(-85.05, -180.0, 85.5, 180.0),
      //                               "EPSG:4326",
      //                               "",
      //                               false,
      //                               Angle::nan(),
      //                               Angle::nan());
      //  layerSet->addLayer(osm);

      final boolean testURLescape = false;
      if (testURLescape) {
         final WMSLayer ayto = new WMSLayer(URL.escape("Ejes de via"), //
                  new URL("http://sig.caceres.es/wms_callejero.mapdef?", false), //
                  WMSServerVersion.WMS_1_1_0,//  
                  Sector.fullSphere(), //
                  "image/png", //
                  "EPSG:4326", //
                  "", //
                  true, //
                  null);
         layerSet.addLayer(ayto);
      }

      final ArrayList<Renderer> renderers = new ArrayList<Renderer>();

      //  if (false) {
      //    // dummy renderer with a simple box
      //      final DummyRenderer dum = new DummyRenderer();
      //      renderers.add(dum);
      //  }

      //  if (false) {
      //    // simple planet renderer, with a basic world image
      //      final SimplePlanetRenderer spr = new SimplePlanetRenderer("world.jpg");
      //      renderers.add(spr);
      //  }


      final boolean useMarkers = true;
      if (useMarkers) {
         final boolean readyWhenMarksReady = false;
         final MarksRenderer marksRenderer = new MarksRenderer(readyWhenMarksReady);
         final MarksRenderer panoMarksRenderer = new MarksRenderer(readyWhenMarksReady);

         marksRenderer.setMarkTouchListener(new MarkTouchListener() {
            @Override
            public boolean touchedMark(final Mark mark) {
               G3MAndroidDemoActivity.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                     final AlertDialog.Builder builder = new AlertDialog.Builder(G3MAndroidDemoActivity.this);
                     builder.setMessage("Touched on mark \"" + mark.getName() + "\"");
                     builder.setTitle("G3M Demo");

                     final AlertDialog dialog = builder.create();
                     dialog.show();
                  }
               });

               return true;
            }
         }, true);

         panoMarksRenderer.setMarkTouchListener(new MarkTouchListener() {
            @Override
            public boolean touchedMark(final Mark mark) {
               G3MAndroidDemoActivity.this.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {

                     final String url = (String) mark.getUserData();
                     final Intent intent = new Intent(getApplicationContext(), PlanarViewerActivity.class);
                     intent.putExtra("markUrl", url);
                     G3MAndroidDemoActivity.this.startActivity(intent);
                     //                     final WebView wv = new WebView(G3MAndroidDemoActivity.this);
                     //                     setContentView(wv);
                     //                     final Browser_Android ba = new Browser_Android(wv);
                     //                     ba.openInBrowser(url);
                  }
               });

               return true;
            }
         }, true);

         renderers.add(marksRenderer);
         renderers.add(panoMarksRenderer);

         final Mark m1 = new Mark(//
                  "Fuerteventura", //
                  new URL("http://glob3m.glob3mobile.com/icons/markers/g3m.png", false), //
                  new Geodetic3D(Angle.fromDegrees(28.05), Angle.fromDegrees(-14.36), 0));
         marksRenderer.addMark(m1);

         final Mark m2 = new Mark( //
                  "Las Palmas", //
                  new URL("http://glob3m.glob3mobile.com/icons/markers/g3m.png", false), //
                  new Geodetic3D(Angle.fromDegrees(28.05), Angle.fromDegrees(-15.36), 0));
         marksRenderer.addMark(m2);

         final boolean randomMarkers = false;
         if (randomMarkers) {
            final Random random = new Random();
            for (int i = 0; i < 500; i++) {
               final Angle latitude = Angle.fromDegrees((random.nextInt() % 180) - 90);
               final Angle longitude = Angle.fromDegrees((random.nextInt() % 360) - 180);

               marksRenderer.addMark(new Mark( //
                        "Random #" + i, //
                        new URL("http://glob3m.glob3mobile.com/icons/markers/g3m.png", false), //
                        new Geodetic3D(latitude, longitude, 0)));

            }
         }

         //-- add markers for planar panoramics --------------------
         URL panoUrl = null;
         try {
            panoUrl = new URL("file:///android_asset/www/planarpanoramic.html?url="
                              + URLEncoder.encode("http://192.168.1.20/panos_cc/caminomontana", "UTF-8"), false);
            final Mark pano1 = new Mark( //
                     "caminomontana", //
                     new URL("http://glob3m.glob3mobile.com/icons/markers/g3m.png", false), //
                     new Geodetic3D(Angle.fromDegrees(39.49), Angle.fromDegrees(-6.32), 0), panoUrl.getPath());
            panoMarksRenderer.addMark(pano1);

            //            panoUrl = new URL("file:///android_asset/www/planarpanoramic.html?url="
            //                              + URLEncoder.encode("http://192.168.1.20/panos_cc/esmeralda2", "UTF-8"), false);
            //            final Mark pano2 = new Mark( //
            //                     "esmeralda2", //
            //                     new URL("http://glob3m.glob3mobile.com/icons/markers/g3m.png", false), //
            //                     new Geodetic3D(Angle.fromDegrees(39.47), Angle.fromDegrees(-6.30), 0), panoUrl.getPath());
            //            panoMarksRenderer.addMark(pano2);
         }
         catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
         }
         //---------------------------------------------------------


      }


      final boolean useQuadShapes = true;
      if (useQuadShapes) {
         final ShapesRenderer shapesRenderer = new ShapesRenderer();
         final String textureFileName = "g3m-marker.png";
         final IImage textureImage = IFactory.instance().createImageFromFileName(textureFileName);

         final QuadShape quad = new QuadShape( //
                  new Geodetic3D(Angle.fromDegrees(37.78333333), //
                           Angle.fromDegrees(-122.41666666666667), //
                           10000), //
                  textureImage, //
                  true, //
                  textureFileName, //
                  500000, //
                  500000);
         quad.setHeading(Angle.fromDegrees(0));
         quad.setPitch(Angle.fromDegrees(0));
         shapesRenderer.addShape(quad);

         renderers.add(shapesRenderer);
      }

      //  if (false) {
      //    LatLonMeshRenderer *renderer = new LatLonMeshRenderer();
      //    renderers.push_back(renderer);
      //  }

      //  if (false) {
      //    SceneGraphRenderer* sgr = new SceneGraphRenderer();
      //    SGCubeNode* cube = new SGCubeNode();
      //    // cube->setScale(Vector3D(6378137.0, 6378137.0, 6378137.0));
      //    sgr->getRootNode()->addChild(cube);
      //    renderers.push_back(sgr);
      //  }

      renderers.add(new GLErrorRenderer());

      final ArrayList<ICameraConstrainer> cameraConstraints = new ArrayList<ICameraConstrainer>();
      final SimpleCameraConstrainer scc = new SimpleCameraConstrainer();
      cameraConstraints.add(scc);

      final UserData userData = null;

      final GTask initializationTask = null;
      final ArrayList<PeriodicalTask> periodicalTasks = new ArrayList<PeriodicalTask>();
      final boolean incrementalTileQuality = false;
      widget.initWidget(cameraConstraints, layerSet, renderers, userData, initializationTask, periodicalTasks,
               incrementalTileQuality);

   }

}
