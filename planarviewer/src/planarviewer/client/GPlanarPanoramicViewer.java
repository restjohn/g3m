

package planarviewer.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.RootPanel;


public class GPlanarPanoramicViewer
         extends
            FlexTable {

   //private static final ILogger LOGGER               = ILogger.instance();
   //private static Logger        logger               = Logger.getLogger("NameOfYourLogger");
   //GWT.log("ERROR");

   @SuppressWarnings("unused")
   private static final int HORIZONTAL_INCREMENT = GPlanarPanoramicZoomLevel.TILE_WIDTH / 3;
   @SuppressWarnings("unused")
   private static final int VERTICAL_INCREMENT   = GPlanarPanoramicZoomLevel.TILE_HEIGHT / 3;


   private class Tile {

      private static final long               serialVersionUID = 1L;

      final FlexTable                         _container;
      private final GPlanarPanoramicZoomLevel _zoomLevel;
      private final int                       _x;
      private final int                       _y;
      private GImage                          _image;
      private final String                    _tileUrl;
      //private BufferedImage                   _image;
      //private transient IHandler              _handler;


      private final GRectangle                _pixelsBounds;


      private Tile(final FlexTable container,
                   final GPlanarPanoramicZoomLevel zoomLevel,
                   final int x,
                   final int y) {
         //super();

         _zoomLevel = zoomLevel;
         _x = x;
         _y = y;
         _tileUrl = getTileUrl();
         _container = container;

         _pixelsBounds = initializePixelsBounds();
      }


      private GRectangle initializePixelsBounds() {
         //final Vector2D lower = new Vector2D(getPixelXInPanoramic(), getPixelYInPanoramic());
         //final Vector2D extent = new Vector2D(GPlanarPanoramicZoomLevel.TILE_WIDTH, GPlanarPanoramicZoomLevel.TILE_HEIGHT);
         return new GRectangle(getPixelXInPanoramic(), getPixelYInPanoramic(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private void positionate() {
         tryToLoadImage();
         final GRectangle bounds = calculateBounds();
         //setBounds(calculateBounds());
         _image.setFixedSize(bounds._width, bounds._height); //TODO: if not null
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
         //               final int priority = ((_zoomLevel.getLevel() * 1000000) - (_x * 1000)) + _y;
         //               tryToLoadImage(priority);
         GImageLoader.load(getTileUrl(), new OnLoadImageHandler(_x, _y)); //TODO add handler
         //_image = new GImage(_tileUrl);
      }


      //      private void tryToLoadImage(final int priority) {
      //         final boolean hasHandler = _handler != null;
      //         final boolean hasImage = _image != null;
      //
      //         if (hasHandler || hasImage) {
      //            return;
      //         }
      //
      //
      //         final GFileName fileName = getTileFileName();
      //
      //
      //         _handler = new ILoader.IHandler() {
      //            @Override
      //            public void loaded(final File file,
      //                               final long bytesLoaded,
      //                               final boolean completeLoaded) {
      //               // System.out.println("loaded " + GStringUtils.getSpaceMessage(bytesLoaded) + " in " + file + ", completed=" + completeLoaded);
      //               if (!completeLoaded) {
      //                  return;
      //               }
      //
      //               _handler = null;
      //               try {
      //                  _image = ImageIO.read(file);
      //                  containerRepaint();
      //               }
      //               catch (final IOException e) {
      //                  //LOGGER.logSevere("Error loading " + fileName, e);
      //                  GWT.log("Error loading image file");
      //               }
      //            }
      //
      //
      //            @Override
      //            public void loadError(final IOException e) {
      //               //LOGGER.logSevere("Error=" + e + " loading " + fileName, e);
      //               GWT.log("Error loading image file");
      //            }
      //         };
      //
      //         _loader.load(fileName, -1, false, priority, _handler);
      //      }


      //      private BufferedImage getImageBlocking() {
      //         tryToLoadImage(Integer.MAX_VALUE); // maximum priority for downloading
      //
      //         while (_image == null) {
      //            GUtils.delay(5); // Ugly hack, use a Semaphore instead (dgd)
      //         }
      //
      //         return _image;
      //      }


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
            }
         }
         else {
            _container.setWidget(_y, _x, _image);
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


      //      private void renderDebug(final Graphics2D g2d) {
      //         g2d.setColor(Color.BLUE);
      //         g2d.drawRect(0, 0, GPlanarPanoramicZoomLevel.TILE_WIDTH - 1, GPlanarPanoramicZoomLevel.TILE_HEIGHT - 1);
      //
      //         renderLabel(g2d, getDebugLabel(), 5, 20);
      //      }


      //      private void renderLabel(final Graphics2D g2d,
      //                               final String label,
      //                               final int x,
      //                               final int y) {
      //         g2d.setColor(Color.WHITE);
      //         g2d.drawString(label, x + 1, y + 1);
      //         g2d.drawString(label, x - 1, y - 1);
      //         g2d.drawString(label, x + 1, y - 1);
      //         g2d.drawString(label, x - 1, y + 1);
      //
      //         g2d.setColor(Color.BLACK);
      //         g2d.drawString(label, x, y);
      //      }


      private String getDebugLabel() {
         return "Level=" + _zoomLevel.getLevel() + ", Tile=" + _x + "x" + _y;
      }


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
         _container.remove(_image);
      }


      private boolean touches(final GRectangle bounds) {
         return bounds.intersects(calculateBounds());
      }

      private class OnLoadImageHandler
               implements
                  IImageLoadHandler {

         final int _row, _col;


         public OnLoadImageHandler(final int row,
                                   final int col) {
            _row = row;
            _col = col;
         }


         @Override
         public void imageLoaded(final GImageLoadEvent event) {

            if (event.isLoadFailed()) {
               GWT.log("Image failed to load.");
            }
            else {
               //_image = new GImage(_tileUrl);
               //_image = new GImage(event.takeImage());
               _image = event.getImage();
               _container.setWidget(_y, _x, _image);
               GWT.log("Tile-" + _row + "-" + _col);
               GWT.log("Image dimensions: " + event.getDimensions().getWidth() + " x " + event.getDimensions().getHeight());
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
   private final List<Tile>                      _tiles   = new ArrayList<Tile>();

   private int                                   _currentLevel;
   //private final Point                           _offset = new Point(0, 0);
   private int                                   _offsetX = 0;
   private int                                   _offsetY = 0;


   //   private JLabel                                _zoomInButton;
   //   private JLabel                                _zoomOutButton;
   //   private JSlider                               _zoomSlider;
   //
   //   private Point                                 _dragLastPosition;


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

      forceDownladLevelOne();

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

      super.setCellPadding(0);
      super.setCellSpacing(0);
      super.setBorderWidth(0);
      super.setTitle(name);

   }


   private void forceDownladLevelOne() {

      final GPlanarPanoramicZoomLevel levelOne = getZoomLevel(1);

      System.out.println("levelOne: " + levelOne.toString());

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


      //      final GHolder<Boolean> completed = new GHolder<Boolean>(false);
      //      final GHolder<List<GPlanarPanoramicZoomLevel>> resultHolder = new GHolder<List<GPlanarPanoramicZoomLevel>>(null);
      //
      //      
      //
      //      _loadID = _loader.load(GFileName.fromParentAndParts(_path, "info.txt"), -1, false, Integer.MAX_VALUE,
      //               new ILoader.IHandler() {
      //                  @Override
      //                  public void loaded(final File file,
      //                                     final long bytesLoaded,
      //                                     final boolean completeLoaded) {
      //                     if (!completeLoaded) {
      //                        return;
      //                     }
      //
      //                     try {
      //                        final String infoString = GIOUtils.getContents(file);
      //
      //                        final Gson gson = new Gson();
      //
      //                        final Type type = new TypeToken<List<GPlanarPanoramicZoomLevel>>() {
      //                        }.getType();
      //
      //                        final List<GPlanarPanoramicZoomLevel> result = gson.fromJson(infoString, type);
      //                        resultHolder.set(result);
      //                        completed.set(true);
      //                     }
      //                     catch (final IOException e) {
      //                        LOGGER.logSevere("error loading " + file, e);
      //                     }
      //                     catch (final com.google.gson.JsonSyntaxException e) {
      //                        LOGGER.logSevere("error loading " + file, e);
      //                     }
      //                  }
      //
      //
      //                  @Override
      //                  public void loadError(final IOException e) {
      //                     LOGGER.logSevere("Error loading 'info.txt'", e);
      //                     completed.set(true);
      //                  }
      //               });
      //
      //      while (!completed.get()) {
      //         GUtils.delay(10);
      //      }
      //
      //      if (resultHolder.isEmpty()) {
      //         throw new IOException("Can't read 'info.txt'");
      //      }
      //
      //      return resultHolder.get();
   }


   //   public void openInFrame() {
   //      openInFrame(800, 600);
   //   }


   //   public void openInCustomFrame() {
   //
   //      final DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
   //      final int goalWidth = (int) (dm.getWidth() * 0.9); // 90% of screen
   //      final int goalHeight = (int) (dm.getHeight() * 0.9); // 90% of screen
   //
   //      final int initLevel = calculateInitialLevel(new Dimension(goalWidth, goalHeight));
   //
   //      final int width = (getZoomLevel(initLevel).getWidth() < goalWidth) ? getZoomLevel(initLevel).getWidth() : goalWidth;
   //      final int height = (getZoomLevel(initLevel).getHeight() < goalHeight) ? getZoomLevel(initLevel).getHeight() : goalHeight;
   //
   //      openInFrame(width, height, initLevel);
   //   }


   //   public void openInDialog(final Frame owner) {
   //      openInDialog(owner, 800, 600);
   //   }
   //
   //
   //   public void openInFrame(final int width,
   //                           final int height) {
   //      openInFrame(width, height, 0);
   //   }
   //
   //
   //   public void openInDialog(final Frame owner,
   //                            final int width,
   //                            final int height) {
   //      openInDialog(owner, width, height, 0);
   //   }
   //
   //
   //   public void openInFullScreen() {
   //      openInFullScreen(0);
   //   }


   //   public void openInFullScreen(final int initialZoomLevelIncrement) {
   //      final JFrame frame = createFrame(-1, -1);
   //      final Container container = frame.getContentPane();
   //
   //      fillContainer(container);
   //
   //      frame.addComponentListener(new ComponentAdapter() {
   //         @Override
   //         public void componentShown(final ComponentEvent e) {
   //            updateZoomLevelFromContainerSize(container, initialZoomLevelIncrement);
   //         }
   //      });
   //
   //      frame.setVisible(true);
   //   }


   //   public void openInDialog(final Frame owner,
   //                            final int width,
   //                            final int height,
   //                            final int initialZoomLevelIncrement) {
   //      final JDialog dialog = createDialog(owner, width, height);
   //      final Container container = dialog.getContentPane();
   //
   //      fillContainer(container);
   //
   //      dialog.addComponentListener(new ComponentAdapter() {
   //         @Override
   //         public void componentShown(final ComponentEvent e) {
   //            updateZoomLevelFromContainerSize(container, initialZoomLevelIncrement);
   //         }
   //      });
   //
   //      dialog.setVisible(true);
   //   }
   //
   //
   //   private JDialog createDialog(final Frame owner,
   //                                final int width,
   //                                final int height) {
   //      final JDialog dialog = new JDialog(owner, _name, true);
   //
   //      dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   //      dialog.getRootPane().registerKeyboardAction(new ActionListener() {
   //         @Override
   //         public void actionPerformed(final ActionEvent e) {
   //            dialog.dispose();
   //         }
   //      }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
   //
   //
   //      dialog.setIconImage(getImage("icons/panoramic.png"));
   //
   //      dialog.setSize(width, height);
   //
   //      dialog.setMinimumSize(new Dimension(320, 240));
   //      dialog.setLocationRelativeTo(null);
   //
   //      SwingUtilities.invokeLater(new Runnable() {
   //         @Override
   //         public void run() {
   //            dialog.requestFocus();
   //            dialog.requestFocusInWindow();
   //         }
   //      });
   //
   //      final Container contentPane = dialog.getContentPane();
   //      contentPane.setFocusable(true);
   //      contentPane.setLayout(null);
   //      contentPane.setBackground(Color.WHITE);
   //      if (contentPane instanceof JPanel) {
   //         ((JPanel) contentPane).putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, Double.valueOf(1));
   //      }
   //
   //      contentPane.addKeyListener(new KeyAdapter() {
   //         @Override
   //         public void keyPressed(final KeyEvent e) {
   //            final int keyCode = e.getKeyCode();
   //
   //            if ((keyCode == KeyEvent.VK_PLUS) || (keyCode == KeyEvent.VK_ADD)) {
   //               setZoomLevel(contentPane, _currentLevel + 1);
   //            }
   //            else if ((keyCode == KeyEvent.VK_MINUS) || (keyCode == KeyEvent.VK_SUBTRACT)) {
   //               setZoomLevel(contentPane, _currentLevel - 1);
   //            }
   //            else if (keyCode == KeyEvent.VK_LEFT) {
   //               moveLeft(contentPane);
   //            }
   //            else if (keyCode == KeyEvent.VK_RIGHT) {
   //               moveRight(contentPane);
   //            }
   //            else if (keyCode == KeyEvent.VK_UP) {
   //               moveUp(contentPane);
   //            }
   //            else if (keyCode == KeyEvent.VK_DOWN) {
   //               moveDown(contentPane);
   //            }
   //         }
   //      });
   //
   //
   //      contentPane.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mousePressed(final MouseEvent evt) {
   //            contentPane.setCursor(new Cursor(Cursor.MOVE_CURSOR));
   //            _dragLastPosition = evt.getPoint();
   //         }
   //
   //
   //         @Override
   //         public void mouseReleased(final MouseEvent evt) {
   //            contentPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
   //         }
   //
   //
   //         @Override
   //         public void mouseExited(final MouseEvent evt) {
   //            contentPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
   //         }
   //      });
   //
   //      contentPane.addMouseMotionListener(new MouseMotionAdapter() {
   //         @Override
   //         public void mouseDragged(final MouseEvent evt) {
   //            final Point point = evt.getPoint();
   //            final Point delta = new Point(point.x - _dragLastPosition.x, point.y - _dragLastPosition.y);
   //
   //            setOffset(contentPane, _offset.x + delta.x, _offset.y + delta.y);
   //
   //            _dragLastPosition = point;
   //         }
   //      });
   //
   //
   //      dialog.addComponentListener(new ComponentAdapter() {
   //         @Override
   //         public void componentResized(final ComponentEvent e) {
   //            recreateTiles(contentPane);
   //         }
   //      });
   //
   //      requestFocus(contentPane);
   //
   //      return dialog;
   //   }


   //   public void openInFrame(final int width,
   //                           final int height,
   //                           final int initialZoomLevelIncrement) {
   //      final JFrame frame = createFrame(width, height);
   //      final Container container = frame.getContentPane();
   //
   //      fillContainer(container);
   //
   //      frame.addComponentListener(new ComponentAdapter() {
   //         @Override
   //         public void componentShown(final ComponentEvent e) {
   //            updateZoomLevelFromContainerSize(container, initialZoomLevelIncrement);
   //         }
   //      });
   //
   //      frame.setVisible(true);
   //   }


   //   private void fillContainer(final Container container) {
   //      //      super.get.addMouseWheelHandler(new MouseWheelHandler() {
   //      //         public void onMouseWheel(final MouseWheelEvent event) {
   //      //            final Label label = new Label("hola");
   //      //            final int wheelDelta = EventHandler.getWheelDelta();
   //      //
   //      //            int newSize = label.getWidth() + (wheelDelta * label.getZoomMultiplier());
   //      //            if (newSize < label.getMinSize()) {
   //      //               newSize = label.getMinSize();
   //      //            }
   //      //            else if (newSize > label.getMaxSize()) {
   //      //               newSize = label.getMaxSize();
   //      //            }
   //      //            label.setWidth(newSize);
   //      //            label.setHeight(newSize);
   //      //            eventTrackerLabel.setLastEvent("mouseWheel", label);
   //      //         }
   //      //      });
   //
   //      container.addMouseWheelListener(new MouseWheelListener() {
   //         @Override
   //         public void mouseWheelMoved(final MouseWheelEvent e) {
   //            final Point point = e.getPoint();
   //
   //            if (e.getWheelRotation() < 0) {
   //               setZoomLevel(container, _currentLevel + 1, point.x, point.y);
   //            }
   //            else {
   //               setZoomLevel(container, _currentLevel - 1, point.x, point.y);
   //            }
   //         }
   //      });
   //
   //      createHUD(container);
   //   }


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


   //   private JFrame createFrame(final int width,
   //                              final int height) {
   //
   //      final boolean fullscreen = (width < 0) || (height < 0);
   //
   //      final JFrame frame = new JFrame(_name);
   //
   //      frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
   //      frame.getRootPane().registerKeyboardAction(new ActionListener() {
   //         @Override
   //         public void actionPerformed(final ActionEvent e) {
   //            frame.dispose();
   //         }
   //      }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
   //
   //
   //      frame.setIconImage(getImage("icons/panoramic.png"));
   //
   //      if (fullscreen) {
   //         frame.setUndecorated(true);
   //
   //         final DisplayMode dm = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
   //         frame.setSize(dm.getWidth(), dm.getHeight());
   //      }
   //      else {
   //         frame.setSize(width, height);
   //      }
   //
   //      frame.setMinimumSize(new Dimension(320, 240));
   //      frame.setLocationRelativeTo(null);
   //
   //      SwingUtilities.invokeLater(new Runnable() {
   //         @Override
   //         public void run() {
   //            frame.requestFocus();
   //            frame.requestFocusInWindow();
   //         }
   //      });
   //
   //      final Container contentPane = frame.getContentPane();
   //      contentPane.setFocusable(true);
   //      contentPane.setLayout(null);
   //      contentPane.setBackground(Color.WHITE);
   //      if (contentPane instanceof JPanel) {
   //         ((JPanel) contentPane).putClientProperty(SubstanceLookAndFeel.COLORIZATION_FACTOR, Double.valueOf(1));
   //      }
   //
   //      contentPane.addKeyListener(new KeyAdapter() {
   //         @Override
   //         public void keyPressed(final KeyEvent e) {
   //            final int keyCode = e.getKeyCode();
   //
   //            if ((keyCode == KeyEvent.VK_PLUS) || (keyCode == KeyEvent.VK_ADD)) {
   //               setZoomLevel(contentPane, _currentLevel + 1);
   //            }
   //            else if ((keyCode == KeyEvent.VK_MINUS) || (keyCode == KeyEvent.VK_SUBTRACT)) {
   //               setZoomLevel(contentPane, _currentLevel - 1);
   //            }
   //            else if (keyCode == KeyEvent.VK_LEFT) {
   //               moveLeft(contentPane);
   //            }
   //            else if (keyCode == KeyEvent.VK_RIGHT) {
   //               moveRight(contentPane);
   //            }
   //            else if (keyCode == KeyEvent.VK_UP) {
   //               moveUp(contentPane);
   //            }
   //            else if (keyCode == KeyEvent.VK_DOWN) {
   //               moveDown(contentPane);
   //            }
   //         }
   //      });
   //
   //
   //      contentPane.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mousePressed(final MouseEvent evt) {
   //            contentPane.setCursor(new Cursor(Cursor.MOVE_CURSOR));
   //            _dragLastPosition = evt.getPoint();
   //         }
   //
   //
   //         @Override
   //         public void mouseReleased(final MouseEvent evt) {
   //            contentPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
   //         }
   //
   //
   //         @Override
   //         public void mouseExited(final MouseEvent evt) {
   //            contentPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
   //         }
   //      });
   //
   //      contentPane.addMouseMotionListener(new MouseMotionAdapter() {
   //         @Override
   //         public void mouseDragged(final MouseEvent evt) {
   //            final Point point = evt.getPoint();
   //            final Point delta = new Point(point.x - _dragLastPosition.x, point.y - _dragLastPosition.y);
   //
   //            setOffset(contentPane, _offset.x + delta.x, _offset.y + delta.y);
   //
   //            _dragLastPosition = point;
   //         }
   //      });
   //
   //
   //      frame.addComponentListener(new ComponentAdapter() {
   //         @Override
   //         public void componentResized(final ComponentEvent e) {
   //            recreateTiles(contentPane);
   //         }
   //      });
   //
   //      requestFocus(contentPane);
   //
   //      return frame;
   //   }


   //   private void requestFocus(final Container contentPane) {
   //      SwingUtilities.invokeLater(new Runnable() {
   //         @Override
   //         public void run() {
   //            contentPane.requestFocus();
   //            contentPane.requestFocusInWindow();
   //         }
   //      });
   //   }
   //
   //
   //   private Image getImage(final String imageName) {
   //      return GUtils.getImage(imageName, getClass().getClassLoader());
   //   }
   //
   //
   //   private ImageIcon getImageIcon(final String iconName,
   //                                  final int width,
   //                                  final int height) {
   //      final ImageIcon icon = GUtils.getImageIcon(iconName, getClass().getClassLoader());
   //
   //      final Image image = icon.getImage();
   //      if (image == null) {
   //         return icon;
   //      }
   //      final int imageWidth = image.getWidth(null);
   //      final int imageHeight = image.getHeight(null);
   //      if ((imageWidth == -1) || (imageHeight == -1)) {
   //         return icon;
   //      }
   //      if ((width == imageWidth) && (height == imageHeight)) {
   //         return icon;
   //      }
   //
   //      final Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
   //      return new ImageIcon(resizedImage);
   //   }
   //
   //
   //   private void createHUD(final Container container) {
   //      final int buttonExtent = 20;
   //      final int margin = 2;
   //
   //      createNavigationButtons(container, buttonExtent, margin);
   //      createZoomWidgets(container, buttonExtent, margin);
   //   }
   //
   //
   //   private void createNavigationButtons(final Container container,
   //                                        final int buttonExtent,
   //                                        final int margin) {
   //      final JLabel buttonUp = new JLabel(getImageIcon("icons/go-up.png", buttonExtent, buttonExtent));
   //      buttonUp.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mouseClicked(final MouseEvent e) {
   //            moveUp(container);
   //         }
   //      });
   //      setLook(buttonUp);
   //      container.add(buttonUp);
   //      setPosition(buttonUp, margin + buttonExtent, margin + 0);
   //
   //
   //      final JLabel buttonDown = new JLabel(getImageIcon("icons/go-down.png", buttonExtent, buttonExtent));
   //      buttonDown.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mouseClicked(final MouseEvent e) {
   //            moveDown(container);
   //         }
   //      });
   //      setLook(buttonDown);
   //      container.add(buttonDown);
   //      setPosition(buttonDown, margin + buttonExtent, margin + (buttonExtent * 2));
   //
   //
   //      final JLabel buttonLeft = new JLabel(getImageIcon("icons/go-left.png", buttonExtent, buttonExtent));
   //      buttonLeft.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mouseClicked(final MouseEvent e) {
   //            moveLeft(container);
   //         }
   //      });
   //      setLook(buttonLeft);
   //      container.add(buttonLeft);
   //      setPosition(buttonLeft, margin + 0, margin + buttonExtent);
   //
   //
   //      final JLabel buttonRight = new JLabel(getImageIcon("icons/go-right.png", buttonExtent, buttonExtent));
   //      buttonRight.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mouseClicked(final MouseEvent e) {
   //            moveRight(container);
   //         }
   //      });
   //      setLook(buttonRight);
   //      container.add(buttonRight);
   //      setPosition(buttonRight, margin + (buttonExtent * 2), margin + buttonExtent);
   //   }
   //
   //
   //   private void setLook(final JComponent widget) {
   //      if (_debug) {
   //         widget.setBorder(new LineBorder(Color.RED, 1));
   //      }
   //      widget.setCursor(new Cursor(Cursor.HAND_CURSOR));
   //   }
   //
   //
   //   private void createZoomWidgets(final Container container,
   //                                  final int buttonExtent,
   //                                  final int margin) {
   //      _zoomInButton = new JLabel(getImageIcon("icons/zoom-in.png", buttonExtent, buttonExtent));
   //      _zoomInButton.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mouseClicked(final MouseEvent e) {
   //            setZoomLevel(container, _currentLevel + 1);
   //         }
   //      });
   //      setLook(_zoomInButton);
   //      container.add(_zoomInButton);
   //      setPosition(_zoomInButton, margin + buttonExtent, margin + (buttonExtent * 4) + 0);
   //
   //
   //      _zoomSlider = new JSlider(SwingConstants.VERTICAL, _minLevel, _maxLevel, _currentLevel);
   //      _zoomSlider.setMajorTickSpacing(0);
   //      _zoomSlider.setMinorTickSpacing(1);
   //      _zoomSlider.setSnapToTicks(true);
   //      _zoomSlider.setOpaque(false);
   //      _zoomSlider.setPreferredSize(new Dimension(_zoomSlider.getPreferredSize().width,
   //               (_zoomLevels.size() * buttonExtent * 2) / 3));
   //      _zoomSlider.addChangeListener(new ChangeListener() {
   //         @Override
   //         public void stateChanged(final ChangeEvent e) {
   //            setZoomLevel(container, _zoomSlider.getValue());
   //            requestFocus(container);
   //         }
   //      });
   //
   //      setLook(_zoomSlider);
   //      container.add(_zoomSlider);
   //      setPosition(_zoomSlider, margin + buttonExtent, margin + (buttonExtent * 5));
   //
   //
   //      _zoomOutButton = new JLabel(getImageIcon("icons/zoom-out.png", buttonExtent, buttonExtent));
   //      _zoomOutButton.addMouseListener(new MouseAdapter() {
   //         @Override
   //         public void mouseClicked(final MouseEvent e) {
   //            setZoomLevel(container, _currentLevel - 1);
   //         }
   //      });
   //      setLook(_zoomOutButton);
   //      container.add(_zoomOutButton);
   //      setPosition(_zoomOutButton, margin + buttonExtent, margin + (buttonExtent * 5) + _zoomSlider.getHeight());
   //   }


   //   private void setZoomLevel(final Container container,
   //                             final int newLevel) {
   private void setZoomLevel(final int newLevel) {
      final int containerWidth = RootPanel.get().getOffsetWidth();
      final int containerHeight = RootPanel.get().getOffsetHeight();
      //final GRectangle containerBounds = container.getBounds();
      final GRectangle containerBounds = new GRectangle(containerWidth, containerHeight);
      final int targetX = (int) containerBounds.getCenterX();
      final int targetY = (int) containerBounds.getCenterY();
      //setZoomLevel(container, newLevel, targetX, targetY);
      setZoomLevel(newLevel, targetX, targetY);
   }


   private void setZoomLevel(//final Container container,
                             final int newLevel,
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

      //updateZoomWidgets();

      //recreateTiles(container);
      recreateTiles();
   }


   private void setOffset(//final Container container,
                          final int offsetX,
                          final int offsetY) {

      if ((offsetX == _offsetX) && (offsetY == _offsetY)) {
         return;
      }

      _offsetX = offsetX;
      _offsetY = offsetY;

      layoutTiles();
      //updateTilesGrid(container);
      updateTilesGrid();
   }


   //private void updateTilesGrid(final Container container) {
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


   //private void removeNotVisibleTiles(final Container container) {
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

      final int containerWidth = RootPanel.get().getOffsetWidth();
      final int containerHeight = RootPanel.get().getOffsetHeight();
      final GRectangle containerBounds = new GRectangle(containerWidth, containerHeight);

      return containerBounds;
   }


   //   private void updateZoomWidgets() {
   //      _zoomInButton.setEnabled(_currentLevel < _maxLevel);
   //      _zoomOutButton.setEnabled(_currentLevel > _minLevel);
   //
   //      _zoomSlider.setValue(_currentLevel);
   //   }


   //private void recreateTiles(final Container container) {
   private void recreateTiles() {
      //      removeTiles(container);
      //      createTiles(container);
      //      addTiles(container);
      removeTiles();
      createTiles();
      addTiles();
      layoutTiles();

      // force redraw
      //      container.invalidate();
      //      container.doLayout();
      //      container.repaint();
   }


   //private void addTiles(final Container container) {
   private void addTiles() {
      for (final Tile tile : _tiles) {
         //container.add(tile);
         tile.paint();
      }
   }


   private void layoutTiles() {
      for (final Tile tile : _tiles) {
         tile.positionate();
      }
   }


   //private void createTiles(final Container container) {
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
   }


   //private void removeTiles(final Container container) {
   private void removeTiles() {
      for (final Tile tile : _tiles) {
         tile.remove();
         //container.remove(tile);
      }
      _tiles.clear();
   }


   //   private void setPosition(final Component component,
   //                            final int x,
   //                            final int y) {
   //      final Dimension size = component.getPreferredSize();
   //      component.setBounds(x, y, size.width, size.height);
   //   }
   //
   //
   //   private void moveDown(final Container container) {
   //      setOffset(container, _offset.x, _offset.y - VERTICAL_INCREMENT);
   //   }
   //
   //
   //   private void moveUp(final Container container) {
   //      setOffset(container, _offset.x, _offset.y + VERTICAL_INCREMENT);
   //   }
   //
   //
   //   private void moveLeft(final Container container) {
   //      setOffset(container, _offset.x + HORIZONTAL_INCREMENT, _offset.y);
   //   }
   //
   //
   //   private void moveRight(final Container container) {
   //      setOffset(container, _offset.x - HORIZONTAL_INCREMENT, _offset.y);
   //   }


   //   private void updateZoomLevelFromContainerSize(final Container container,
   //                                                 final int initialZoomLevelIncrement) {
   private void updateZoomLevelFromContainerSize(final int initialZoomLevelIncrement) {

      //final Dimension containerSize = container.getSize();
      final GDimension containerSize = getContainerSize();

      if (initialZoomLevelIncrement > 0) {
         //_currentLevel = GMath.clamp(initialZoomLevelIncrement, _minLevel, _maxLevel);
         if (_currentLevel < _minLevel) {
            _currentLevel = _minLevel;
         }
         else if (_currentLevel > _maxLevel) {
            _currentLevel = _maxLevel;
         }
         else {
            _currentLevel = initialZoomLevelIncrement;
         }
      }
      else {
         _currentLevel = calculateInitialLevel(containerSize);
      }

      final GPlanarPanoramicZoomLevel currentZoomLevel = getCurrentZoomLevel();
      _offsetX = (containerSize.getWidth() - currentZoomLevel.getWidth()) / 2;
      _offsetY = (containerSize.getHeight() - currentZoomLevel.getHeight()) / 2;

      //updateZoomWidgets();
      //recreateTiles(container);

      recreateTiles();
   }


   //   private static void initializeSubstance() {
   //      JFrame.setDefaultLookAndFeelDecorated(true);
   //      JDialog.setDefaultLookAndFeelDecorated(true);
   //
   //      try {
   //         UIManager.setLookAndFeel(new SubstanceMistAquaLookAndFeel());
   //
   //         SubstanceLookAndFeel.setToUseConstantThemesOnDialogs(true);
   //      }
   //      catch (final UnsupportedLookAndFeelException e) {
   //         e.printStackTrace();
   //      }
   //   }


   @Override
   public String toString() {
      return "GPlanarPanoramicViewer [name=\"" + _name + "\", url=" + _url + ", levels=" + _minLevel + "->" + _maxLevel + "]";
   }


   //   public static void main(final String[] args) throws IOException {
   //      System.out.println("GPlanarPanoramicViewer 0.1");
   //      System.out.println("--------------------------\n");
   //
   //
   //      final IProgressReporter progressReporter = null;
   //      //      final ILoader loader = new GFileLoader(GFileName.absolute("home", "dgd", "Desktop", "ASSORTED STUFF", "PruebaPanoramicas",
   //      //               "PLANAR", "PANOS"));
   //      //      final ILoader loader = new GHttpLoader(new URL("http://sourceforge.net/projects/glob3/files/globe-demo-data/gigapixels/"),
   //      //               true, progressReporter);
   //      //      final ILoader loader = new GHttpLoader(new URL("http://glob3.sourceforge.net/globe-demo-data/gigapixels/"), true,
   //      //               progressReporter);
   //
   //      //      final GFileName panoramicPath = GFileName.relative("PlazaSanJorge-Caceres-Espana");
   //
   //      final ILoader loader = new GFileLoader(GFileName.relative("PANO", "SantaMaria"), progressReporter);
   //
   //      final GFileName panoramicPath = GFileName.relative("santa_maria.jpg");
   //
   //
   //      //      final GFileName panoramicPath = GFileName.relative("cantabria1.jpg");
   //      //      //      final GFileName panoramicPath = GFileName.relative("Caballos.jpg");
   //
   //      final GPlanarPanoramicViewer viewer = new GPlanarPanoramicViewer(loader, panoramicPath, "Sample Gigapixel Picture", false);
   //
   //      final boolean fullscreen = false;
   //
   //      SwingUtilities.invokeLater(new Runnable() {
   //         @Override
   //         public void run() {
   //            if (fullscreen) {
   //               viewer.openInFullScreen();
   //            }
   //            else {
   //               initializeSubstance();
   //               viewer.openInFrame(1024, 768);
   //            }
   //         }
   //      });
   //   }


   //   public GFileName getPath() {
   //      return _path;
   //   }

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


   //   public BufferedImage getFullImage(final int level) {
   //      final GPlanarPanoramicZoomLevel zoomLevel = getZoomLevel(level);
   //      if (zoomLevel == null) {
   //         throw new RuntimeException("Invalid Level: " + level);
   //      }
   //
   //      final BufferedImage result = new BufferedImage(zoomLevel.getWidth(), zoomLevel.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
   //
   //      final Graphics2D g2d = result.createGraphics();
   //
   //      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
   //      g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
   //      g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
   //      g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
   //      g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
   //      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
   //
   //
   //      for (int x = 0; x < zoomLevel.getWidthInTiles(); x++) {
   //         for (int y = 0; y < zoomLevel.getHeightInTiles(); y++) {
   //            final Tile tile = new Tile(zoomLevel, x, y);
   //
   //            final BufferedImage tileImage = tile.getImageBlocking();
   //            final int pixelX = tile.getPixelXInPanoramic();
   //            final int pixelY = tile.getPixelYInPanoramic();
   //
   //            g2d.drawImage(tileImage, pixelX, pixelY, null);
   //         }
   //      }
   //
   //      g2d.dispose();
   //
   //      return result;
   //   }


}
