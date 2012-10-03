

package planarviewer.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.RootPanel;


public class GPlanarViewerEntryPoint
         extends
            FlexTable
         implements
            EntryPoint {

   @Override
   public void onModuleLoad() {
      // TODO Auto-generated method stub
      System.out.println("> Starting up..");

      super.setCellPadding(0);
      super.setCellSpacing(0);
      super.setBorderWidth(0);
      super.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);
      //VerticalAlignmentConstant x;

      GImageLoader.load("BostonCityFlow.jpg", new OnLoadHandler(0));
      setWidget(0, 0, new GImage("BostonCityFlow.jpg"));

      GImageLoader.load("BostonCityFlow.jpg", new OnLoadHandler(0) {
         @Override
         public void imageLoaded(final GImageLoadEvent event) {
            setWidget(_row, 1, event.takeImage());
            super.imageLoaded(event);
         }
      });

      GImageLoader.load("BostonCityFlow.jpg", new OnLoadHandler(1));
      setWidget(1, 0, new GImage("BostonCityFlow.jpg"));

      GImageLoader.load("BostonCityFlow.jpg", new OnLoadHandler(1));
      setWidget(1, 1, new GImage("BostonCityFlow.jpg"));

      RootPanel.get().add(this);
   }

   private class OnLoadHandler
            implements
               IImageLoadHandler {

      public OnLoadHandler(final int row) {
         this._row = row;
      }

      int _row;


      @Override
      public void imageLoaded(final GImageLoadEvent event) {
         if (event.isLoadFailed()) {
            setText(_row + 2, _row, "Image failed to load.");
         }
         else {
            setText(_row + 2, _row, "Image dimensions: " + event.getDimensions().getWidth() + " x "
                                    + event.getDimensions().getHeight());
         }
      }
   }


}
