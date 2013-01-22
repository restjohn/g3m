

package org.glob3.mobile.generated;

import org.glob3.mobile.specific.TileVisitorCache;


public class TileRenderer
    extends
      Renderer {

  private int __degubCounter = 0;
  private final TileTessellator _tessellator;
  private final TileTexturizer _texturizer;
  private final TilesRenderParameters _parameters;
  private final boolean _showStatistics;
  private boolean _topTilesJustCreated;

  private ITileVisitor _tileVisitor = null;

  private Camera _lastCamera;

  private final java.util.ArrayList<Tile> _topLevelTiles = new java.util.ArrayList<Tile>();

  private ITimer _lastSplitTimer; // timer to start every time a tile get
                                  // splitted into subtiles


  private void clearTopLevelTiles() {
    for (int i = 0; i < _topLevelTiles.size(); i++) {
      final Tile tile = _topLevelTiles.get(i);
    }

    _topLevelTiles.clear();
  }


  public void acceptTileVisitor(final ITileVisitor tileVisitor) {
    _tileVisitor = tileVisitor;
  }


  public void visitTilesTouchesWith(final RenderContext rc,
                                    final Sector sector,
                                    final int topLevel,
                                    final int maxLevel) {
    if (_tileVisitor != null) {
      final int topLevelCache = (topLevel < _parameters._topLevel)
                                                                  ? _parameters._topLevel
                                                                  : topLevel;

      final int maxLevelCache = (maxLevel > _parameters._maxLevel)
                                                                  ? _parameters._maxLevel
                                                                  : maxLevel;
      // Get Tiles to Cache
      for (final Tile tile : _topLevelTiles) {
        if (tile.getSector().touchesWith(sector)) {
          _tileVisitor.visitTile(tile);
          visitSubTilesTouchesWith(rc, tile, sector, topLevelCache,
              maxLevelCache);
        }
      }
    }
  }


  private void visitSubTilesTouchesWith(final RenderContext rc,
                                        final Tile tile,
                                        final Sector sectorToVisit,
                                        final int topLevel,
                                        final int maxLevel) {
    if (tile.getLevel() < maxLevel) {
      for (final Tile tl : tile.getSubTiles()) {
        if (tl.getSector().touchesWith(sectorToVisit)) {
          if ((tile.getLevel() >= topLevel)) {
            __degubCounter++;
            rc.getLogger().logInfo(
                "Level: " + tl.getLevel() + ". Col: " + tl.getColumn()
                    + "; Row: " + tl.getRow() + ". Contador: "
                    + __degubCounter);
            _tileVisitor.visitTile(tl);
          }
          visitSubTilesTouchesWith(rc, tl, sectorToVisit, topLevel, maxLevel);
        }
      }
    }
  }


  private void createTopLevelTiles(final InitializationContext ic) {
    final Angle fromLatitude = _parameters._topSector.lower().latitude();
    final Angle fromLongitude = _parameters._topSector.lower().longitude();

    final Angle deltaLan = _parameters._topSector.getDeltaLatitude();
    final Angle deltaLon = _parameters._topSector.getDeltaLongitude();

    final Angle tileHeight = deltaLan.div(_parameters._splitsByLatitude);
    final Angle tileWidth = deltaLon.div(_parameters._splitsByLongitude);

    for (int row = 0; row < _parameters._splitsByLatitude; row++) {
      final Angle tileLatFrom = tileHeight.times(row).add(fromLatitude);
      final Angle tileLatTo = tileLatFrom.add(tileHeight);

      for (int col = 0; col < _parameters._splitsByLongitude; col++) {
        final Angle tileLonFrom = tileWidth.times(col).add(fromLongitude);
        final Angle tileLonTo = tileLonFrom.add(tileWidth);

        final Geodetic2D tileLower = new Geodetic2D(tileLatFrom, tileLonFrom);
        final Geodetic2D tileUpper = new Geodetic2D(tileLatTo, tileLonTo);
        final Sector sector = new Sector(tileLower, tileUpper);

        final Tile tile = new Tile(_texturizer, null, sector,
            _parameters._topLevel, row, col);
        _topLevelTiles.add(tile);
      }
    }

    ic.getLogger().logInfo("Created %d top level tiles", _topLevelTiles.size());

    _topTilesJustCreated = true;
  }

  private TilesStatistics _lastStatistics = new TilesStatistics();

  private boolean _firstRender;


  // class DistanceToCenterTileComparison {
  // private:
  // const Camera* _camera;
  // const Planet* _planet;
  // std::map<TileKey, double> _distancesCache;
  //
  // public:
  // DistanceToCenterTileComparison(const Camera *camera,
  // const Planet *planet):
  // _camera(camera),
  // _planet(planet)
  // {}
  //
  // void initialize() {
  // _distancesCache.clear();
  // }
  //
  // double getSquaredDistanceToCamera(const Tile* tile) {
  // const TileKey key = tile->getKey();
  // double distance = _distancesCache[key];
  // if (distance == 0) {
  // const Geodetic2D center = tile->getSector().getCenter();
  // const Vector3D cameraPosition = _camera->getPosition();
  // const Vector3D centerVec3 = _planet->toVector3D(center);
  //
  // distance = centerVec3.sub(cameraPosition).squaredLength();
  //
  // _distancesCache[key] = distance;
  // }
  //
  // return distance;
  //
  // }
  //
  // inline bool operator()(const Tile *t1,
  // const Tile *t2) {
  // // const Vector3D cameraPos = _camera->getPosition();
  // //
  // // const Vector3D center1 =
  // _planet->toVector3D(t1->getSector().getCenter());
  // // const Vector3D center2 =
  // _planet->toVector3D(t2->getSector().getCenter());
  // //
  // // const double dist1 = center1.sub(cameraPos).squaredLength();
  // // const double dist2 = center2.sub(cameraPos).squaredLength();
  //
  // const double dist1 = getSquaredDistanceToCamera(t1);
  // const double dist2 = getSquaredDistanceToCamera(t2);
  // return (dist1 < dist2);
  // }
  // };

  public TileRenderer(final TileTessellator tessellator,
                      final TileTexturizer texturizer,
                      final TilesRenderParameters parameters,
                      final boolean showStatistics) {
    _tessellator = tessellator;
    _texturizer = texturizer;
    _parameters = parameters;
    _showStatistics = showStatistics;
    _lastStatistics = new TilesStatistics();
    _topTilesJustCreated = false;
    _lastSplitTimer = null;
    _lastCamera = null;
    _firstRender = false;

  }


  @Override
  public void dispose() {
    clearTopLevelTiles();

  }


  @Override
  public final void initialize(final InitializationContext ic) {
    clearTopLevelTiles();
    createTopLevelTiles(ic);

    if (_lastSplitTimer != null) {
      if (_lastSplitTimer != null) {
        _lastSplitTimer.dispose();
      }
    }
    _lastSplitTimer = ic.getFactory().createTimer();

    _texturizer.initialize(ic, _parameters);


  }


  @Override
  public final void render(final RenderContext rc) {
    final TilesStatistics statistics = new TilesStatistics();
    // Saving camera for Long Press Event
    _lastCamera = rc.getCurrentCamera();

    final TileRenderContext trc = new TileRenderContext(_tessellator,
        _texturizer, _parameters, statistics, _lastSplitTimer, _firstRender); // if
                                                                              // first
                                                                              // render,
                                                                              // force
                                                                              // full
                                                                              // render

    if (_firstRender && _parameters._forceTopLevelTilesRenderOnStart) {
      acceptTileVisitor(new TileVisitorCache(
          (MultiLayerTileTexturizer) _texturizer, _parameters, rc));
      visitTilesTouchesWith(
          rc,
          new Sector(new Geodetic2D(Angle.fromDegrees(39.31),
              Angle.fromDegrees(-6.72)), new Geodetic2D(
              Angle.fromDegrees(39.38), Angle.fromDegrees(-6.64))), 0, 14);
      // force one render of the topLevel tiles to make the (toplevel)
      // textures loaded as they
      // will be used as last-chance fallback texture for any tile.
      _firstRender = false;

      for (int i = 0; i < _topLevelTiles.size(); i++) {
        final Tile tile = _topLevelTiles.get(i);
        tile.render(rc, trc, null);
      }
    }
    else {
      java.util.LinkedList<Tile> toVisit = new java.util.LinkedList<Tile>();
      for (int i = 0; i < _topLevelTiles.size(); i++) {
        toVisit.addLast(_topLevelTiles.get(i));
      }

      // DistanceToCenterTileComparison predicate =
      // DistanceToCenterTileComparison(rc->getCurrentCamera(),
      // rc->getPlanet());

      while (toVisit.size() > 0) {
        final java.util.LinkedList<Tile> toVisitInNextIteration = new java.util.LinkedList<Tile>();

        // predicate.initialize();
        // toVisit.sort(predicate);

        for (final Tile tile : toVisit) {
          tile.render(rc, trc, toVisitInNextIteration);
        }

        toVisit = toVisitInNextIteration;
      }
    }

    if (_showStatistics) {
      if (!_lastStatistics.equalsTo(statistics)) {
        _lastStatistics = statistics;
        statistics.log(rc.getLogger());
      }
    }

  }


  @Override
  public final boolean onTouchEvent(final EventContext ec,
                                    final TouchEvent touchEvent) {
    boolean handled = false;

    if (touchEvent.getType() == TouchEventType.LongPress) {

      if (_lastCamera != null) {
        final Vector2D pixel = touchEvent.getTouch(0).getPos();
        final Vector3D ray = _lastCamera.pixel2Ray(pixel);
        final Vector3D origin = _lastCamera.getCartesianPosition();

        final Planet planet = ec.getPlanet();

        final Vector3D positionCartesian = planet.closestIntersection(origin,
            ray);
        if (positionCartesian.isNan()) {
          return false;
        }

        final Geodetic3D position = planet.toGeodetic3D(positionCartesian);

        for (int i = 0; i < _topLevelTiles.size(); i++) {
          final Tile tile = _topLevelTiles.get(i).getDeepestTileContaining(
              position);
          if (tile != null) {
            _texturizer.onTerrainTouchEvent(ec, position, tile);
            handled = true;
          }
        }
      }
    }

    return handled;
  }


  @Override
  public final void onResizeViewportEvent(final EventContext ec,
                                          final int width,
                                          final int height) {

  }


  @Override
  public final boolean isReadyToRender(final RenderContext rc) {
    if (_topTilesJustCreated) {
      if (_texturizer != null) {
        final int topLevelTilesSize = _topLevelTiles.size();
        for (int i = 0; i < topLevelTilesSize; i++) {
          final Tile tile = _topLevelTiles.get(i);
          _texturizer.justCreatedTopTile(rc, tile);
        }
      }
      _topTilesJustCreated = false;
    }

    if (_parameters._forceTopLevelTilesRenderOnStart) {
      if (_tessellator != null) {
        if (!_tessellator.isReady(rc)) {
          return false;
        }
      }

      if (_texturizer != null) {
        if (!_texturizer.isReady(rc)) {
          return false;
        }
      }
    }

    return true;
  }


  @Override
  public final void start() {
    _firstRender = true;
  }


  @Override
  public final void stop() {
    _firstRender = false;
  }


  @Override
  public final void onResume(final InitializationContext ic) {

  }


  @Override
  public final void onPause(final InitializationContext ic) {

  }

}
