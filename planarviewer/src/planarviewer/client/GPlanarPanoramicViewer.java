

package planarviewer.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;


public class GPlanarPanoramicViewer
         extends
            GContainer {

   //private static final ILogger LOGGER               = ILogger.instance();
   //private static Logger        logger               = Logger.getLogger("NameOfYourLogger");
   //GWT.log("ERROR");

   private static final int    HORIZONTAL_INCREMENT  = GPlanarPanoramicZoomLevel.TILE_WIDTH / 3;
   private static final int    VERTICAL_INCREMENT    = GPlanarPanoramicZoomLevel.TILE_HEIGHT / 3;
   private static final int    BUTTONEXTEND          = 48;
   private static final int    BUTTONMARGIN          = 2;
   private static final int    MIN_TOUCH_MOVE_EVENTS = 3;
   private static final int    ZOOM_SCALE_DELTA      = 10;
   private static final int    MIN_OFFSET_DISTANCE   = 6;
   private static final double NEIGHBORN_FACTOR      = 0.8;


   private class Tile {

      final GContainer                        _container;
      private final GPlanarPanoramicZoomLevel _zoomLevel;
      private final int                       _x;
      private final int                       _y;
      private GImage                          _image = null;
      private final String                    _tileUrl;
      //private boolean                         _removeWhileLoading;
      private int                             _xPos, _yPos;
      private final GRectangle                _pixelsBounds;
      private final Tile                      _thisTile;


      private Tile(final GContainer container,
                   final GPlanarPanoramicZoomLevel zoomLevel,
                   final int x,
                   final int y) {

         _zoomLevel = zoomLevel;
         _x = x;
         _y = y;
         _container = container;

         //_removeWhileLoading = false;
         _tileUrl = getTileUrl();
         _pixelsBounds = initializePixelsBounds();
         _xPos = calculateXPosition();
         _yPos = calculateYPosition();
         _thisTile = this;
      }


      private GRectangle initializePixelsBounds() {
         return new GRectangle(getPixelXInPanoramic(), getPixelYInPanoramic(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private void positionate() {
         _xPos = calculateXPosition();
         _yPos = calculateYPosition();
         if (_image == null) {
            tryToLoadImage();
         }
         else {
            _container.setImage(_image, _xPos, _yPos);
         }

      }


      private GRectangle calculateBounds() {
         return new GRectangle(calculateXPosition(), calculateYPosition(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private GRectangle calculateExtendedBounds() {
         return new GRectangle(calculateXPosition() - GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  calculateYPosition() - GPlanarPanoramicZoomLevel.TILE_HEIGHT, 3 * GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  3 * GPlanarPanoramicZoomLevel.TILE_HEIGHT);
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

         if (GImageLoader.isDownloadingImage(_tileUrl)) {
            //_removeWhileLoading = false;
            return;
         }

         GImageLoader.load(_tileUrl, new OnLoadImageHandler());
      }


      //TODO
      private void tryToLoadImage(final OnLoadImageHandler handler) {

         if (GImageLoader.isDownloadingImage(_tileUrl)) {
            //_removeWhileLoading = false;
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

      private void resizeWhileDownloading(final int newZoomLevel) {

         if (_zoomLevel.getLevel() == newZoomLevel) {
            return;
         }

         if (_image != null) {
            final double scale = Math.pow(2, newZoomLevel - _zoomLevel.getLevel());

            if (_debug) {
               _logger.logInfo("Scale: " + scale, _thisTile);
            }

            _xPos = _offsetX + (int) (_x * GPlanarPanoramicZoomLevel.TILE_WIDTH * scale);
            _yPos = _offsetY + (int) (_y * GPlanarPanoramicZoomLevel.TILE_HEIGHT * scale);
            final int width = (int) (_image.getOriginalWidth() * scale);
            final int height = (int) (_image.getOriginalHeight() * scale);
            _container.updateImage(_image, _xPos, _yPos, width, height);
         }
      }


      private void paintAncestor() {
         //TODO
         final Tile ancestor = getNearestAncestorWithTextureInCache();
         if (ancestor != null) {

            final int scale = (int) Math.pow(2, _zoomLevel.getLevel() - ancestor._zoomLevel.getLevel());
            final GDimension newSize = new GDimension(_pixelsBounds.getSize().getWidth() * scale,
                     _pixelsBounds.getSize().getHeight() * scale);

            if (_debug) {
               _logger.logInfo("Ancestor: " + ancestor.toString(), _thisTile);
               _logger.logInfo("Scale: " + scale, _thisTile);
               _logger.logInfo("newSize: " + newSize.toString(), _thisTile);
            }

            //            final GRectangle scaledAncestorBounds = ancestor._pixelsBounds.scale(scale);
            //            final int left = (_pixelsBounds._x - (scaledAncestorBounds._width * ancestor._x));
            //            final int top = (_pixelsBounds._y - (scaledAncestorBounds._height * ancestor._y));
            final int scaledAncestorX = ancestor._pixelsBounds._x * scale;
            final int scaledAncestorY = ancestor._pixelsBounds._y * scale;
            final int left = (scaledAncestorX - _pixelsBounds._x);
            final int top = (scaledAncestorY - _pixelsBounds._y);


            if (_debug) {
               _logger.logInfo("left: " + left + ", top: " + top, _thisTile);
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
            if (_image.getParent() == _container) {
               _container.remove(_image);
            }
            else { // to remove image ancestors
               _container.remove(_image.getParent());
               _image.removeFromParent();
            }
            //            _image.removeFromParent();
            //            _container.clear();
            if (_debug) {
               System.out.println("Borrando: " + tileToString());
               _logger.logInfo("Borrando: " + tileToString(), _thisTile);
            }
         }
         //         else if (GImageLoader.isDownloadingImage(_tileUrl)) {
         //            _removeWhileLoading = true;
         //         }
      }


      private boolean touches(final GRectangle bounds) {
         return bounds.intersects(calculateBounds());
      }


      private boolean touchesNeighborn(final GRectangle bounds) {
         return bounds.intersects(calculateExtendedBounds());
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
               _logger.logInfo("Image failed to load: " + "Level: " + _currentLevel + " " + tileToString(), _thisTile);
               if (_debug) {
                  _logger.logInfo("Image failed to load: " + "Level: " + _currentLevel + " " + tileToString(), _thisTile);
                  _logger.logInfo("Paint ANCESTOR.", _thisTile);
               }
               paintAncestor();
            }
            else {
               _numTilesToDownload--;
               //               if (_removeWhileLoading) {
               //                  if (_debug) {
               //                     System.out.println("Delete while downloading");
               //                  }
               //                  _removeWhileLoading = false;
               //                  return;
               //               }

               if (_zoomLevel.getLevel() != _currentLevel) {
                  if (_debug) {
                     _logger.logInfo("> Diferent level tile.. ", _thisTile);
                  }
                  return;
               }

               if (!hasTileInTheSamePosition(_thisTile)) { // == if (!_tiles.contains(_thisTile)) 
                  if (_debug) {
                     _logger.logInfo("> Not contained tile pos.. ", _thisTile);
                  }
                  return;
               }

               _image = event.getImage();
               if (_imgSize != null) {
                  final AbsolutePanel cropPanel = new AbsolutePanel();
                  _image.setSize(_imgSize);
                  cropPanel.add(_image, _left, _top);
                  cropPanel.setPixelSize(GPlanarPanoramicZoomLevel.TILE_WIDTH, GPlanarPanoramicZoomLevel.TILE_HEIGHT);
                  _container.addImage(cropPanel, _xPos, _yPos);
               }
               else {
                  _container.addImage(_image, _xPos, _yPos);
               }

               if (_numTilesToDownload == 0) {
                  //_progressInd.setVisible(false);
                  if (_debug) {
                     _logger.logInfo("Deleting previous tiles: " + _tilesToRemove.size(), _thisTile);
                  }
                  for (final List<Tile> tileList : _tilesToRemove) {
                     for (final Tile tile : tileList) {
                        tile.remove();
                     }
                  }
                  _tilesToRemove.clear();
               }

               if (_debug) {
                  _logger.logInfo("Loaded: " + tileToString(), _thisTile);
               }
            }
         }
      }

   } // end class Tile

   // -- GPlanarPanoramicViewer ------------------------------------

   private final String                          _url;
   private final String                          _name;
   private final boolean                         _debug;

   private final List<GPlanarPanoramicZoomLevel> _zoomLevels          = new ArrayList<GPlanarPanoramicZoomLevel>();
   private int                                   _minLevel;
   private int                                   _maxLevel;
   private final List<Tile>                      _tiles               = new ArrayList<Tile>();

   private int                                   _currentLevel;
   private int                                   _offsetX             = 0;
   private int                                   _offsetY             = 0;

   private int                                   _dragLastXPosition   = 0;
   private int                                   _dragLastYPosition   = 0;
   private boolean                               _isDragging          = false;

   private int                                   _touch0LastXPosition = 0;
   private int                                   _touch0LastYPosition = 0;
   private double                                _touchesDistance     = 0;
   private double                                _referenceScale      = 0;
   private boolean                               _isScaling           = false;
   private int                                   _touchMoveCounter    = 0;

   private PushButton                            _buttonZoomIn;
   private PushButton                            _buttonZoomOut;
   private PushButton                            _buttonUp;
   private PushButton                            _buttonDown;
   private PushButton                            _buttonLeft;
   private PushButton                            _buttonRight;

   //private Image                                 _progressInd;
   List<List<Tile>>                              _tilesToRemove       = new ArrayList<List<Tile>>();
   private int                                   _numTilesToDownload  = 0;
   private final Logger_WebGL                    _logger;


   public GPlanarPanoramicViewer(final String url,
                                 final String name) {
      this(url, name, false);
   }


   //TODO: constructor 
   public GPlanarPanoramicViewer(final String url,
                                 final String name,
                                 final boolean debug) {

      _logger = new Logger_WebGL(LogLevel.InfoLevel);
      _url = url;
      _name = name;
      _debug = debug;

      super.setTitle(name);
      super.setVisible(true);
      super.setSize(getContainerSize().getWidth(), getContainerSize().getHeight());
      Window.enableScrolling(false);

      //System.out.println("Starting up .. ");
      _logger.logInfo("Starting up .. ", this);
      //createProgressIndicator();
      readZoomLevelsAndGo();
   }


   private void fillContainer() {

      addMouseWheelHandler(_mouseWheelHandler);

      addMouseDownHandler(_mouseDownHandler);
      addMouseMoveHandler(_mouseMoveHandler);
      addMouseUpHandler(_mouseUpHandler);
      sinkEvents(Event.MOUSEEVENTS);

      addTouchStartHandler(_touchStartHandler);
      addTouchMoveHandler(_touchMoveHandler);
      addTouchEndHandler(_touchEndHandler);
      sinkEvents(Event.TOUCHEVENTS);

      addDoubleClickHandler(_doubleClickHandler);
      sinkEvents(Event.ONDBLCLICK);

      Window.addResizeHandler(new ResizeHandler() {

         @Override
         public void onResize(final ResizeEvent event) {
            setSize(getContainerSize().getWidth(), getContainerSize().getHeight());
            updateZoomLevelFromContainerSize(_currentLevel);
            //recreateTiles();
            recreateWidgets(BUTTONEXTEND, BUTTONMARGIN);
         }
      });

      createHUD();
   }

   final MouseWheelHandler  _mouseWheelHandler  = new MouseWheelHandler() {

                                                   @Override
                                                   public void onMouseWheel(final MouseWheelEvent event) {
                                                      event.preventDefault();
                                                      if (_debug) {
                                                         _logger.logInfo("MOUSE-WHEEL EVENT", this);
                                                      }
                                                      if (event.getDeltaY() < 0) {
                                                         setZoomLevel(_currentLevel + 1, event.getX(), event.getY());
                                                      }
                                                      else {
                                                         setZoomLevel(_currentLevel - 1, event.getX(), event.getY());
                                                      }

                                                   }
                                                };


   final MouseDownHandler   _mouseDownHandler   = new MouseDownHandler() {

                                                   @Override
                                                   public void onMouseDown(final MouseDownEvent event) {
                                                      event.preventDefault();
                                                      _dragLastXPosition = event.getNativeEvent().getScreenX();
                                                      _dragLastYPosition = event.getNativeEvent().getScreenY();

                                                      if (_debug) {
                                                         _logger.logInfo("MOUSE-DOWN EVENT: X=" + _dragLastXPosition + ", Y="
                                                                         + _dragLastYPosition, this);
                                                      }
                                                      _isDragging = true;
                                                   }
                                                };

   final MouseMoveHandler   _mouseMoveHandler   = new MouseMoveHandler() {

                                                   @Override
                                                   public void onMouseMove(final MouseMoveEvent event) {
                                                      event.preventDefault();
                                                      if (_isDragging) {
                                                         final int deltaX = event.getNativeEvent().getScreenX()
                                                                            - _dragLastXPosition;
                                                         final int deltaY = event.getNativeEvent().getScreenY()
                                                                            - _dragLastYPosition;
                                                         _dragLastXPosition = event.getNativeEvent().getScreenX();
                                                         _dragLastYPosition = event.getNativeEvent().getScreenY();

                                                         if (_debug) {
                                                            _logger.logInfo("MOUSE-MOVE EVENT: deltaX=" + deltaX + ", deltaY="
                                                                            + deltaY, this);
                                                         }
                                                         setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                      }
                                                   }
                                                };


   final MouseUpHandler     _mouseUpHandler     = new MouseUpHandler() {

                                                   @Override
                                                   public void onMouseUp(final MouseUpEvent event) {
                                                      event.preventDefault();

                                                      if (_debug) {
                                                         _logger.logInfo("MOUSE-UP EVENT", this);
                                                      }
                                                      _isDragging = false;
                                                   }
                                                };

   final TouchStartHandler  _touchStartHandler  = new TouchStartHandler() {

                                                   @Override
                                                   public void onTouchStart(final TouchStartEvent event) {
                                                      event.preventDefault();
                                                      _touchMoveCounter = 0;
                                                      final JsArray<Touch> touches = event.getTargetTouches();
                                                      if (touches.length() > 1) {
                                                         _isScaling = true;
                                                      }
                                                      if (_isScaling) {
                                                         final Touch t0 = touches.get(0);
                                                         final Touch t1 = touches.get(1);
                                                         final int deltaX = t1.getScreenX() - t0.getScreenX();
                                                         final int deltaY = t1.getScreenY() - t0.getScreenY();
                                                         _touchesDistance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
                                                         //                                                       _referenceScale = Math.max(getContainerSize().getHeight(),
                                                         //                                                                getContainerSize().getWidth());
                                                         _referenceScale = getContainerSize().getHeight();
                                                         if (_debug) {
                                                            _logger.logInfo("TOUCHES distance: " + _touchesDistance, this);
                                                         }
                                                      }
                                                      else {
                                                         final Touch t = touches.get(0);
                                                         _touch0LastXPosition = t.getScreenX();
                                                         _touch0LastYPosition = t.getScreenY();
                                                         if (_debug) {
                                                            _logger.logInfo("TOUCH-START EVENT: x=" + _touch0LastXPosition
                                                                            + ", y=" + _touch0LastYPosition + _touchesDistance,
                                                                     this);
                                                         }
                                                      }
                                                   }
                                                };

   final TouchMoveHandler   _touchMoveHandler   = new TouchMoveHandler() {

                                                   @Override
                                                   public void onTouchMove(final TouchMoveEvent event) {
                                                      event.preventDefault();
                                                      // filter because there are too many touch-move events
                                                      _touchMoveCounter++;
                                                      if (_touchMoveCounter < MIN_TOUCH_MOVE_EVENTS) {
                                                         return;
                                                      }
                                                      _touchMoveCounter = 0;
                                                      final JsArray<Touch> touches = event.getTargetTouches();
                                                      if (_isScaling) {
                                                         final Touch t0 = touches.get(0);
                                                         final Touch t1 = touches.get(1);
                                                         final int deltaX = t1.getScreenX() - t0.getScreenX();
                                                         final int deltaY = t1.getScreenY() - t0.getScreenY();
                                                         final double currentDistance = Math.sqrt(Math.pow(deltaX, 2)
                                                                                                  + Math.pow(deltaY, 2));
                                                         final double distanceDelta = currentDistance - _touchesDistance;
                                                         if (_debug) {
                                                            _logger.logInfo("delta distance: " + distanceDelta, this);
                                                         }
                                                         if (distanceDelta > (_referenceScale / ZOOM_SCALE_DELTA)) {
                                                            _touchesDistance = currentDistance;
                                                            setZoomLevel(_currentLevel + 1);
                                                         }
                                                         else if (distanceDelta < -(_referenceScale / ZOOM_SCALE_DELTA)) {
                                                            _touchesDistance = currentDistance;
                                                            setZoomLevel(_currentLevel - 1);
                                                         }

                                                      }
                                                      else {
                                                         final Touch t = touches.get(0);
                                                         int deltaX = t.getScreenX() - _touch0LastXPosition;
                                                         int deltaY = t.getScreenY() - _touch0LastYPosition;

                                                         if (_debug) {
                                                            _logger.logInfo("TOUCH-MOVE EVENT: deltaX=" + deltaX + ", deltaY="
                                                                            + deltaY, this);
                                                         }

                                                         // to filter spurious offsets
                                                         if (Math.abs(deltaX) < MIN_OFFSET_DISTANCE) {
                                                            deltaX = 0;
                                                         }
                                                         else {
                                                            _touch0LastXPosition = t.getScreenX();
                                                         }

                                                         if (Math.abs(deltaY) < MIN_OFFSET_DISTANCE) {
                                                            deltaY = 0;
                                                         }
                                                         else {
                                                            _touch0LastYPosition = t.getScreenY();
                                                         }

                                                         if ((deltaX != 0) || (deltaY != 0)) { // to filter controls touchs
                                                            setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                         }
                                                      }
                                                   }
                                                };

   final TouchEndHandler    _touchEndHandler    = new TouchEndHandler() {

                                                   @Override
                                                   public void onTouchEnd(final TouchEndEvent event) {
                                                      event.preventDefault();
                                                      if (_isScaling && (event.getTargetTouches().length() == 0)) {
                                                         _isScaling = false;
                                                      }

                                                      if (_debug) {
                                                         _logger.logInfo("TOUCH-END EVENT: " + event.getTargetTouches().length(),
                                                                  this);
                                                      }
                                                   }
                                                };

   final DoubleClickHandler _doubleClickHandler = new DoubleClickHandler() {

                                                   @Override
                                                   public void onDoubleClick(final DoubleClickEvent event) {
                                                      event.preventDefault();
                                                      if (_debug) {
                                                         _logger.logInfo("DOUBLE-CLICK EVENT", this);
                                                      }

                                                      setZoomLevel(_currentLevel + 1, event.getX(), event.getY());
                                                   }
                                                };


   //   private void forceDownloadLevelOne() {
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

   //   private void forceDownloadLevelOne() {
   //
   //      final GPlanarPanoramicZoomLevel levelOne = getZoomLevel(1);
   //
   //      if (_debug) {
   //         System.out.println("forceDownloadLevelOne: " + levelOne.toString());
   //      }
   //
   //      createTiles();
   //      layoutTiles();
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
      //rb.setHeader("Access-Control-Allow-Origin", "*");
      //rb.setHeader("X-Requested-With", "XMLHttpRequest");
      rb.setCallback(new RequestCallback() {
         @Override
         public void onResponseReceived(final Request request,
                                        final Response response) {
            try {
               //final int responseCode = response.getStatusCode() / 100;
               if (_url.startsWith("file:/") || (response.getStatusCode() == 200)) {
                  //System.out.println("Response= " + response.getText());
                  final JSONValue values = JSONParser.parseLenient(response.getText());
                  if (values != null) {
                     final JSONArray valuesList = values.isObject().get("levels").isArray();
                     //final JSONArray valuesList = values.isArray();
                     if (valuesList != null) {
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

                        //forceDownloadLevelOne();
                        fillContainer();
                        updateZoomLevelFromContainerSize(0);
                     }
                  }
               }
               else {
                  _logger.logInfo("HttpError #" + response.getStatusCode() + " - " + response.getText(), this);
                  reportHttpError();
               }
            }
            catch (final Throwable e) {
               _logger.logInfo("Exception: " + e.toString(), this);
               reportHttpError();
            }
         }


         @Override
         public void onError(final Request request,
                             final Throwable exception) {

            _logger.logInfo("HttpError#" + request.toString() + "Exception: " + exception.toString(), this);
            reportHttpError();
         }
      });


      try {
         rb.send();
      }
      catch (final RequestException e) {
         _logger.logInfo("RequestException: " + e.toString(), this);
         reportHttpError();
      }
   }


   private void reportHttpError() {
      //_progressInd.setVisible(false); // removed
      Window.alert("Panoramic not available");
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

      if (!isMobileDevice()) {
         createNavigationButtons(BUTTONEXTEND, BUTTONMARGIN);
         createZoomWidgets(BUTTONEXTEND, BUTTONMARGIN);
      }
      //createProgressIndicator();
   }


   private boolean isMobileDevice() {
      //final String platform = Navigator.getPlatform().toLowerCase();
      final String platform = Navigator.getUserAgent().toLowerCase();
      if (_debug) {
         _logger.logInfo("PLATFORM: " + platform, this);
      }

      return (
      //Detect android
      (platform.indexOf("android") != -1) ||
      //Detect iPhone
               (platform.indexOf("iphone") != -1) ||
               //Detect iPad
               (platform.indexOf("ipad") != -1) ||
      //Detect iPod
      (platform.indexOf("ipod") != -1));
   }


   //   private void recreateHUD() {
   //      recreateNavigationButtons(BUTTONEXTEND, BUTTONMARGIN);
   //      recreateZoomWidgets(BUTTONEXTEND, BUTTONMARGIN);
   //   }

   //   private void createProgressIndicator() {
   //
   //      _progressInd = new Image("IMG/loader1.gif");
   //      //DOM.setIntStyleAttribute(_progressInd.getElement(), "border", 0);
   //      final int posX = getContainerSize().getWidth() / 2;
   //      final int posY = getContainerSize().getHeight() / 2;
   //      super.addTopWidget(_progressInd, posX, posY);
   //      _progressInd.setVisible(true);
   //   }


   private void createNavigationButtons(final int buttonExtent,
                                        final int margin) {

      final String buttonSize = asCssString(buttonExtent);

      // up button
      final Image imgUp = new Image("./IMG/go-up.png");
      imgUp.setSize(buttonSize, buttonSize);
      _buttonUp = new PushButton(imgUp);
      _buttonUp.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            event.preventDefault();
            _isDragging = false;
            moveUp();
         }
      });
      _buttonUp.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonUp.getElement(), "zIndex", 101);
      super.addWidget(_buttonUp, margin + buttonExtent, margin + 0);

      // down button
      final Image imgDown = new Image("./IMG/go-down.png");
      imgDown.setSize(buttonSize, buttonSize);
      _buttonDown = new PushButton(imgDown);
      _buttonDown.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            event.preventDefault();
            _isDragging = false;
            moveDown();
         }
      });
      _buttonDown.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonDown.getElement(), "zIndex", 101);
      super.addWidget(_buttonDown, margin + buttonExtent, margin + (buttonExtent * 2));

      // left button
      final Image imgLeft = new Image("./IMG/go-left.png");
      imgLeft.setSize(buttonSize, buttonSize);
      _buttonLeft = new PushButton(imgLeft);
      _buttonLeft.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            event.preventDefault();
            _isDragging = false;
            moveLeft();
         }
      });
      _buttonLeft.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonLeft.getElement(), "zIndex", 101);
      super.addWidget(_buttonLeft, margin + 0, margin + buttonExtent);

      // right button
      final Image imgRight = new Image("./IMG/go-right.png");
      imgRight.setSize(buttonSize, buttonSize);
      _buttonRight = new PushButton(imgRight);
      _buttonRight.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            event.preventDefault();
            _isDragging = false;
            moveRight();
         }
      });
      _buttonRight.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonRight.getElement(), "zIndex", 101);
      super.addWidget(_buttonRight, margin + (buttonExtent * 2), margin + buttonExtent);

   }


   //   private void recreateNavigationButtons(final int buttonExtent,
   //                                          final int margin) {
   //
   //      super.setWidget(_buttonUp, margin + buttonExtent, margin + 0);
   //      super.setWidget(_buttonDown, margin + buttonExtent, margin + (buttonExtent * 2));
   //      super.setWidget(_buttonLeft, margin + 0, margin + buttonExtent);
   //      super.setWidget(_buttonRight, margin + (buttonExtent * 2), margin + buttonExtent);
   //   }


   private String asCssString(final int value) {
      return Integer.toString(value) + "px";
   }


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
            event.preventDefault();
            _isDragging = false;
            setZoomLevel(_currentLevel + 1);
         }
      });
      _buttonZoomIn.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonZoomIn.getElement(), "zIndex", 101);
      //super.setWidget(_buttonZoomIn, margin + buttonExtent, margin + (buttonExtent * 4)); // at client left position
      super.addWidget(_buttonZoomIn, rightPosition, margin); // at client right position

      // zoom-out button
      final Image imgZoomOut = new Image("./IMG/zoom-out.png");
      imgZoomOut.setSize(buttonSize, buttonSize);
      _buttonZoomOut = new PushButton(imgZoomOut);
      _buttonZoomOut.addClickHandler(new ClickHandler() {

         @Override
         public void onClick(final ClickEvent event) {
            event.preventDefault();
            _isDragging = false;
            setZoomLevel(_currentLevel - 1);
         }
      });
      _buttonZoomOut.setSize(buttonSize, buttonSize);
      DOM.setIntStyleAttribute(_buttonZoomOut.getElement(), "zIndex", 101);
      //super.setWidget(_buttonZoomOut, margin + buttonExtent, margin + (buttonExtent * 5) + 40); // at client left position
      super.addWidget(_buttonZoomOut, rightPosition, margin + (buttonExtent * 2)); // at client right position
   }


   private void recreateWidgets(final int buttonExtent,
                                final int margin) {

      final int rightPosition = getContainerSize().getWidth() - (buttonExtent) - margin;

      super.addWidget(_buttonZoomIn, rightPosition, margin); // at client right position
      super.addWidget(_buttonZoomOut, rightPosition, margin + (buttonExtent * 2)); // at client right position

      //      final int posX = getContainerSize().getWidth() / 2;
      //      final int posY = getContainerSize().getHeight() / 2;
      //      super.setTopWidget(_progressInd, posX, posY);
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

      if (_numTilesToDownload > 0) {
         return;
      }

      final int oldLevel = _currentLevel;
      _currentLevel = newLevel;

      final double zoomFactor = Math.pow(2, _currentLevel - oldLevel);

      final double targetXForNewZoom = (targetX - _offsetX) * zoomFactor;
      final double targetYForNewZoom = (targetY - _offsetY) * zoomFactor;

      _offsetX = (int) (targetXForNewZoom - targetX) * -1;
      _offsetY = (int) (targetYForNewZoom - targetY) * -1;

      //updateZoomWidgets();
      updateTilesToNewZoom(_currentLevel);
      //recreateTiles(); //removed: 01/03/2013
   }


   private void setOffset(final int offsetX,
                          final int offsetY) {

      if ((offsetX == _offsetX) && (offsetY == _offsetY)) {
         return;
      }

      //      if (_numTilesToDownload > 0) {
      //         return;
      //      }

      _offsetX = offsetX;
      _offsetY = offsetY;

      updateTilesGrid();
      layoutTiles();
   }


   private void updateTilesGrid() {

      removeNotVisibleTiles();

      final List<Tile> tilesToCreate = new ArrayList<Tile>();
      final List<Tile> neighborTiles = new ArrayList<Tile>();
      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();
      final GRectangle containerBounds = getContainerBound();
      final GRectangle extendedContainerBounds = getExtendedContainerBound();

      for (int x = 0; x < currentZoomLevel.getWidthInTiles(); x++) {
         for (int y = 0; y < currentZoomLevel.getHeightInTiles(); y++) {
            final Tile tile = new Tile(this, currentZoomLevel, x, y);
            if (tile.touches(containerBounds)) {
               if (!hasTileInTheSamePosition(tile)) {
                  tilesToCreate.add(tile);
               }
            }
            else if (tile.touchesNeighborn(extendedContainerBounds)) {
               if (!hasTileInTheSamePosition(tile)) {
                  neighborTiles.add(tile);
               }
            }
         }
      }

      _tiles.addAll(tilesToCreate);
      _tiles.addAll(neighborTiles);
      _numTilesToDownload += (tilesToCreate.size() + neighborTiles.size());
   }


   private boolean hasTileInTheSamePosition(final Tile tile) {

      for (final Tile each : _tiles) {
         if ((each._x == tile._x) && (each._y == tile._y)) {
            return true;
         }
      }
      return false;
   }


   private void removeNotVisibleTiles() {

      final List<Tile> tilesToRemove = new ArrayList<Tile>();
      final GRectangle containerBounds = getContainerBound();
      final GRectangle extendedContainerBounds = getExtendedContainerBound();

      for (final Tile tile : _tiles) {
         if (!tile.touches(containerBounds) && !tile.touchesNeighborn(extendedContainerBounds)) {
            tile.remove();
            tilesToRemove.add(tile);
         }
      }

      _tiles.removeAll(tilesToRemove);
   }


   private GDimension getContainerSize() {

      return getContainerBound().getSize();
   }


   private GRectangle getContainerBound() {

      final int containerWidth = Window.getClientWidth();
      final int containerHeight = Window.getClientHeight();

      final GRectangle containerBounds = new GRectangle(containerWidth, containerHeight);

      return containerBounds;
   }


   private GRectangle getExtendedContainerBound() {

      final int containerWidth = Window.getClientWidth() + (int) (NEIGHBORN_FACTOR * GPlanarPanoramicZoomLevel.TILE_WIDTH);
      final int containerHeight = Window.getClientHeight() + (int) (NEIGHBORN_FACTOR * GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      final int originX = (int) (-(NEIGHBORN_FACTOR / 2) * GPlanarPanoramicZoomLevel.TILE_WIDTH);
      final int originY = (int) (-(NEIGHBORN_FACTOR / 2) * GPlanarPanoramicZoomLevel.TILE_HEIGHT);

      final GRectangle containerBounds = new GRectangle(originX, originY, containerWidth, containerHeight);

      return containerBounds;
   }


   //   private void updateZoomWidgets() {
   //      _buttonZoomIn.setEnabled(_currentLevel < _maxLevel);
   //      _buttonZoomOut.setEnabled(_currentLevel > _minLevel);
   //      //_zoomSlider.setValue(_currentLevel);
   //   }


   private void updateTilesToNewZoom(final int currentLevel) {

      final List<Tile> newTilesToRemove = new ArrayList<Tile>();
      newTilesToRemove.addAll(_tiles);

      for (final Tile tile : newTilesToRemove) {
         tile.resizeWhileDownloading(currentLevel);
      }
      _tilesToRemove.add(newTilesToRemove);

      _tiles.clear();

      createTiles();
      layoutTiles();
   }


   private void recreateTiles() {

      if (_debug) {
         _logger.logInfo("Quitando..", this);
      }
      removeTiles();

      if (_debug) {
         _logger.logInfo("Creando..", this);
      }
      createTiles();

      if (_debug) {
         _logger.logInfo("Posicionando..", this);
      }
      layoutTiles();
   }


   //   private void addTiles() {
   //      for (final Tile tile : _tiles) {
   //         //container.add(tile);
   //      }
   //   }


   private void layoutTiles() {

      //      if (_numTilesToDownload > 0) {
      //         _progressInd.setVisible(true);
      //      }
      for (final Tile tile : _tiles) {
         tile.positionate();
      }
   }


   private void createTiles() {

      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();
      final GRectangle containerBounds = getContainerBound();
      final GRectangle extendedContainerBounds = getExtendedContainerBound();
      final List<Tile> neighborTiles = new ArrayList<Tile>();

      for (int x = 0; x < currentZoomLevel.getWidthInTiles(); x++) {
         for (int y = 0; y < currentZoomLevel.getHeightInTiles(); y++) {
            final Tile tile = new Tile(this, currentZoomLevel, x, y);
            if (tile.touches(containerBounds)) {
               _tiles.add(tile);
            }
            else if (tile.touchesNeighborn(extendedContainerBounds)) {
               neighborTiles.add(tile);
            }
         }
      }

      _tiles.addAll(neighborTiles); // add at the end of the list

      if (_debug) {
         _logger.logInfo("Level: " + currentZoomLevel.getLevel() + ", NUM Tiles: " + _tiles.size(), this);
         _logger.logInfo("Width: " + containerBounds.getWidth() + ", Height: " + containerBounds.getHeight(), this);
      }

      _numTilesToDownload = _tiles.size();
   }


   private void removeTiles() {

      for (final Tile tile : _tiles) {
         tile.remove();
      }
      _tiles.clear();

      _numTilesToDownload = 0;
   }


   //   private void clearTiles() {
   //      for (final Tile tile : _tiles) {
   //         tile.remove();
   //      }
   //   }


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

      //final GDimension containerSize = getContainerSize();
      final GDimension containerSize = getExtendedContainerBound().getSize();

      if (initialZoomLevelIncrement > 0) {
         _currentLevel = clamp(initialZoomLevelIncrement, _minLevel, _maxLevel);
      }
      else {
         _currentLevel = calculateInitialLevel(containerSize);
      }

      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();
      //      _offsetX = (containerSize.getWidth() - currentZoomLevel.getWidth()) / 2;
      //      _offsetY = (containerSize.getHeight() - currentZoomLevel.getHeight()) / 2;
      _offsetX = ((containerSize.getWidth() - currentZoomLevel.getWidth()) / 2)
                 - (int) ((NEIGHBORN_FACTOR / 2) * GPlanarPanoramicZoomLevel.TILE_WIDTH);
      _offsetY = ((containerSize.getHeight() - currentZoomLevel.getHeight()) / 2)
                 - (int) ((NEIGHBORN_FACTOR / 2) * GPlanarPanoramicZoomLevel.TILE_HEIGHT);

      if (_debug) {
         _logger.logInfo("Updating zoomLevel from container size. ZoomLevel: " + currentZoomLevel, this);
         _logger.logInfo("_offsetX: " + _offsetX + " ,_offsetY: " + _offsetY, this);
      }

      //updateZoomWidgets();
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
