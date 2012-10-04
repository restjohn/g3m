

package planarviewer.client;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JSlider;


public class GPlanarPanoramicViewer {

   //private static final ILogger LOGGER               = ILogger.instance();
   //private static Logger        logger               = Logger.getLogger("NameOfYourLogger");
   //GWT.log("ERROR");

   @SuppressWarnings("unused")
   private static final int HORIZONTAL_INCREMENT = GPlanarPanoramicZoomLevel.TILE_WIDTH / 3;
   @SuppressWarnings("unused")
   private static final int VERTICAL_INCREMENT   = GPlanarPanoramicZoomLevel.TILE_HEIGHT / 3;


   private class Tile
            extends
               GImage {
      private static final long               serialVersionUID = 1L;


      private final GPlanarPanoramicZoomLevel _zoomLevel;
      private final int                       _x;
      private final int                       _y;

      //private BufferedImage                   _image;
      //private transient IHandler              _handler;


      private final Rectangle                 _pixelsBounds;


      private Tile(final GPlanarPanoramicZoomLevel zoomLevel,
                   final int x,
                   final int y) {
         super();

         _zoomLevel = zoomLevel;
         _x = x;
         _y = y;

         _pixelsBounds = initializePixelsBounds();
      }


      private Rectangle initializePixelsBounds() {
         //final Vector2D lower = new Vector2D(getPixelXInPanoramic(), getPixelYInPanoramic());
         //final Vector2D extent = new Vector2D(GPlanarPanoramicZoomLevel.TILE_WIDTH, GPlanarPanoramicZoomLevel.TILE_HEIGHT);
         return new Rectangle(getPixelXInPanoramic(), getPixelYInPanoramic(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private void positionate() {
         tryToLoadImage();
         final Rectangle bounds = calculateBounds();
         //setBounds(calculateBounds());
         setPixelSize(bounds.width, bounds.height);
      }


      private Rectangle calculateBounds() {
         return new Rectangle(calculateXPosition(), calculateYPosition(), GPlanarPanoramicZoomLevel.TILE_WIDTH,
                  GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private int getPixelXInPanoramic() {
         return (_x * GPlanarPanoramicZoomLevel.TILE_WIDTH);
      }


      private int getPixelYInPanoramic() {
         return (_y * GPlanarPanoramicZoomLevel.TILE_HEIGHT);
      }


      private int calculateXPosition() {
         return _offset.x + getPixelXInPanoramic();
      }


      private int calculateYPosition() {
         return _offset.y + getPixelYInPanoramic();
      }


      private void tryToLoadImage() {
         //               final int priority = ((_zoomLevel.getLevel() * 1000000) - (_x * 1000)) + _y;
         //               tryToLoadImage(priority);
         GImageLoader.load(getTileFileName(), null); //TODO add handler
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


      @Override
      public void paint(final Graphics g) {
         final Graphics2D g2d = (Graphics2D) g;

         if (_image == null) {
            final Tile ancestor = getNearestAncestorWithTextureInCache();
            if (ancestor != null) {
               paintAncestor(g2d, ancestor);
            }
         }
         else {
            g2d.drawImage(_image, 0, 0, null);
         }

         if (_debug) {
            renderDebug(g2d);
         }

      }


      private void paintAncestor(final Graphics2D g2d,
                                 final Tile ancestor) {

         // final RenderingHints renderHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
         // renderHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
         // g2d.setRenderingHints(renderHints);

         ancestor.tryToLoadImage();

         final double scale = Math.pow(2, _zoomLevel.getLevel() - ancestor._zoomLevel.getLevel());

         final Rectangle scaledAncestorBounds = ancestor._pixelsBounds.scale(scale);

         final IVector2 lower = _pixelsBounds._lower.sub(scaledAncestorBounds._lower).div(scale);
         final IVector2 upper = _pixelsBounds._upper.sub(scaledAncestorBounds._lower).div(scale);

         final int sx1 = Math.round((float) lower.x());
         final int sy1 = Math.round((float) lower.y());
         final int sx2 = Math.round((float) upper.x());
         final int sy2 = Math.round((float) upper.y());
         g2d.drawImage( //
                  ancestor._image, //
                  0, 0, //
                  GPlanarPanoramicZoomLevel.TILE_WIDTH, GPlanarPanoramicZoomLevel.TILE_HEIGHT, //
                  sx1, sy1, //
                  sx2, sy2, //
                  null);
      }


      private boolean hasTextureInCache() {
         return (_image != null) || _loader.canLoadFromLocalCache(getTileFileName());
      }


      private Tile getNearestAncestorWithTextureInCache() {
         final int zoomLevel = _zoomLevel.getLevel();

         if (zoomLevel <= 1) {
            return null;
         }

         final int parentLevel = zoomLevel - 1;
         final int parentX = _x / 2; // integer (truncated) division
         final int parentY = _y / 2; // integer (truncated) division

         final Tile parent = new Tile(getZoomLevel(parentLevel), parentX, parentY);
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


      private String getTileFileName() {
         return _url + Integer.toString(_zoomLevel.getLevel()) + "tile-" + _x + "-" + _y + ".jpg";
      }


      private void remove() {
         if (_loadID != null) {
            _loader.cancelLoad(_loadID);
         }

      }


      private boolean touches(final Rectangle bounds) {
         return bounds.intersects(calculateBounds());
      }


   }


   private final ILoader                         _loader;
   //private final GFileName                       _path;
   private final String                          _url;
   private final String                          _name;
   private final boolean                         _debug;

   private final List<GPlanarPanoramicZoomLevel> _zoomLevels;
   private final int                             _minLevel;
   private final int                             _maxLevel;
   private final List<Tile>                      _tiles  = new ArrayList<Tile>();

   private final int                             _currentLevel;
   private final Point                           _offset = new Point(0, 0);

   private JLabel                                _zoomInButton;
   private JLabel                                _zoomOutButton;
   private JSlider                               _zoomSlider;

   private Point                                 _dragLastPosition;

   private ILoader.LoadID                        _loadID;


   public GPlanarPanoramicViewer(final ILoader loader,
                                 final GFileName path,
                                 final String name) throws IOException {
      this(loader, path, name, false);
   }


   public GPlanarPanoramicViewer(final ILoader loader,
                                 final GFileName path,
                                 final String name,
                                 final boolean debug) throws IOException {

      GAssert.notNull(loader, "loader");
      GAssert.notNull(path, "path");
      GAssert.notNull(name, "name");

      _loader = loader;
      _path = path;
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
   }

}
