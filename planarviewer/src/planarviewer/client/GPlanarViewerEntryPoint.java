

package planarviewer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.RootPanel;


public class GPlanarViewerEntryPoint
         implements
            EntryPoint {

   @Override
   public void onModuleLoad() {

      final String imgUrl = Location.getParameter("url");
      final String[] splitedUrl = imgUrl.split("/");
      final String name = splitedUrl[splitedUrl.length - 1];
      //System.out.println("url=" + imgUrl);
      //System.out.println("> Arrancando panoramica..");
      GWT.log("Starting panoramic viewer..");

      //final String imgUrl = "./IMG/caminomontana";

      final GPlanarPanoramicViewer viewer = new GPlanarPanoramicViewer(imgUrl, name, false);
      RootPanel.get().add(viewer);

   }

   //   @Override
   //   public void onModuleLoad() {
   //      // TODO Auto-generated method stub
   //      System.out.println("> Starting up..");
   //
   //      final String imgUrl = "./IMG/BostonCityFlow.jpg";
   //
   //      super.setCellPadding(0);
   //      super.setCellSpacing(0);
   //      super.setBorderWidth(0);
   //      //super.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
   //      //super.setHeight("683px");
   //      //VerticalAlignmentConstant x;
   //
   //      GImageLoader.load(imgUrl, new OnLoadHandler(0, 0));
   //      setWidget(0, 0, new GImage(imgUrl));
   //
   //      GImageLoader.load(imgUrl, new OnLoadHandler(0, 1) {
   //         @Override
   //         public void imageLoaded(final GImageLoadEvent event) {
   //            super.imageLoaded(event);
   //            setWidget(_row, _col, event.takeImage());
   //         }
   //      });
   //
   //      GImageLoader.load(imgUrl, new OnLoadHandler(1, 0));
   //      setWidget(1, 0, new GImage(imgUrl));
   //
   //      GImageLoader.load(imgUrl, new OnLoadHandler(1, 1));
   //      setWidget(1, 1, new GImage(imgUrl));
   //
   //      RootPanel.get().add(this);
   //   }
   //
   //   private class OnLoadHandler
   //            implements
   //               IImageLoadHandler {
   //
   //      final int _row, _col;
   //
   //
   //      public OnLoadHandler(final int row,
   //                           final int col) {
   //         _row = row;
   //         _col = col;
   //      }
   //
   //
   //      @Override
   //      public void imageLoaded(final GImageLoadEvent event) {
   //
   //         if (event.isLoadFailed()) {
   //            GWT.log("Image failed to load.");
   //            Window.alert("Image " + event.getImageUrl() + " failed to load.");
   //            //            if (!isCellPresent(_row + 1, _col)) {
   //            //               setText(_row + 1, _col, "Image failed to load.");
   //            //            }
   //         }
   //         else {
   //            //event.getImage().setFixedSize(1024, 683);
   //            //event.takeImage().setPixelSize(1024, 683);
   //            GWT.log("Image dimensions: " + event.getDimensions().getWidth() + " x " + event.getDimensions().getHeight());
   //            Window.alert("Image dimensions: " + event.getDimensions().getWidth() + " x " + event.getDimensions().getHeight());
   //            //            if (!isCellPresent(_row + 1, _col)) {
   //            //               setText(_row + 2, _col, "Image dimensions: " + event.getDimensions().getWidth() + " x "
   //            //                                       + event.getDimensions().getHeight());
   //
   //            //            }
   //         }
   //      }
   //   }


}
