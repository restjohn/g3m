

package planarviewer.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;


public class GImageLoader {

   private static Map<String, GDimension>  _dimensionCache = new HashMap<String, GDimension>();

   private static List<ImageElementLoader> _activeLoaders  = new ArrayList<ImageElementLoader>();

   private static Element                  _loadingArea;


   public static GDimension getCachedDimensions(final String url) {
      return _dimensionCache.get(url);
   }


   /**
    * Call this method to preload an image.
    * 
    * @param url
    *           - the image to pre-load
    * @param loadHandler
    *           - (optional) specify an ImageLoadHandler to be fired when the image is fully loaded. Within this handler you will
    *           also be able to get the original dimensions of the loaded image.
    */
   public static void load(final String url,
                           final IImageLoadHandler loadHandler) {
      if (url == null) {
         if (loadHandler != null) {
            loadHandler.imageLoaded(new GImageLoadEvent(url, null));
         }
         return;
      }

      if (_dimensionCache.containsKey(url)) {
         if (loadHandler != null) {
            final GDimension cachedDimensions = _dimensionCache.get(url);
            if (cachedDimensions.getWidth() == -1) {
               // image load failed
               loadHandler.imageLoaded(new GImageLoadEvent(url, null));
            }
            else {
               // image load succeeded
               loadHandler.imageLoaded(new GImageLoadEvent(url, cachedDimensions));
            }
         }
         return;
      }

      final int index = findUrlInPool(url);
      if (index != -1) {
         _activeLoaders.get(index).addHandler(loadHandler);
         return;
      }

      init();

      final ImageElementLoader loader = new ImageElementLoader();
      _activeLoaders.add(loader);
      loader.addHandler(loadHandler);
      loader.start(url);
   }


   public static boolean isInLocalCache(final String url) {
      return _dimensionCache.containsKey(url);
   }


   private static void init() {

      if (_loadingArea == null) {
         _loadingArea = DOM.createDiv();
         _loadingArea.getStyle().setProperty("visibility", "hidden");
         _loadingArea.getStyle().setProperty("position", "absolute");
         _loadingArea.getStyle().setProperty("width", "1px");
         _loadingArea.getStyle().setProperty("height", "1px");
         _loadingArea.getStyle().setProperty("overflow", "hidden");
         Document.get().getBody().appendChild(_loadingArea);
         Event.setEventListener(_loadingArea, new EventListener() {
            @Override
            public void onBrowserEvent(final Event event) {
               boolean success;
               if (Event.ONLOAD == event.getTypeInt()) {
                  success = true;
                  //GWT.log("Init OK ");
               }
               else if (Event.ONERROR == event.getTypeInt()) {
                  success = false;
                  //GWT.log("Init ERROR ");
               }
               else {
                  return;
               }

               if (!ImageElement.is(event.getCurrentEventTarget())) {
                  return;
               }

               final ImageElement image = ImageElement.as(Element.as(event.getCurrentEventTarget()));
               final int index = findImageInPool(image);
               final ImageElementLoader loader = _activeLoaders.get(index);

               GDimension dim = null;
               if (success) {
                  dim = new GDimension(image.getWidth(), image.getHeight());
                  _dimensionCache.put(loader._url, dim);
               }
               else {
                  _dimensionCache.put(loader._url, new GDimension(-1, -1));
               }

               _loadingArea.removeChild(image);
               _activeLoaders.remove(index);

               final GImageLoadEvent evt = new GImageLoadEvent(image, dim);
               loader.fireHandlers(evt);
            }
         });
      }
   }


   private static int findImageInPool(final ImageElement image) {
      for (int index = 0; index < _activeLoaders.size(); index++) {
         if (_activeLoaders.get(index).imageEquals(image)) {
            return index;
         }
      }
      return -1;
   }


   private static int findUrlInPool(final String url) {
      for (int index = 0; index < _activeLoaders.size(); index++) {
         if (_activeLoaders.get(index).urlEquals(url)) {
            return index;
         }
      }
      return -1;
   }


   public static boolean isDownloadingImage(final String url) {
      return findUrlInPool(url) > -1;
   }

   private static class ImageElementLoader {

      ImageElement            _image = DOM.createImg().cast();
      List<IImageLoadHandler> _handlers;
      String                  _url;


      public ImageElementLoader() {
         Event.sinkEvents(_image, Event.ONLOAD | Event.ONERROR);
         _loadingArea.appendChild(_image);
      }


      @SuppressWarnings("unused")
      public void clearHandlers() {
         if (_handlers != null) {
            _handlers.clear();
         }
      }


      public void addHandler(final IImageLoadHandler handler) {
         if (handler != null) {
            if (_handlers == null) {
               _handlers = new ArrayList<IImageLoadHandler>(1);
            }
            _handlers.add(handler);
         }
      }


      public void fireHandlers(final GImageLoadEvent event) {
         if (_handlers != null) {
            for (final IImageLoadHandler handler : _handlers) {
               handler.imageLoaded(event);
            }
         }
      }


      public void start(final String url) {
         this._url = url;
         _image.setSrc(url);
      }


      public boolean imageEquals(final ImageElement image) {
         return this._image == image;
      }


      public boolean urlEquals(final String url) {
         return this._url.equals(url);
      }
   }

}
