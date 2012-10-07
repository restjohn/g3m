

package planarviewer.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDragEndHandlers;
import com.google.gwt.event.dom.client.HasDragHandlers;
import com.google.gwt.event.dom.client.HasDragStartHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


public class Container
         extends
            Composite
         implements
            HasClickHandlers,
            HasDragHandlers,
            HasMouseWheelHandlers,
            HasDragStartHandlers,
            HasDragEndHandlers,
            HasMouseDownHandlers,
            HasMouseMoveHandlers
//ClickHandler,
//DragStartHandler,
//DragEndHandler,
//MouseWheelHandler 
{

   private final AbsolutePanel panel = new AbsolutePanel();


   //   private final int           _dragStart = 0;
   //   private final int           _dragEnd   = 0;


   public Container() {
      super();
      initWidget(panel);
      //addClickHandler(this);
      //      addDragHandler(this);
      //addDragStartHandler(this);
      //addDragEndHandler(this);
      //addMouseWheelHandler(this);
   }


   @Override
   public HandlerRegistration addClickHandler(final ClickHandler handler) {
      return addDomHandler(handler, ClickEvent.getType());
   }


   @Override
   public HandlerRegistration addDragHandler(final DragHandler handler) {
      return addDomHandler(handler, DragEvent.getType());
   }


   @Override
   public HandlerRegistration addDragStartHandler(final DragStartHandler handler) {
      return addDomHandler(handler, DragStartEvent.getType());
   }


   @Override
   public HandlerRegistration addDragEndHandler(final DragEndHandler handler) {
      return addDomHandler(handler, DragEndEvent.getType());
   }


   @Override
   public HandlerRegistration addMouseWheelHandler(final MouseWheelHandler handler) {
      return addDomHandler(handler, MouseWheelEvent.getType());
   }


   @Override
   public HandlerRegistration addMouseMoveHandler(final MouseMoveHandler handler) {
      return addDomHandler(handler, MouseMoveEvent.getType());
   }


   @Override
   public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler) {
      return addDomHandler(handler, MouseDownEvent.getType());
   }


   //   @Override
   //   public void onClick(final ClickEvent event) {
   //      System.out.println("MOUSE CLICK EVENT");
   //      System.out.println("CclientX: " + event.getClientX() + ", CclientY: " + event.getClientY());
   //      System.out.println("CscreenX: " + event.getScreenX() + ", CscreenY: " + event.getScreenY());
   //   }
   //
   //
   //   @Override
   //   public void onMouseWheel(final MouseWheelEvent event) {
   //      System.out.println("MOUSE WHEEL EVENT");
   //      System.out.println("WclientX: " + event.getClientX() + ", WclientY: " + event.getClientY());
   //      System.out.println("WscreenX: " + event.getScreenX() + ", WscreenY: " + event.getScreenY());
   //      System.out.println("WdeltaY: " + event.getDeltaY());
   //      event.stopPropagation();
   //   }
   //
   //
   //   //   @Override
   //   //   public void onDrag(final DragEvent event) {
   //   //      // TODO Auto-generated method stub
   //   //   }
   //
   //
   //   @Override
   //   public void onDragStart(final DragStartEvent event) {
   //      final int SclientX = event.getNativeEvent().getClientX();
   //      final int SclientY = event.getNativeEvent().getClientY();
   //      final int SscreenX = event.getNativeEvent().getScreenX();
   //      final int SscreenY = event.getNativeEvent().getScreenY();
   //      _dragStart = SscreenX;
   //      System.out.println("MOUSE DRAG-START EVENT");
   //      System.out.println("SclientX: " + SclientX + ", SclientY: " + SclientY);
   //      System.out.println("SscreenX: " + SscreenX + ", SscreenY: " + SscreenY);
   //   }
   //
   //
   //   @Override
   //   public void onDragEnd(final DragEndEvent event) {
   //      final int EclientX = event.getNativeEvent().getClientX();
   //      final int EclientY = event.getNativeEvent().getClientY();
   //      final int EscreenX = event.getNativeEvent().getScreenX();
   //      final int EscreenY = event.getNativeEvent().getScreenY();
   //      _dragEnd = EscreenX;
   //      System.out.println("MOUSE DRAG-END EVENT");
   //      System.out.println("EclientX: " + EclientX + ", EclientY: " + EclientY);
   //      System.out.println("EscreenX: " + EscreenX + ", EscreenY: " + EscreenY);
   //      System.out.println("Distance: " + (_dragEnd - _dragStart));
   //      //System.out.println("WscreenX: " + event.getScreenX() + ", WscreenY: " + event.getScreenY());
   //   }


   public void setWidget(final Widget widget,
                         final int left,
                         final int top) {
      panel.add(widget, left, top);
   }


   public void remove(final Widget widget) {
      panel.remove(widget);
   }


   public void setSize(final int width,
                       final int height) {
      super.setSize(Integer.toString(width) + "px", Integer.toString(height) + "px");
   }


   public void clear() {
      panel.clear();
   }


}
