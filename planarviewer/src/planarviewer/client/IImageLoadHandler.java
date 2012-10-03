

package planarviewer.client;

import com.google.gwt.event.shared.EventHandler;


public interface IImageLoadHandler
         extends
            EventHandler {
   public void imageLoaded(GImageLoadEvent event);
}
