

package planarviewer.client;

import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasMouseWheelHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;


public class Container
         extends
            Composite
         implements
            HasAllTouchHandlers,
            HasMouseWheelHandlers,
            HasMouseDownHandlers,
            HasMouseUpHandlers,
            HasMouseMoveHandlers {

   private final AbsolutePanel _panel    = new AbsolutePanel();
   private final SimplePanel   _topPanel = new SimplePanel();


   public Container() {
      super();
      initWidget(_panel);
      DOM.setIntStyleAttribute(_topPanel.getElement(), "zIndex", 50);
      //_topPanel.setVisible(true);
      setWidget(_topPanel, 0, 0);
   }


   @Override
   public HandlerRegistration addMouseWheelHandler(final MouseWheelHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, MouseWheelEvent.getType());
      return hr;
   }


   @Override
   public HandlerRegistration addMouseMoveHandler(final MouseMoveHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, MouseMoveEvent.getType());
      return hr;
   }


   @Override
   public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, MouseDownEvent.getType());
      return hr;
   }


   @Override
   public HandlerRegistration addMouseUpHandler(final MouseUpHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, MouseUpEvent.getType());
      return hr;
   }


   @Override
   public HandlerRegistration addTouchStartHandler(final TouchStartHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, TouchStartEvent.getType());
      return hr;
   }


   @Override
   public HandlerRegistration addTouchMoveHandler(final TouchMoveHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, TouchMoveEvent.getType());
      return hr;
   }


   @Override
   public HandlerRegistration addTouchEndHandler(final TouchEndHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, TouchEndEvent.getType());
      return hr;
   }


   @Override
   public HandlerRegistration addTouchCancelHandler(final TouchCancelHandler handler) {
      final HandlerRegistration hr = _topPanel.addDomHandler(handler, TouchCancelEvent.getType());
      return hr;
   }


   public void setWidget(final Widget widget,
                         final int left,
                         final int top) {
      _panel.add(widget, left, top);
   }


   public void remove(final Widget widget) {
      _panel.remove(widget);
   }


   public void setSize(final int width,
                       final int height) {
      super.setSize(Integer.toString(width) + "px", Integer.toString(height) + "px");
      _topPanel.setSize(Integer.toString(width) + "px", Integer.toString(height) + "px");
   }


   public void clear() {
      _panel.clear();
   }


}
