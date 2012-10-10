

package planarviewer.client;

import java.util.ArrayList;
import java.util.List;

import com.gargoylesoftware.htmlunit.javascript.host.Event;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DragEndEvent;
import com.google.gwt.event.dom.client.DragEndHandler;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.DragStartEvent;
import com.google.gwt.event.dom.client.DragStartHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;


public class GPlanarPanoramicViewer
         extends
            Container {

   //private static final ILogger LOGGER               = ILogger.instance();
   //private static Logger        logger               = Logger.getLogger("NameOfYourLogger");
   //GWT.log("ERROR");

   private static final int HORIZONTAL_INCREMENT = GPlanarPanoramicZoomLevel.TILE_WIDTH / 3;
   private static final int VERTICAL_INCREMENT   = GPlanarPanoramicZoomLevel.TILE_HEIGHT / 3;
   private static final int BUTTONEXTEND         = 20;
   private static final int BUTTONMARGIN         = 2;


   private class Tile {

      final Container                         _container;
      private final GPlanarPanoramicZoomLevel _zoomLevel;
      private final int                       _x;
      private final int                       _y;
      private GImage                          _image;
      private final String                    _tileUrl;
      private boolean                         _removeWhileLoading;
      private int                             _xPos, _yPos;
      private final GRectangle                _pixelsBounds;


      private Tile(final Container container,
                   final GPlanarPanoramicZoomLevel zoomLevel,
                   final int x,
                   final int y) {

         _zoomLevel = zoomLevel;
         _x = x;
         _y = y;
         _container = container;

         _removeWhileLoading = false;
         _tileUrl = getTileUrl();
         _pixelsBounds = initializePixelsBounds();
         _xPos = calculateXPosition();
         _yPos = calculateYPosition();
      }


      private GRectangle initializePixelsBounds() {
         return new GRectangle(getPixelXInPanoramic(), getPixelYInPanoramic(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private void positionate() {
         _xPos = calculateXPosition();
         _yPos = calculateYPosition();
         tryToLoadImage();
      }


      private GRectangle calculateBounds() {
         return new GRectangle(calculateXPosition(), calculateYPosition(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private int getPixelXInPanoramic() {
         return (_x * GPlanarPanoramicZoomLevel.TILE_WIDTH);
      }


      private int getPixelYInPanoramic() {
         return (_y * GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private int calculateXPosition() {
         return _offsetX + getPixelXInPanoramic();
      }


      private int calculateYPosition() {
         return _offsetY + getPixelYInPanoramic();
      }


      private void tryToLoadImage() {
         //GImageLoader.load(getTileUrl(), new OnLoadImageHandler(_x, _y));
         _removeWhileLoading = false;
         if (GImageLoader.isDownloadingImage(_tileUrl)) {
            return;
         }
         //final GRectangle bounds = calculateBounds();
         //GImageLoader.load(_tileUrl, new OnLoadImageHandler(bounds._x, bounds._y));
         GImageLoader.load(_tileUrl, new OnLoadImageHandler());
         //_image = new GImage(_tileUrl);
      }


      //TODO
      private void tryToLoadImage(final OnLoadImageHandler handler) {
         _removeWhileLoading = false;
         if (GImageLoader.isDownloadingImage(_tileUrl)) {
            return;
         }
         GImageLoader.load(_tileUrl, handler);
      }


      //      private void containerRepaint() {
      //         final Container parent = getParent();
      //         if (parent != null) {
      //            parent.repaint();
      //         }
      //
      //      }


      private void paintAncestor() {
         //TODO
         final Tile ancestor = getNearestAncestorWithTextureInCache();
         if (ancestor != null) {

            final int scale = (int) Math.pow(2, _zoomLevel.getLevel() - ancestor._zoomLevel.getLevel());
            final GDimension newSize = new GDimension(_pixelsBounds.getSize().getWidth() * scale,
                     _pixelsBounds.getSize().getHeight() * scale);

            if (_debug) {
               System.out.println("Ancestor: " + ancestor.toString());
               System.out.println("Scale: " + scale);
               System.out.println("newSize: " + newSize.toString());
            }

            final GRectangle scaledAncestorBounds = ancestor._pixelsBounds.scale(scale);
            final int left = (_pixelsBounds._x - (scaledAncestorBounds._width * ancestor._x));
            final int top = (_pixelsBounds._y - (scaledAncestorBounds._height * ancestor._y));
            if (_debug) {
               System.out.println("left: " + left + ", top: " + top);
            }

            ancestor.tryToLoadImage(new OnLoadImageHandler(left, top, newSize));
         }

      }


      private boolean hasTextureInCache() {
         //return (_image != null) || _loader.canLoadFromLocalCache(getTileUrl());
         return (_image != null) || GImageLoader.isInLocalCache(getTileUrl());
      }


      private Tile getNearestAncestorWithTextureInCache() {
         final int zoomLevel = _zoomLevel.getLevel();

         if (zoomLevel <= 1) {
            return null;
         }

         final int parentLevel = zoomLevel - 1;
         final int parentX = _x / 2; // integer (truncated) division
         final int parentY = _y / 2; // integer (truncated) division

         final Tile parent = new Tile(_container, getZoomLevel(parentLevel), parentX, parentY);
         if (parent.hasTextureInCache()) {
            return parent;
         }

         return parent.getNearestAncestorWithTextureInCache();
      }


      private String getTileUrl() {
         return _url + "/" + Integer.toString(_zoomLevel.getLevel()) + "/" + "tile-" + _x + "-" + _y + ".jpg";
      }


      private void remove() {
         if (_image != null) {
            _image.setVisible(false);
            _container.remove(_image);
            _image.removeFromParent();
            //_container.clear();
            if (_debug) {
               System.out.println("Borrando: " + toString());
            }
         }
         if (GImageLoader.isDownloadingImage(_tileUrl)) {
            _removeWhileLoading = true;
         }
      }


      private boolean touches(final GRectangle bounds) {
         return bounds.intersects(calculateBounds());
      }


      @Override
      public String toString() {
         final String str = "Tile-" + _x + "-" + _y + " | " + "pos: (" + _xPos + "," + _yPos + ")";
         return str;
      }


      private String tileToString() {
         return toString();
      }

      private class OnLoadImageHandler
               implements
                  IImageLoadHandler {

         private final GDimension _imgSize;
         private final int        _left;
         private final int        _top;


         public OnLoadImageHandler() {
            this(0, 0, null);
         }


         public OnLoadImageHandler(final int left,
                                   final int top,
                                   final GDimension imgSize) {
            _left = left;
            _top = top;
            _imgSize = imgSize;
         }


         @Override
         public void imageLoaded(final GImageLoadEvent event) {

            if (event.isLoadFailed()) {
               GWT.log("Image failed to load: " + toString());
               if (_debug) {
                  System.out.println("Image failed to load: Paint ANCESTOR.");
               }
               paintAncestor();
            }
            else {
               if (_removeWhileLoading) {
                  _removeWhileLoading = false;
                  return;
               }
               //_image = new GImage(_tileUrl);
               //_image = new GImage(event.takeImage());
               _image = event.getImage();
               if (_imgSize != null) {
                  //_image.setVisibleRect(0, 0, _imgSize.getWidth(), _imgSize.getHeight());
                  //_image.setSize(_imgSize);
                  //_image.setFixedSize(_imgSize.getWidth() * 2, _imgSize.getHeight() * 2);
                  final AbsolutePanel testPanel = new AbsolutePanel();
                  _image.setSize(_imgSize);
                  testPanel.add(_image, _left, _top);
                  testPanel.setPixelSize(_imgSize.getWidth(), _imgSize.getHeight());
                  _container.setWidget(testPanel, _xPos, _yPos);
               }
               else {
                  _image.unsinkEvents(Event.DRAGDROP);
                  _image.unsinkEvents(Event.CLICK);
                  _image.unsinkEvents(Event.MOUSEDOWN);
                  _image.unsinkEvents(Event.MOUSEDRAG);
                  _image.unsinkEvents(Event.SELECT);
                  _container.setWidget(_image, _xPos, _yPos);
               }
               if (_debug) {
                  System.out.println("Loaded: " + tileToString());
               }
            }
         }
      }


   }

   // -- GPlanarPanoramicViewer ------------------------------------

   private final String                          _url;
   private final String                          _name;
   private final boolean                         _debug;

   private final List<GPlanarPanoramicZoomLevel> _zoomLevels        = new ArrayList<GPlanarPanoramicZoomLevel>();
   private int                                   _minLevel;
   private int                                   _maxLevel;
   private final List<Tile>                      _tiles             = new ArrayList<Tile>();

   private int                                   _currentLevel;
   private int                                   _offsetX           = 0;
   private int                                   _offsetY           = 0;

   private int                                   _dragLastXPosition = 0;
   private int                                   _dragLastYPosition = 0;
   private boolean                               _isDragging        = false;

   private PushButton                            _buttonZoomIn;
   private PushButton                            _buttonZoomOut;


   public GPlanarPanoramicViewer(final String url,
                                 final String name) {
      this(url, name, false);
   }


   //TODO: constructor
   public GPlanarPanoramicViewer(final String url,
                                 final String name,
                                 final boolean debug) {

      _url = url;
      _name = name;
      _debug = debug;

      super.setTitle(name);
      super.setVisible(true);
      super.setSize(getContainerSize().getWidth(), getContainerSize().getHeight());
      Window.enableScrolling(false);

      readZoomLevelsAndGo();

      //forceDownloadLevelOne();

      //      int minLevel = Integer.MAX_VALUE;
      //      int maxLevel = Integer.MIN_VALUE;
      //
      //      for (final GPlanarPanoramicZoomLevel zoomLevel : _zoomLevels) {
      //         final int currentLevel = zoomLevel.getLevel();
      //         minLevel = Math.min(minLevel, currentLevel);
      //         maxLevel = Math.max(maxLevel, currentLevel);
      //      }
      //
      //      _minLevel = minLevel;
      //      _maxLevel = maxLevel;
      //      _currentLevel = minLevel;
      //
      //      fillContainer();
      //      updateZoomLevelFromContainerSize(0);
   }


   private void fillContainer() {

      super.addMouseWheelHandler(_mouseWheelHandler);
      super.addDragStartHandler(_dragStartHandler);
      super.addDragEndHandler(_dragEndHandler);
      //super.addDragHandler(_dragHandler);
      //super.addMouseDownHandler(_mouseDownHandler);
      //super.addMouseMoveHandler(_mouseMoveHandler);
      //super.addMouseUpHandler(_mouseUpHandler);

      Window.addResizeHandler(new ResizeHandler() {

         @Override
         public void onResize(final ResizeEvent event) {
            if (_debug) {
               System.out.println("RESIZE EVENT !");
            }
            setSize(getContainerSize().getWidth(), getContainerSize().getHeight());
            updateZoomLevelFromContainerSize(_currentLevel);
            recreateZoomWidtgets(BUTTONEXTEND, BUTTONMARGIN);
         }
      });

      DOM.setIntStyleAttribute(this.getElement(), "border", 0);
      createHUD();
   }

   final MouseWheelHandler _mouseWheelHandler = new MouseWheelHandler() {

                                                 @Override
                                                 public void onMouseWheel(final MouseWheelEvent event) {
                                                    if (_debug) {
                                                       System.out.println("MOUSE WHEEL EVENT");
                                                    }
                                                    if (event.getDeltaY() < 0) {
                                                       //setZoomLevel(_currentLevel + 1, event.getClientX(), event.getClientY());
                                                       setZoomLevel(_currentLevel + 1, event.getX(), event.getY());
                                                    }
                                                    else {
                                                       //setZoomLevel(_currentLevel - 1, event.getClientX(), event.getClientY());
                                                       setZoomLevel(_currentLevel - 1, event.getX(), event.getY());
                                                    }
                                                    event.stopPropagation();
                                                 }
                                              };

   final DragStartHandler  _dragStartHandler  = new DragStartHandler() {

                                                 @Override
                                                 public void onDragStart(final DragStartEvent event) {
                                                    _dragLastXPosition = event.getNativeEvent().getScreenX();
                                                    _dragLastYPosition = event.getNativeEvent().getScreenY();
                                                    if (_debug) {
                                                       System.out.println("MOUSE DRAG-START EVENT");
                                                       System.out.println("pos: (" + _dragLastXPosition + ","
                                                                          + _dragLastYPosition + ")");
                                                    }
                                                 }
                                              };


   final DragEndHandler    _dragEndHandler    = new DragEndHandler() {

                                                 @Override
                                                 public void onDragEnd(final DragEndEvent event) {
                                                    final int deltaX = event.getNativeEvent().getScreenX() - _dragLastXPosition;
                                                    final int deltaY = event.getNativeEvent().getScreenY() - _dragLastYPosition;

                                                    if (_debug) {
                                                       System.out.println("MOUSE DRAG-END EVENT");
                                                       System.out.println("delta: (" + deltaX + "," + deltaY + ")");
                                                    }
                                                    setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                    //_isDragging = false;
                                                 }
                                              };

   final DragHandler       _dragHandler       = new DragHandler() {

                                                 @Override
                                                 public void onDrag(final DragEvent event) {
                                                    final int deltaX = event.getNativeEvent().getScreenX() - _dragLastXPosition;
                                                    final int deltaY = event.getNativeEvent().getScreenY() - _dragLastYPosition;
                                                    final int deltaCX = event.getNativeEvent().getClientX() - _dragLastXPosition;
                                                    final int deltaCY = event.getNativeEvent().getClientY() - _dragLastYPosition;

                                                    if (_debug) {
                                                       System.out.println("ON-DRAG EVENT");
                                                       System.out.println("pos: (" + event.getNativeEvent().getScreenX() + ","
                                                                          + event.getNativeEvent().getScreenY() + ")");
                                                       System.out.println("deltaScreen: (" + deltaX + "," + deltaY + ")");
                                                       System.out.println("deltaClient: (" + deltaCX + "," + deltaCY + ")");
                                                    }
                                                    //setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                 }
                                              };

   //                                            @Override
   //   public void dragMouseMoved(DragSourceDragEvent dsde) {
   //      
   //   }
   //
   //   final DragSourceMotionListener _dragSourceListener = new DragSourceMotionListener() {
   //
   //                                                         @Override
   //                                                         public void dragMouseMoved(final DragSourceDragEvent dsde) {
   //                                                            System.out.println("DRAG-MOVE EVENT");
   //                                                         }
   //                                                      };

   final MouseMoveHandler  _mouseMoveHandler  = new MouseMoveHandler() {

                                                 @Override
                                                 public void onMouseMove(final MouseMoveEvent event) {
                                                    if (_isDragging) {
                                                       final int deltaX = event.getNativeEvent().getScreenX()
                                                                          - _dragLastXPosition;
                                                       final int deltaY = event.getNativeEvent().getScreenY()
                                                                          - _dragLastYPosition;
                                                       _dragLastXPosition = event.getNativeEvent().getScreenX();
                                                       _dragLastYPosition = event.getNativeEvent().getScreenY();
                                                       if (_debug) {
                                                          System.out.println("ON-MOUSE-MOVE EVENT");
                                                          System.out.println("delta: (" + deltaX + "," + deltaY + ")");
                                                       }
                                                       setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                    }
                                                 }
                                              };


   final MouseDownHandler  _mouseDownHandler  = new MouseDownHandler() {

                                                 @Override
                                                 public void onMouseDown(final MouseDownEvent event) {
                                                    _dragLastXPosition = event.getNativeEvent().getScreenX();
                                                    _dragLastYPosition = event.getNativeEvent().getScreenY();
                                                    if (_debug) {
                                                       System.out.println("MOUSE-DOWN EVENT");
                                                       System.out.println("pos: (" + _dragLastXPosition + ","
                                                                          + _dragLastYPosition + ")");
                                                    }
                                                    _isDragging = true;
                                                 }
                                              };

   final MouseUpHandler    _mouseUpHandler    = new MouseUpHandler() {

                                                 @Override
                                                 public void onMouseUp(final MouseUpEvent event) {
                                                    if (_debug) {
                                                       System.out.println("MOUSE-UP EVENT");
                                                    }
                                                    _isDragging = false;
                                                 }
                                              };


   //   private void forceDownloadLevelOne() { //TODO
   //
   //      final GPlanarPanoramicZoomLevel levelOne = getZoomLevel(1);
   //
   //      if (_debug) {
   //         System.out.println("forceDownloadLevelOne: " + levelOne.toString());
   //      }
   //
   //      for (int x = 0; x < levelOne.getWidthInTiles(); x++) {
   //         for (int y = 0; y < levelOne.getHeightInTiles(); y++) {
   //            final Tile tile = new Tile(this, levelOne, x, y);
   //            tile.tryToLoadImage(); // level one has maximum priority for downloading
   //         }
   //      }
   //   }


   private GPlanarPanoramicZoomLevel getCurrentZoomLevel() {
      return getZoomLevel(_currentLevel);
   }


   private GPlanarPanoramicZoomLevel getZoomLevel(final int level) {
      if (_zoomLevels == null) {
         return null;
      }

      for (final GPlanarPanoramicZoomLevel zoomLevel : _zoomLevels) {
         if (zoomLevel.getLevel() == level) {
            return zoomLevel;
         }
      }

      return null;
   }


   private void readZoomLevelsAndGo() {

      final RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, _url + "/info.txt");

      rb.setCallback(new RequestCallback() {
         @Override
         public void onResponseReceived(final Request request,
                                        final Response response) {
            try {
               final int responseCode = response.getStatusCode() / 100;
               if (_url.startsWith("file:/") || (responseCode == 2)) {
                  //final JavaScriptObject valores = parseJson(response.getText());
                  final JSONValue values = JSONParser.parseLenient(response.getText());
                  if (values != null) {
                     final JSONArray valuesList = values.isArray();
                     for (int i = 0; i < valuesList.size(); i++) {
                        final JSONObject data = valuesList.get(i).isObject();
                        final GPlanarPanoramicZoomLevel level = new GPlanarPanoramicZoomLevel(
                                 (int) data.get("level").isNumber().doubleValue(),
                                 (int) data.get("width").isNumber().doubleValue(),
                                 (int) data.get("height").isNumber().doubleValue(),
                                 (int) data.get("widthInTiles").isNumber().doubleValue(),
                                 (int) data.get("heightInTiles").isNumber().doubleValue());
                        _zoomLevels.add(level);
                        if (_debug) {
                           System.out.println("valores: " + level.toString());
                        }
                     }

                     // Complete initialization on zoom-level data received 
                     int minLevel = Integer.MAX_VALUE;
                     int maxLevel = Integer.MIN_VALUE;

                     for (final GPlanarPanoramicZoomLevel zoomLevel : _zoomLevels) {
                        final int currentLevel = zoomLevel.getLevel();
                        minLevel = Math.min(minLevel, currentLevel);
                        maxLevel = Math.max(maxLevel, currentLevel);
                     }

                     _minLevel = minLevel;
                     _maxLevel = maxLevel;
                     _currentLevel = minLevel;

                     fillContainer();
                     updateZoomLevelFromContainerSize(0);
                  }
               }
               else {
                  GWT.log("HttpError#" + response.getStatusCode() + " - " + response.getText());
               }
            }
            catch (final Throwable e) {
               GWT.log("Exception: " + e.toString());
            }
         }


         @Override
         public void onError(final Request request,
                             final Throwable exception) {
            GWT.log("HttpError#" + request.toString() + "Exception: " + exception.toString());
         }
      });
      try {
         rb.send();
      }
      catch (final RequestException e) {
         GWT.log("RequestException: " + e.toString());
      }
   }


   //   /*
   //    * Takes in a trusted JSON String and evals it.
   //    * @param JSON String that you trust
   //    * @return JavaScriptObject that you can cast to an Overlay Type
   //    */
   //   private static native JavaScriptObject parseJson(String jsonStr) /*-{
   //		return eval(jsonStr);
   //   }-*/;


   private int calculateInitialLevel(final GDimension containerSize) {

      int result = _minLevel;

      final double currentWidth = containerSize.getWidth();
      final double currentHeight = containerSize.getHeight();

      for (int i = _minLevel + 1; i < _maxLevel; i++) {
         final GPlanarPanoramicZoomLevel currentLevel = getZoomLevel(i);
         if ((currentLevel.getWidth() <= currentWidth) && (currentLevel.getHeight() <= currentHeight)) {
            result = i;
         }
      }

      return result;
   }


   private void createHUD() {
      //      final int buttonExtent = 20;
      //      final int margin = 2;
      createNavigationButtons(BUTTONEXTEND, BUTTONMARGIN);
      createZoomWidgets(BUTTONEXTEND, BUTTONMARGIN);
   }


   private void createNavigationButtons(final int buttonExtent,
                                        final int margin) {

      final String buttonSize = asCssString(buttonExtent);

      // up button
      final Image imgUp = new Image("./IMG/go-up.png");
      imgUp.setSize(buttonSize, buttonSize);
      final PushButton buttonUp = new PushButton(imgUp);
      buttonUp.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            moveUp();
         }
      });
      buttonUp.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(buttonUp.getElement(), "zIndex", 101);
      super.setWidget(buttonUp, margin + buttonExtent, margin + 0);

      // down button
      final Image imgDown = new Image("./IMG/go-down.png");
      imgDown.setSize(buttonSize, buttonSize);
      final PushButton buttonDown = new PushButton(imgDown);
      buttonDown.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            moveDown();
         }
      });
      buttonDown.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(buttonDown.getElement(), "zIndex", 101);
      super.setWidget(buttonDown, margin + buttonExtent, margin + (buttonExtent * 2));

      // left button
      final Image imgLeft = new Image("./IMG/go-left.png");
      imgLeft.setSize(buttonSize, buttonSize);
      final PushButton buttonLeft = new PushButton(imgLeft);
      buttonLeft.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            moveLeft();
         }
      });
      buttonLeft.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(buttonLeft.getElement(), "zIndex", 101);
      super.setWidget(buttonLeft, margin + 0, margin + buttonExtent);

      // right button
      final Image imgRight = new Image("./IMG/go-right.png");
      imgRight.setSize(buttonSize, buttonSize);
      final PushButton buttonRight = new PushButton(imgRight);
      buttonRight.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            moveRight();
         }
      });
      buttonRight.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(buttonRight.getElement(), "zIndex", 101);
      super.setWidget(buttonRight, margin + (buttonExtent * 2), margin + buttonExtent);

   }


   private String asCssString(final int value) {
      return Integer.toString(value) + "px";
   }


   //   private void recreateZoomWidtgets() {
   //      super.remove(_buttonZoomIn);
   //      super.remove(_buttonZoomOut);
   //      createZoomWidgets(BUTTONEXTEND, BUTTONMARGIN);
   //   }


   private void createZoomWidgets(final int buttonExtent,
                                  final int margin) {

      final String buttonSize = asCssString(buttonExtent);
      final int rightPosition = getContainerSize().getWidth() - (buttonExtent) - margin;

      // zoom-in button
      final Image imgZoomIn = new Image("./IMG/zoom-in.png");
      imgZoomIn.setSize(buttonSize, buttonSize);
      _buttonZoomIn = new PushButton(imgZoomIn);
      _buttonZoomIn.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            setZoomLevel(_currentLevel + 1);
         }
      });
      _buttonZoomIn.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonZoomIn.getElement(), "zIndex", 101);
      //super.setWidget(_buttonZoomIn, margin + buttonExtent, margin + (buttonExtent * 4)); // at client left position
      super.setWidget(_buttonZoomIn, rightPosition, margin); // at client right position

      // zoom-out button
      final Image imgZoomOut = new Image("./IMG/zoom-out.png");
      imgZoomOut.setSize(buttonSize, buttonSize);
      _buttonZoomOut = new PushButton(imgZoomOut);
      _buttonZoomOut.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            setZoomLevel(_currentLevel - 1);
         }
      });
      _buttonZoomOut.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonZoomOut.getElement(), "zIndex", 101);
      //super.setWidget(_buttonZoomOut, margin + buttonExtent, margin + (buttonExtent * 5) + 40); // at client left position
      super.setWidget(_buttonZoomOut, rightPosition, margin + (buttonExtent * 2)); // at client right position
   }


   private void recreateZoomWidtgets(final int buttonExtent,
                                     final int margin) {

      final int rightPosition = getContainerSize().getWidth() - (buttonExtent) - margin;

      super.setWidget(_buttonZoomIn, rightPosition, margin); // at client right position
      super.setWidget(_buttonZoomOut, rightPosition, margin + (buttonExtent * 2)); // at client right position
   }


   private void setZoomLevel(final int newLevel) {
      final GRectangle containerBounds = getContainerBound();
      final int targetX = (int) containerBounds.getCenterX();
      final int targetY = (int) containerBounds.getCenterY();
      setZoomLevel(newLevel, targetX, targetY);
   }


   private void setZoomLevel(final int newLevel,
                             final int targetX,
                             final int targetY) {

      if (newLevel == _currentLevel) {
         return;
      }

      if ((newLevel < _minLevel) || (newLevel > _maxLevel)) {
         return;
      }

      final int oldLevel = _currentLevel;
      _currentLevel = newLevel;

      final double zoomFactor = Math.pow(2, _currentLevel - oldLevel);

      final double targetXForNewZoom = (targetX - _offsetX) * zoomFactor;
      final double targetYForNewZoom = (targetY - _offsetY) * zoomFactor;

      _offsetX = (int) (targetXForNewZoom - targetX) * -1;
      _offsetY = (int) (targetYForNewZoom - targetY) * -1;

      updateZoomWidgets();
      recreateTiles();
   }


   private void setOffset(final int offsetX,
                          final int offsetY) {

      if ((offsetX == _offsetX) && (offsetY == _offsetY)) {
         return;
      }

      _offsetX = offsetX;
      _offsetY = offsetY;

      //updateTilesGrid();
      //layoutTiles();
      recreateTiles(); // ??
   }


   //   private void updateTilesGrid() {
   //
   //      removeNotVisibleTiles();
   //
   //      final List<Tile> tilesToCreate = new ArrayList<Tile>();
   //
   //      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();
   //
   //      //      final GRectangle containerBounds = new GRectangle(0, 0, (int) container.getBounds().getWidth(),
   //      //               (int) container.getBounds().getHeight());
   //      final GRectangle containerBounds = getContainerBound();
   //
   //      for (int x = 0; x < currentZoomLevel.getWidthInTiles(); x++) {
   //         for (int y = 0; y < currentZoomLevel.getHeightInTiles(); y++) {
   //            final Tile tile = new Tile(this, currentZoomLevel, x, y);
   //            if (tile.touches(containerBounds)) {
   //               if (!hasTileInTheSamePosition(tile)) {
   //                  tilesToCreate.add(tile);
   //               }
   //            }
   //         }
   //      }
   //
   //      for (final Tile tileToCreate : tilesToCreate) {
   //         _tiles.add(tileToCreate);
   //         //container.add(tileToCreate);
   //         tileToCreate.positionate();
   //      }
   //
   //   }
   //
   //
   //   private boolean hasTileInTheSamePosition(final Tile tile) {
   //      for (final Tile each : _tiles) {
   //         if ((each._x == tile._x) && (each._y == tile._y)) {
   //            return true;
   //         }
   //      }
   //      return false;
   //   }
   //
   //
   //   private void removeNotVisibleTiles() {
   //      //      final GRectangle containerBounds = new GRectangle(0, 0, (int) container.getBounds().getWidth(),
   //      //               (int) container.getBounds().getHeight());
   //
   //      final GRectangle containerBounds = getContainerBound();
   //
   //      final Iterator<Tile> iterator = _tiles.iterator();
   //      while (iterator.hasNext()) {
   //         final Tile tile = iterator.next();
   //         if (!tile.touches(containerBounds)) {
   //            tile.remove();
   //            iterator.remove();
   //            //container.remove(tile);
   //         }
   //      }
   //   }


   private GDimension getContainerSize() {

      return getContainerBound().getSize();
   }


   private GRectangle getContainerBound() {
      final int containerWidth = Window.getClientWidth();
      final int containerHeight = Window.getClientHeight();

      final GRectangle containerBounds = new GRectangle(containerWidth, containerHeight);

      return containerBounds;
   }


   private void updateZoomWidgets() {
      _buttonZoomIn.setEnabled(_currentLevel < _maxLevel);
      _buttonZoomOut.setEnabled(_currentLevel > _minLevel);
      //_zoomSlider.setValue(_currentLevel);
   }


   private void recreateTiles() {
      //      removeTiles(container);
      //      createTiles(container);
      //      addTiles(container);
      if (_debug) {
         System.out.println("Quitando..");
      }
      removeTiles();
      if (_debug) {
         System.out.println("Creando..");
      }
      createTiles();
      //addTiles();
      if (_debug) {
         System.out.println("Posicionando..");
      }
      layoutTiles();

      // force redraw
      //      container.invalidate();
      //      container.doLayout();
      //      container.repaint();
   }


   //   private void addTiles() {
   //      for (final Tile tile : _tiles) {
   //         //container.add(tile);
   //      }
   //   }


   private void layoutTiles() {
      for (final Tile tile : _tiles) {
         tile.positionate();
      }
   }


   private void createTiles() {
      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();

      //      final GRectangle containerBounds = new GRectangle(0, 0, (int) container.getBounds().getWidth(),
      //               (int) container.getBounds().getHeight());

      final GRectangle containerBounds = getContainerBound();

      for (int x = 0; x < currentZoomLevel.getWidthInTiles(); x++) {
         for (int y = 0; y < currentZoomLevel.getHeightInTiles(); y++) {
            final Tile tile = new Tile(this, currentZoomLevel, x, y);
            if (tile.touches(containerBounds)) {
               _tiles.add(tile);
            }
         }
      }
      if (_debug) {
         System.out.println("Level: " + currentZoomLevel.getLevel() + ", NUM Tiles: " + _tiles.size());
         System.out.println("Width: " + containerBounds.getWidth() + ", Height: " + containerBounds.getHeight());
      }

   }


   private void removeTiles() {
      for (final Tile tile : _tiles) {
         tile.remove();
         //container.remove(tile);
      }
      //super.clear();
      _tiles.clear();
   }


   //   private void setPosition(final Component component,
   //                            final int x,
   //                            final int y) {
   //      final Dimension size = component.getPreferredSize();
   //      component.setBounds(x, y, size.width, size.height);
   //   }
   //

   private void moveDown() {
      setOffset(_offsetX, _offsetY - VERTICAL_INCREMENT);
   }


   private void moveUp() {
      setOffset(_offsetX, _offsetY + VERTICAL_INCREMENT);
   }


   private void moveLeft() {
      setOffset(_offsetX + HORIZONTAL_INCREMENT, _offsetY);
   }


   private void moveRight() {
      setOffset(_offsetX - HORIZONTAL_INCREMENT, _offsetY);
   }


   private void updateZoomLevelFromContainerSize(final int initialZoomLevelIncrement) {

      //final Dimension containerSize = container.getSize();
      final GDimension containerSize = getContainerSize();

      if (initialZoomLevelIncrement > 0) {
         _currentLevel = clamp(initialZoomLevelIncrement, _minLevel, _maxLevel);
      }
      else {
         _currentLevel = calculateInitialLevel(containerSize);
      }

      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();
      _offsetX = (containerSize.getWidth() - currentZoomLevel.getWidth()) / 2;
      _offsetY = (containerSize.getHeight() - currentZoomLevel.getHeight()) / 2;
      if (_debug) {
         System.out.println("Updating zoomLevel from container size. ZoomLevel: " + currentZoomLevel);
         System.out.println("_offsetX: " + _offsetX + " ,_offsetY: " + _offsetY);
      }

      updateZoomWidgets();
      recreateTiles();
   }


   private int clamp(final int value,
                     final int min,
                     final int max) {
      return (value < min) ? min : ((value > max) ? max : value);
   }


   @Override
   public String toString() {
      return "GPlanarPanoramicViewer [name=\"" + _name + "\", url=" + _url + ", levels=" + _minLevel + "->" + _maxLevel + "]";
   }


   public String getUrl() {
      return _url;
   }


   public String getName() {
      return _name;
   }


   public int getMinLevel() {
      return _minLevel;
   }


   public int getMaxLevel() {
      return _maxLevel;
   }


}
