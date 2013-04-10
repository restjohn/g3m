

package planarviewer.client;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
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
import com.google.gwt.user.client.ui.Widget;


public class GContainer
         extends
            Composite
         implements
            HasAllTouchHandlers,
            HasMouseWheelHandlers,
            HasMouseDownHandlers,
            HasMouseUpHandlers,
            HasMouseMoveHandlers,
            HasDoubleClickHandlers {

   private final AbsolutePanel _panel    = new AbsolutePanel();
   //private final AbsolutePanel _controlPanel = new AbsolutePanel();
   //private final AbsolutePanel _debugPanel = new AbsolutePanel();
   private final AbsolutePanel _topPanel = new AbsolutePanel();


   public GContainer() {
      super();
      initWidget(_panel);
      //DOM.setIntStyleAttribute(_controlPanel.getElement(), "zIndex", 49);
      DOM.setIntStyleAttribute(_panel.getElement(), "border", 0);
      //DOM.setStyleAttribute(_panel.getElement(), "background", "black");
      DOM.setIntStyleAttribute(_topPanel.getElement(), "zIndex", 50);
      //setWidget(_topPanel, 0, 0);
      //_panel.add(_controlPanel, 0, 0);
      //DOM.setIntStyleAttribute(_debugPanel.getElement(), "zIndex", 101);
      _panel.add(_topPanel, 0, 0);
      //_panel.add(_debugPanel, 0, 0);
      DOM.setElementAttribute(_panel.getElement(), "id", "planarviewer");
   }


   @Override
   public HandlerRegistration addMouseWheelHandler(final MouseWheelHandler handler) {
      return _topPanel.addDomHandler(handler, MouseWheelEvent.getType());
   }


   @Override
   public HandlerRegistration addMouseMoveHandler(final MouseMoveHandler handler) {
      return _topPanel.addDomHandler(handler, MouseMoveEvent.getType());
   }


   @Override
   public HandlerRegistration addMouseDownHandler(final MouseDownHandler handler) {
      return _topPanel.addDomHandler(handler, MouseDownEvent.getType());
   }


   @Override
   public HandlerRegistration addMouseUpHandler(final MouseUpHandler handler) {
      return _topPanel.addDomHandler(handler, MouseUpEvent.getType());
   }


   @Override
   public HandlerRegistration addTouchStartHandler(final TouchStartHandler handler) {
      return _topPanel.addDomHandler(handler, TouchStartEvent.getType());
   }


   @Override
   public HandlerRegistration addTouchMoveHandler(final TouchMoveHandler handler) {
      return _topPanel.addDomHandler(handler, TouchMoveEvent.getType());
   }


   @Override
   public HandlerRegistration addTouchEndHandler(final TouchEndHandler handler) {
      return _topPanel.addDomHandler(handler, TouchEndEvent.getType());
   }


   @Override
   public HandlerRegistration addTouchCancelHandler(final TouchCancelHandler handler) {
      return _topPanel.addDomHandler(handler, TouchCancelEvent.getType());
   }


   @Override
   public HandlerRegistration addDoubleClickHandler(final DoubleClickHandler handler) {
      return _topPanel.addDomHandler(handler, DoubleClickEvent.getType());
   }


   public void addWidget(final Widget widget,
                         final int left,
                         final int top) {

      if (widget != null) {
         _panel.add(widget, left, top);
      }
   }


   public void setWidget(final Widget widget,
                         final int left,
                         final int top) {

      if ((widget != null) && (_panel == widget.getParent())) {
         _panel.setWidgetPosition(widget, left, top);
      }
   }


   public void addImage(final Widget widget,
                        final int left,
                        final int top) {

      if (widget != null) {
         _panel.add(widget, left, top);
      }
   }


   public void setImage(final Widget widget,
                        final int left,
                        final int top) {

      if ((widget != null) && (_panel == widget.getParent())) {
         _panel.setWidgetPosition(widget, left, top);
      }
   }


   public void addTopWidget(final Widget widget,
                            final int left,
                            final int top) {

      if (widget != null) {
         _topPanel.add(widget, left, top);
      }
   }


   public void setTopWidget(final Widget widget,
                            final int left,
                            final int top) {

      if ((widget != null) && (_topPanel == widget.getParent())) {
         _topPanel.setWidgetPosition(widget, left, top);
      }
   }


   public void updateImage(final Widget widget,
                           final int left,
                           final int top,
                           final int width,
                           final int height) {

      if ((widget != null) && (_panel == widget.getParent())) {
         widget.setSize(width + "px", height + "px");
         _panel.setWidgetPosition(widget, left, top);
      }
   }


   public void remove(final Widget widget) {

      if (widget != null) {
         _panel.remove(widget);
      }
   }


   public void setSize(final int width,
                       final int height) {

      final String widthS = Integer.toString(width) + "px";
      final String heightS = Integer.toString(height) + "px";
      super.setSize(widthS, heightS);
      _panel.setSize(widthS, heightS);
      _topPanel.setSize(widthS, heightS);
   }


   public void clear() {

      _panel.clear();
      //_panel.add(_controlPanel, 0, 0);
      _panel.add(_topPanel, 0, 0);
   }


   public void doRender() {
      //render("planarviewer.planarviewer");;
      render("planarviewer");
      //Event.trigger(_panel.getElement(), "resize");
      //ResizeEvent.fire(this, _panel.getOffsetWidth(), _panel.getOffsetHeight());
   }


   //   @Override
   //   public HandlerRegistration addResizeHandler(final ResizeHandler handler) {
   //      //return this.addDomHandler(handler, ResizeEvent.getType());
   //      return Window.addResizeHandler(handler);
   //      //return null;
   //   }

   //   public void addDebugLabel(final Widget widget,
   //                             final int left,
   //                             final int top) {
   //      //_debugPanel.add(widget, left, top);
   //      _topPanel.add(widget, left, top);
   //   }


   //   public void forceReloadOnIos() {
   //      if (_counterIos == 0) {
   //         //DOM.setIntStyleAttribute(_panel.getElement(), "left", 1);
   //         //DOM.setIntStyleAttribute(_panel.getElement(), "left", 0);
   //         DOM.setIntStyleAttribute(_topPanel.getElement(), "left", 1);
   //         _counterIos++;
   //      }
   //   }

}
