

package planarviewer.client;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;


/**
 * Sub-class of Image that allows maxWidth and maxHeight to be specified. When the image loads it automatically resizes itself to
 * maintain the correct aspect ratio and fit within the maximum dimensions.
 * 
 */
public class GImage
         extends
            Image {

   private Integer    _maxWidth, _maxHeight, _fixedWidth, _fixedHeight;
   private int        _height = 0;
   private int        _width  = 0;
   private Double     _aspectRatio;
   private GDimension _dimensions;


   private void resize() {
      if (_fixedWidth != null) {
         setWidth(_fixedWidth);
         if (_fixedHeight != null) {
            setHeight(_fixedHeight);
         }
         else if (_aspectRatio != null) {
            setHeight((int) Math.round(_fixedWidth * _aspectRatio));
         }
         else {
            setHeight(_fixedWidth);
         }
      }
      else if (_fixedHeight != null) {
         setHeight(_fixedHeight);
         if (_aspectRatio != null) {
            setWidth((int) Math.round(_fixedHeight / _aspectRatio));
         }
         else {
            setWidth(_fixedHeight);
         }
      }
      else if (_maxWidth != null) {
         if (_maxHeight != null) {
            if (_aspectRatio != null) {
               final double maxAR = ((double) _maxHeight) / ((double) _maxWidth);
               if (_aspectRatio > maxAR) {
                  setHeight(_maxHeight);
                  setWidth((int) Math.round(_maxHeight / _aspectRatio));
               }
               else {
                  setWidth(_maxWidth);
                  setHeight((int) Math.round(_maxWidth * _aspectRatio));
               }
            }
            else {
               setWidth(_maxWidth);
               setHeight(_maxHeight);
            }
         }
         else {
            setWidth(_maxWidth);
            if (_aspectRatio != null) {
               setHeight((int) Math.round(_maxWidth * _aspectRatio));
            }
            else {
               setHeight(_maxWidth);
            }
         }
      }
      else if (_maxHeight != null) {
         setHeight(_maxHeight);
         if (_aspectRatio != null) {
            setWidth((int) Math.round(_maxHeight / _aspectRatio));
         }
         else {
            setWidth(_maxHeight);
         }
      }
      else {
         setWidth((Integer) null);
         setHeight((Integer) null);
      }
   }


   @Override
   public void onBrowserEvent(final Event e) {
      //do nothing
   }


   public GImage() {
   }


   //   @Override
   //   public HandlerRegistration addDragHandler(final DragHandler handler) {
   //      return null;
   //      //do nothing
   //   }
   //
   //
   //   @Override
   //   public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler) {
   //      return null;
   //      //do nothing
   //   }


   public GImage(final Image image) {
      super.setUrl(image.getUrl());
      _dimensions = new GDimension(image.getWidth(), image.getHeight());
      _aspectRatio = (double) ((_dimensions.getHeight()) / (_dimensions.getWidth()));
      resize();
   }


   public GImage(final String url) {
      super();
      setUrl(url);
   }


   public GImage(final IImageLoadHandler loadHandler) {
      super();
      addImageLoadHandler(loadHandler);
   }


   public GImage(final String url,
                 final IImageLoadHandler loadHandler) {
      super();
      addImageLoadHandler(loadHandler);
      setUrl(url);
   }


   public GImage(final String url,
                 final int maxWidth,
                 final int maxHeight) {
      super();
      _maxWidth = maxWidth;
      _maxHeight = maxHeight;
      setUrl(url);
      resize();
   }


   public GImage(final String url,
                 final int maxWidth,
                 final int maxHeight,
                 final IImageLoadHandler loadHandler) {
      super();
      _maxWidth = maxWidth;
      _maxHeight = maxHeight;
      addImageLoadHandler(loadHandler);
      setUrl(url);
      resize();
   }


   @Override
   public void setUrl(final String url) {
      super.setUrl(url);
      GImageLoader.load(url, new IImageLoadHandler() {
         @Override
         public void imageLoaded(final GImageLoadEvent event) {
            if (!event.isLoadFailed()) {
               _dimensions = event.getDimensions();
               _aspectRatio = (double) ((_dimensions.getHeight()) / (_dimensions.getWidth()));
            }
            resize();
            //fireEvent(new GImageLoadEvent(event.isLoadFailed()));
         }
      });
   }


   public Integer getOriginalWidth() {
      return _dimensions == null ? null : _dimensions.getWidth();
   }


   public Integer getOriginalHeight() {
      return _dimensions == null ? null : _dimensions.getHeight();
   }


   /**
    * <p>
    * Handle FitImageLoadEvents. These events are fired whenever the image finishes loading completely or fails to load. The event
    * occurs after the image has been resized to fit the original image aspect ratio.
    * 
    * <p>
    * NOTE: Add this handler before setting the URL property of the FitImage. If set after, there is no guarantee that the handler
    * will be fired for the event.
    */
   public HandlerRegistration addImageLoadHandler(final IImageLoadHandler handler) {
      return addHandler(handler, GImageLoadEvent.getType());
   }


   public Integer getMaxWidth() {
      return _maxWidth;
   }


   /**
    * The width of the image will never exceed this number of pixels.
    */
   public void setMaxWidth(final Integer maxWidth) {
      _maxWidth = maxWidth;
      resize();
   }


   public Integer getMaxHeight() {
      return _maxHeight;
   }


   /**
    * The height of the image will never exceed this number of pixels.
    */
   public void setMaxHeight(final Integer maxHeight) {
      _maxHeight = maxHeight;
      resize();
   }


   public void setMaxSize(final Integer maxWidth,
                          final Integer maxHeight) {
      _maxWidth = maxWidth;
      _maxHeight = maxHeight;
      resize();
   }


   public Integer getFixedWidth() {
      return _fixedWidth;
   }


   /**
    * The exact width (in pixels) for the image. This overrides the max dimension behavior, but preserves aspect ratio if
    * fixedHeight is not also specified.
    */
   public void setFixedWidth(final Integer fixedWidth) {
      _fixedWidth = fixedWidth;
      resize();
   }


   public Integer getFixedHeight() {
      return _fixedHeight;
   }


   /**
    * The exact height (in pixels) for the image. This overrides the max dimension behavior, but preserves aspect ratio if
    * fixedWidth is not also specified.
    */
   public void setFixedHeight(final Integer fixedHeight) {
      _fixedHeight = fixedHeight;
      resize();
   }


   public void setFixedSize(final Integer fixedWidth,
                            final Integer fixedHeight) {
      _fixedWidth = fixedWidth;
      _fixedHeight = fixedHeight;
      resize();
   }


   private void setHeight(final Integer px) {
      if (px == null) {
         setHeight("");
         _height = 0;
      }
      else {
         super.setHeight(px + "px");
         _height = px;
      }
   }


   @Override
   public int getHeight() {
      return _height;
   }


   private void setWidth(final Integer px) {
      if (px == null) {
         setWidth("");
         _width = 0;
      }
      else {
         super.setWidth(px + "px");
         _width = px;
      }
   }


   @Override
   public int getWidth() {
      return _width;
   }
}
