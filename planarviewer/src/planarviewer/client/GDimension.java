

package planarviewer.client;


public class GDimension {


   private final int _width;
   private final int _height;


   public GDimension(final int width,
                     final int height) {
      _width = width;
      _height = height;
   }


   public int getWidth() {
      return _width;
   }


   public int getHeight() {
      return _height;
   }


   @Override
   public String toString() {
      return _width + ", " + _height;
   }

}
