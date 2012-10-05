

package planarviewer.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDragHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
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
            ClickHandler,
            DragHandler,
            MouseWheelHandler {

   private final AbsolutePanel panel = new AbsolutePanel();


   public Container() {
      super();
      initWidget(panel);
      addClickHandler(this);
      addDragHandler(this);
      addMouseWheelHandler(this);
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
   public HandlerRegistration addMouseWheelHandler(final MouseWheelHandler handler) {
      return addDomHandler(handler, MouseWheelEvent.getType());
   }


   @Override
   public void onClick(final ClickEvent event) {
      // TODO Auto-generated method stub

   }


   @Override
   public void onMouseWheel(final MouseWheelEvent event) {
      // TODO Auto-generated method stub

   }


   @Override
   public void onDrag(final DragEvent event) {
      // TODO Auto-generated method stub

   }


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


}
