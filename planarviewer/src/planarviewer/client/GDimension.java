

package planarviewer.client;



public class GDimension
//         implements
//            DragEndHandler, DragStartHandler, DragEnterHandler, DragLeaveHandler, DragOverHandler, DragEnterClickHandler, DragHandler, DragSourceMotionListener {
{
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


   //
   //   @Override
   //   public void dragMouseMoved(DragSourceDragEvent dsde) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //
   //   @Override
   //   public void onDrag(DragEvent event) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //
   //   @Override
   //   public void onDoubleClick(DragEnterEvent event) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //
   //   @Override
   //   public void onDragOver(DragOverEvent event) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //
   //   @Override
   //   public void onDragLeave(DragLeaveEvent event) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //
   //   @Override
   //   public void onDragEnter(DragEnterEvent event) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //
   //   @Override
   //   public void onDragStart(DragStartEvent event) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //
   //   @Override
   //   public void onDragEnd(DragEndEvent event) {
   //      // TODO Auto-generated method stub
   //      
   //   }


}
