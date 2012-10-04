

package planarviewer.client;


import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Image;


public class GImageLoadEvent
         extends
            GwtEvent<IImageLoadHandler> {

   private static final Type<IImageLoadHandler> TYPE = new Type<IImageLoadHandler>();

   protected ImageElement                       _image;
   protected String                             _url;
   protected GDimension                         _dimensions;
   protected boolean                            _imageTaken;


   public GImageLoadEvent(final ImageElement image,
                          final GDimension dimensions) {
      _image = image;
      _dimensions = dimensions;
   }


   public GImageLoadEvent(final String url,
                          final GDimension dimensions) {
      _url = url;
      _dimensions = dimensions;
   }


   public GDimension getDimensions() {
      return _dimensions;
   }


   public Image takeImage() {

      _imageTaken = true;
      if (_image == null) {
         return new Image(_url);
      }

      final Image ret = new ImageFromElement(_image);
      _image = null;
      return ret;

   }


   public String getImageUrl() {
      if (_url != null) {
         return _url;
      }
      return _image.getSrc();
   }


   public GImage getImage() {
      //return (GImage) getSource();
      return new GImage(getImageUrl());
      //return (GImage) takeImage();
   }


   public boolean isImageTaken() {
      return _imageTaken;
   }


   public boolean isLoadFailed() {
      return _dimensions == null;
   }


   @Override
   protected void dispatch(final IImageLoadHandler handler) {
      handler.imageLoaded(this);
   }


   @Override
   public Type<IImageLoadHandler> getAssociatedType() {
      return TYPE;
   }


   public static Type<IImageLoadHandler> getType() {
      return TYPE;
   }

   private static class ImageFromElement
            extends
               Image {
      public ImageFromElement(final ImageElement element) {
         super(element);
      }
   }

}
