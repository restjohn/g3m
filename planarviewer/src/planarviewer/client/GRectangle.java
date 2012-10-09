

package planarviewer.client;


public class GRectangle {

   /**
    * The X coordinate of the upper-left corner of the Rectangle.
    */
   public int _x;

   /**
    * The Y coordinate of the upper-left corner of the Rectangle.
    */
   public int _y;

   /**
    * The width of the Rectangle.
    */
   public int _width;

   /**
    * The height of the Rectangle.
    */
   public int _height;


   public GRectangle() {
      this(0, 0, 0, 0);
   }


   public GRectangle(final GRectangle r) {
      this(r._x, r._y, r._width, r._height);
   }


   public GRectangle(final int x,
                     final int y,
                     final int width,
                     final int height) {
      _x = x;
      _y = y;
      _width = width;
      _height = height;
   }


   public GRectangle(final int width,
                     final int height) {
      this(0, 0, width, height);
   }


   public GRectangle(final GDimension d) {
      this(0, 0, d.getWidth(), d.getHeight());
   }


   public double getX() {
      return _x;
   }


   public double getY() {
      return _y;
   }


   public double getWidth() {
      return _width;
   }


   public double getHeight() {
      return _height;
   }


   public GRectangle getBounds() {
      return new GRectangle(_x, _y, _width, _height);
   }


   public GDimension getSize() {
      return new GDimension(_width, _height);
   }


   public double getCenterX() {
      return getX() + (getWidth() / 2.0);
   }


   public double getCenterY() {
      return getY() + (getHeight() / 2.0);
   }


   public GRectangle scale(final double scale) {
      final int width = (int) (_width * scale);
      final int height = (int) (_height * scale);
      final int x = (int) (getCenterX() - (getWidth() / 2.0));
      final int y = (int) (getCenterY() - (getHeight() / 2.0));
      return new GRectangle(x, y, width, height);
   }


   public boolean intersects(final GRectangle r) {
      int tw = this._width;
      int th = this._height;
      int rw = r._width;
      int rh = r._height;
      if ((rw <= 0) || (rh <= 0) || (tw <= 0) || (th <= 0)) {
         return false;
      }
      final int tx = this._x;
      final int ty = this._y;
      final int rx = r._x;
      final int ry = r._y;
      rw += rx;
      rh += ry;
      tw += tx;
      th += ty;
      //      overflow || intersect
      return (((rw < rx) || (rw > tx)) && ((rh < ry) || (rh > ty)) && ((tw < tx) || (tw > rx)) && ((th < ty) || (th > ry)));
   }


   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = (prime * result) + _height;
      result = (prime * result) + _width;
      result = (prime * result) + _x;
      result = (prime * result) + _y;
      return result;
   }


   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      final GRectangle other = (GRectangle) obj;
      if (_height != other._height) {
         return false;
      }
      if (_width != other._width) {
         return false;
      }
      if (_x != other._x) {
         return false;
      }
      if (_y != other._y) {
         return false;
      }
      return true;
   }


   @Override
   public String toString() {
      return getClass().getName() + "[x=" + _x + ",y=" + _y + ",width=" + _width + ",height=" + _height + "]";
   }

}
