

package planarviewer.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.gargoylesoftware.htmlunit.javascript.host.Event;
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
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
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


   private class Tile {

      //final FlexTable                         _container;
      final Container                         _container;
      private final GPlanarPanoramicZoomLevel _zoomLevel;
      private final int                       _x;
      private final int                       _y;
      private GImage                          _image;
      private final String                    _tileUrl;
      private boolean                         _removeWhileLoading;
      //private BufferedImage                   _image;
      //private transient IHandler              _handler;

      private int                             _xPos, _yPos;
      private final GRectangle                _pixelsBounds;


      private Tile(final Container container,
                   final GPlanarPanoramicZoomLevel zoomLevel,
                   final int x,
                   final int y) {
         //super();
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
         //final Vector2D lower = new Vector2D(getPixelXInPanoramic(), getPixelYInPanoramic());
         //final Vector2D extent = new Vector2D(GPlanarPanoramicZoomLevel.TILE_WIDTH, GPlanarPanoramicZoomLevel.TILE_HEIGHT);
         return new GRectangle(getPixelXInPanoramic(), getPixelYInPanoramic(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private void positionate() {
         _xPos = calculateXPosition();
         _yPos = calculateYPosition();
         tryToLoadImage();
         //final GRectangle bounds = calculateBounds();
         //setBounds(calculateBounds());
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
         GImageLoader.load(_tileUrl, new OnLoadImageHandler());//TODO 
         //_image = new GImage(_tileUrl);
      }


      //      private void containerRepaint() {
      //         final Container parent = getParent();
      //         if (parent != null) {
      //            parent.repaint();
      //         }
      //
      //      }


      public void paint() {

         if (_image == null) {
            final Tile ancestor = getNearestAncestorWithTextureInCache();
            if (ancestor != null) {
               paintAncestor(ancestor);
               System.out.println("Debería pintar ancestor: Tile-" + ancestor._x + "-" + ancestor._y);
            }
         }
         else {
            //_container.setWidget(_y, _x, _image);
            //final GRectangle bounds = calculateBounds();
            System.out.println("Pintando: " + asString());
            _container.setWidget(_image, _xPos, _yPos); //TODO
         }
      }


      private void paintAncestor(final Tile ancestor) {
         //TODO
      }


      //      public void paint(final Graphics g) {
      //         final Graphics2D g2d = (Graphics2D) g;
      //
      //         if (_image == null) {
      //            final Tile ancestor = getNearestAncestorWithTextureInCache();
      //            if (ancestor != null) {
      //               paintAncestor(g2d, ancestor);
      //            }
      //         }
      //         else {
      //            g2d.drawImage(_image, 0, 0, null);
      //         }
      //
      //         if (_debug) {
      //            renderDebug(g2d);
      //         }
      //
      //      }


      //      private void paintAncestor(final Graphics2D g2d,
      //                                 final Tile ancestor) {
      //
      //         // final RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      //         // renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      //         // g2d.setRenderingHints(renderHints);
      //
      //         ancestor.tryToLoadImage();
      //
      //         final double scale = Math.pow(2, _zoomLevel.getLevel() - ancestor._zoomLevel.getLevel());
      //
      //         final Rectangle scaledAncestorBounds = ancestor._pixelsBounds.scale(scale);
      //
      //         final IVector2 lower = _pixelsBounds._lower.sub(scaledAncestorBounds._lower).div(scale);
      //         final IVector2 upper = _pixelsBounds._upper.sub(scaledAncestorBounds._lower).div(scale);
      //
      //         final int sx1 = Math.round((float) lower.x());
      //         final int sy1 = Math.round((float) lower.y());
      //         final int sx2 = Math.round((float) upper.x());
      //         final int sy2 = Math.round((float) upper.y());
      //         g2d.drawImage( //
      //                  ancestor._image, //
      //                  0, 0, //
      //                  GPlanarPanoramicZoomLevel.TILE_WIDTH, GPlanarPanoramicZoomLevel.TILE_HEIGHT, //
      //                  sx1, sy1, //
      //                  sx2, sy2, //
      //                  null);
      //      }


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


      //      private String getDebugLabel() {
      //         return "Level=" + _zoomLevel.getLevel() + ", Tile=" + _x + "x" + _y;
      //      }


      //      private GFileName getTileFileName() {
      //         return GFileName.fromParentAndParts(_path, Integer.toString(_zoomLevel.getLevel()), "tile-" + _x + "-" + _y + ".jpg");
      //      }


      private String getTileUrl() {
         return _url + "/" + Integer.toString(_zoomLevel.getLevel()) + "/" + "tile-" + _x + "-" + _y + ".jpg";
      }


      private void remove() {
         //         if (_loadID != null) {
         //            _loader.cancelLoad(_loadID);
         //         }
         //_image.removeFromParent();
         if (_image != null) {
            _container.remove(_image);
            _image.removeFromParent();
            System.out.println("Borrando: " + asString());
         }
         if (GImageLoader.isDownloadingImage(_tileUrl)) {
            _removeWhileLoading = true;
         }
      }


      private boolean touches(final GRectangle bounds) {
         return bounds.intersects(calculateBounds());
      }


      private String asString() {
         final String str = "Tile-" + _x + "-" + _y + " | " + "pos: (" + _xPos + "," + _yPos + ")";
         return str;
      }

      private class OnLoadImageHandler
               implements
                  IImageLoadHandler {

         //final int _xPos, _yPos;
         //         public OnLoadImageHandler(final int xPos,
         //                                   final int yPos) {
         //            _xPos = xPos;
         //            _yPos = yPos;
         //         }

         public OnLoadImageHandler() {

         }


         @Override
         public void imageLoaded(final GImageLoadEvent event) {

            if (event.isLoadFailed()) {
               //GWT.log("Image failed to load.");
               System.out.println("Image failed to load.");
            }
            else {
               if (_removeWhileLoading) {
                  _removeWhileLoading = false;
                  return;
               }
               //_image = new GImage(_tileUrl);
               //_image = new GImage(event.takeImage());
               _image = event.getImage();
               _image.unsinkEvents(Event.DRAGDROP);
               _image.unsinkEvents(Event.CLICK);
               _image.unsinkEvents(Event.MOUSEDOWN);
               _image.unsinkEvents(Event.MOUSEDRAG);
               _image.unsinkEvents(Event.SELECT);
               _container.setWidget(_image, _xPos, _yPos);
               //GWT.log("Tile-" + _x + "-" + _y);
               System.out.println("Loaded: " + asString());
               //createHUD(); // TODO: temporal, quitar luego
            }
         }
      }


   }


   //private final ILoader                         _loader;
   //private final GFileName                       _path;
   private final String                          _url;
   private final String                          _name;
   private final boolean                         _debug;

   private final List<GPlanarPanoramicZoomLevel> _zoomLevels;
   private final int                             _minLevel;
   private final int                             _maxLevel;
   private final List<Tile>                      _tiles             = new ArrayList<Tile>();

   private int                                   _currentLevel;
   //private final Point                           _offset = new Point(0, 0);
   private int                                   _offsetX           = 0;
   private int                                   _offsetY           = 0;

   private final int                             _dragStartX        = 0;
   private final int                             _dragStartY        = 0;
   private final int                             _dragEndX          = 0;
   private final int                             _dragEndY          = 0;

   private int                                   _dragLastXPosition = 0;
   private int                                   _dragLastYPosition = 0;
   private boolean                               _isDragging        = false;

   private PushButton                            _buttonZoomIn;
   private PushButton                            _buttonZoomOut;


   //   private JLabel                                _zoomInButton;
   //   private JLabel                                _zoomOutButton;
   //   private JSlider                               _zoomSlider;
   //
   //   private Point                                 _dragLastPosition;
   //
   //private ILoader.LoadID                        _loadID;


   public GPlanarPanoramicViewer(final String url,
                                 final String name) throws IOException {
      this(url, name, false);
   }


   //TODO: constructor
   public GPlanarPanoramicViewer(final String url,
                                 final String name,
                                 final boolean debug) throws IOException {

      //      GAssert.notNull(loader, "loader");
      //      GAssert.notNull(path, "path");
      //      GAssert.notNull(name, "name");

      //_loader = loader;
      _url = url;
      _name = name;
      _debug = debug;

      _zoomLevels = readZoomLevels();

      //forceDownloadLevelOne();

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

      super.setTitle(name);
      super.setVisible(true);
      super.setSize(getContainerSize().getWidth(), getContainerSize().getHeight());
      Window.enableScrolling(false);

      fillContainer();
      updateZoomLevelFromContainerSize(0);
      //recreateTiles();
   }


   private void fillContainer() {
      super.addMouseWheelHandler(_mouseWheelHandler);
      super.addDragStartHandler(_dragStartHandler);
      super.addDragEndHandler(_dragEndHandler);
      //super.addDragHandler(_dragHandler);
      super.addMouseDownHandler(_mouseDownHandler);
      //super.addMouseMoveHandler(_mouseMoveHandler);

      createHUD();
   }

   final MouseWheelHandler _mouseWheelHandler = new MouseWheelHandler() {

                                                 @Override
                                                 public void onMouseWheel(final MouseWheelEvent event) {
                                                    System.out.println("MOUSE WHEEL EVENT");
                                                    //                                                    System.out.println("WclientX: " + event.getClientX() + ", WclientY: "
                                                    //                                                                       + event.getClientY());
                                                    //                                                    System.out.println("WscreenX: " + event.getScreenX() + ", WscreenY: "
                                                    //                                                                       + event.getScreenY());
                                                    //                                                    System.out.println("WdeltaY: " + event.getDeltaY());

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
                                                    final int SclientX = event.getNativeEvent().getClientX();
                                                    //                                                    final int SclientY = event.getNativeEvent().getClientY();
                                                    //                                                    final int SscreenX = event.getNativeEvent().getScreenX();
                                                    //                                                    final int SscreenY = event.getNativeEvent().getScreenY();
                                                    //                                                    _dragStartX = event.getNativeEvent().getScreenX();
                                                    //                                                    _dragStartY = event.getNativeEvent().getScreenY();
                                                    System.out.println("MOUSE DRAG-START EVENT");
                                                    //                                                    System.out.println("SclientX: " + SclientX + ", SclientY: " + SclientY);
                                                    //                                                    System.out.println("SscreenX: " + SscreenX + ", SscreenY: " + SscreenY);
                                                    _isDragging = true;
                                                 }
                                              };


   final DragEndHandler    _dragEndHandler    = new DragEndHandler() {

                                                 @Override
                                                 public void onDragEnd(final DragEndEvent event) {
                                                    //                                                    final int EclientX = event.getNativeEvent().getClientX();
                                                    //                                                    final int EclientY = event.getNativeEvent().getClientY();
                                                    //                                                    final int EscreenX = event.getNativeEvent().getScreenX();
                                                    //                                                    final int EscreenY = event.getNativeEvent().getScreenY();
                                                    //                                                    _dragEndX = event.getNativeEvent().getScreenX();
                                                    //                                                    _dragEndY = event.getNativeEvent().getScreenY();
                                                    System.out.println("MOUSE DRAG-END EVENT");
                                                    //                                                    System.out.println("EclientX: " + EclientX + ", EclientY: " + EclientY);
                                                    //                                                    System.out.println("EscreenX: " + EscreenX + ", EscreenY: " + EscreenY);
                                                    //                                                    System.out.println("DistanceX: " + (_dragEndX - _dragStartX)
                                                    //                                                                       + " / DistanceY: " + (_dragEndY - _dragStartY));
                                                    //                                                    //System.out.println("WscreenX: " + event.getScreenX() + ", WscreenY: " + event.getScreenY());
                                                    //                                                    final int deltaX = event.getNativeEvent().getScreenX() - _dragStartX;
                                                    //                                                    final int deltaY = event.getNativeEvent().getScreenY() - _dragStartY;
                                                    //                                                    setOffset(_offsetX + deltaX, _offsetY + deltaY);

                                                    final int deltaX = event.getNativeEvent().getScreenX() - _dragLastXPosition;
                                                    final int deltaY = event.getNativeEvent().getScreenY() - _dragLastYPosition;

                                                    System.out.println("delta: (" + deltaX + "," + deltaY + ")");
                                                    setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                    _isDragging = false;
                                                 }
                                              };

   final DragHandler       _dragHandler       = new DragHandler() {

                                                 @Override
                                                 public void onDrag(final DragEvent event) {
                                                    System.out.println("ON-DRAG EVENT");
                                                    System.out.println("pos: (" + event.getNativeEvent().getScreenX() + ","
                                                                       + event.getNativeEvent().getScreenY() + ")");
                                                    final int deltaX = event.getNativeEvent().getScreenX() - _dragLastXPosition;
                                                    final int deltaY = event.getNativeEvent().getScreenY() - _dragLastYPosition;

                                                    System.out.println("delta: (" + deltaX + "," + deltaY + ")");
                                                    setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                 }
                                              };

   //                                            @Override
   //   public void dragMouseMoved(DragSourceDragEvent dsde) {
   //      // TODO Auto-generated method stub
   //      
   //   }
   //
   //   final DragSourceMotionListener _dragSourceListener = new DragSourceMotionListener() {
   //
   //                                                         @Override
   //                                                         public void dragMouseMoved(final DragSourceDragEvent dsde) {
   //                                                            // TODO Auto-generated method stub
   //                                                            System.out.println("DRAG-MOVE EVENT");
   //                                                         }
   //                                                      };

   final MouseMoveHandler  _mouseMoveHandler  = new MouseMoveHandler() {

                                                 @Override
                                                 public void onMouseMove(final MouseMoveEvent event) {
                                                    // TODO Auto-generated method stub
                                                    if (_isDragging) {
                                                       System.out.println("ON-MOUSE-MOVE EVENT");
                                                       final int deltaX = event.getNativeEvent().getScreenX()
                                                                          - _dragLastXPosition;
                                                       final int deltaY = event.getNativeEvent().getScreenY()
                                                                          - _dragLastYPosition;

                                                       System.out.println("delta: (" + deltaX + "," + deltaY + ")");
                                                       setOffset(_offsetX + deltaX, _offsetY + deltaY);
                                                    }
                                                 }
                                              };


   final MouseDownHandler  _mouseDownHandler  = new MouseDownHandler() {

                                                 @Override
                                                 public void onMouseDown(final MouseDownEvent event) {
                                                    //                                                    _dragLastXPosition = event.getX();
                                                    //                                                    _dragLastYPosition = event.getY();
                                                    System.out.println("MOUSE-DOWN EVENT");
                                                    _dragLastXPosition = event.getNativeEvent().getScreenX();
                                                    _dragLastYPosition = event.getNativeEvent().getScreenY();
                                                    System.out.println("pos: (" + _dragLastXPosition + "," + _dragLastYPosition
                                                                       + ")");
                                                 }
                                              };


   private void forceDownloadLevelOne() {

      final GPlanarPanoramicZoomLevel levelOne = getZoomLevel(1);

      //super.setSize(levelOne.getWidth(), levelOne.getHeight());
      System.out.println("forceDownloadLevelOne: " + levelOne.toString());

      for (int x = 0; x < levelOne.getWidthInTiles(); x++) {
         for (int y = 0; y < levelOne.getHeightInTiles(); y++) {
            final Tile tile = new Tile(this, levelOne, x, y);
            tile.tryToLoadImage(); // level one has maximum priority for downloading
         }
      }

   }


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


   private List<GPlanarPanoramicZoomLevel> readZoomLevels() throws IOException {

      //TODO: read file from url and parse with json

      //now hack for test viewer code
      final List<GPlanarPanoramicZoomLevel> result = new ArrayList<GPlanarPanoramicZoomLevel>();

      final GPlanarPanoramicZoomLevel level1 = new GPlanarPanoramicZoomLevel(1, 2430, 210, 10, 1);
      final GPlanarPanoramicZoomLevel level2 = new GPlanarPanoramicZoomLevel(2, 4861, 420, 19, 2);
      final GPlanarPanoramicZoomLevel level3 = new GPlanarPanoramicZoomLevel(3, 9723, 840, 38, 4);
      final GPlanarPanoramicZoomLevel level4 = new GPlanarPanoramicZoomLevel(4, 19446, 1680, 76, 7);
      final GPlanarPanoramicZoomLevel level5 = new GPlanarPanoramicZoomLevel(5, 38892, 3361, 152, 14);
      final GPlanarPanoramicZoomLevel level6 = new GPlanarPanoramicZoomLevel(6, 77784, 6722, 304, 27);

      Collections.addAll(result, level1, level2, level3, level4, level5, level6);


      return result;
   }


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
      final int buttonExtent = 20;
      final int margin = 2;

      createNavigationButtons(buttonExtent, margin);
      createZoomWidgets(buttonExtent, margin);
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


   private void createZoomWidgets(final int buttonExtent,
                                  final int margin) {

      final String buttonSize = asCssString(buttonExtent);
      final int rightPosition = getContainerSize().getWidth() - (buttonExtent * 2) - margin;

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

      //layoutTiles();
      //updateTilesGrid();
      recreateTiles(); // ??
   }


   private void updateTilesGrid() {

      //removeNotVisibleTiles(container);
      removeNotVisibleTiles();

      final List<Tile> tilesToCreate = new ArrayList<Tile>();

      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();

      //      final GRectangle containerBounds = new GRectangle(0, 0, (int) container.getBounds().getWidth(),
      //               (int) container.getBounds().getHeight());
      final GRectangle containerBounds = getContainerBound();

      for (int x = 0; x < currentZoomLevel.getWidthInTiles(); x++) {
         for (int y = 0; y < currentZoomLevel.getHeightInTiles(); y++) {
            final Tile tile = new Tile(this, currentZoomLevel, x, y);
            if (tile.touches(containerBounds)) {
               if (!hasTileInTheSamePosition(tile)) {
                  tilesToCreate.add(tile);
               }
            }
         }
      }

      for (final Tile tileToCreate : tilesToCreate) {
         _tiles.add(tileToCreate);
         //container.add(tileToCreate);
         tileToCreate.positionate();
      }

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
      //      final GRectangle containerBounds = new GRectangle(0, 0, (int) container.getBounds().getWidth(),
      //               (int) container.getBounds().getHeight());

      final GRectangle containerBounds = getContainerBound();

      final Iterator<Tile> iterator = _tiles.iterator();
      while (iterator.hasNext()) {
         final Tile tile = iterator.next();
         if (!tile.touches(containerBounds)) {
            tile.remove();
            iterator.remove();
            //container.remove(tile);
         }
      }
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


   private void updateZoomWidgets() {
      _buttonZoomIn.setEnabled(_currentLevel < _maxLevel);
      _buttonZoomOut.setEnabled(_currentLevel > _minLevel);
      //_zoomSlider.setValue(_currentLevel);
   }


   private void recreateTiles() {
      //      removeTiles(container);
      //      createTiles(container);
      //      addTiles(container);
      System.out.println("Quitando..");
      removeTiles();
      System.out.println("Creando..");
      createTiles();
      //addTiles();
      System.out.println("Posicionando..");
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
      System.out.println("Level: " + currentZoomLevel.getLevel() + ", NUM Tiles: " + _tiles.size());
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
      System.out.println("Updating zoomLevel from container size. ZoomLevel: " + currentZoomLevel);
      System.out.println("_offsetX: " + _offsetX + " ,_offsetY: " + _offsetY);

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
