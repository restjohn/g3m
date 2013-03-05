package org.glob3.mobile.generated; 
public class TileRenderer extends LeafRenderer implements LayerSetChangedListener
{
  private final TileTessellator _tessellator;
  private ElevationDataProvider _elevationDataProvider;
  private TileTexturizer _texturizer;
  private LayerSet _layerSet;
  private final TilesRenderParameters _parameters;
  private final boolean _showStatistics;
  private boolean _topTilesJustCreated;

  private Camera     _lastCamera;
  private G3MContext _context;

  private java.util.ArrayList<Tile> _topLevelTiles = new java.util.ArrayList<Tile>();

  private ITimer _lastSplitTimer; // timer to start every time a tile get splitted into subtiles

  private void clearTopLevelTiles()
  {
    for (int i = 0; i < _topLevelTiles.size(); i++)
    {
      Tile tile = _topLevelTiles.get(i);
      if (tile != null)
         tile.dispose();
    }
  
    _topLevelTiles.clear();
  }
  private void createTopLevelTiles(G3MContext context)
  {
  
    final LayerTilesRenderParameters layerParameters = _layerSet.getLayerTilesRenderParameters();
    if (layerParameters == null)
    {
      ILogger.instance().logError("LayerSet returned a NULL for LayerTilesRenderParameters, can't create topTiles");
      return;
    }
  
    final Angle fromLatitude = layerParameters._topSector.lower().latitude();
    final Angle fromLongitude = layerParameters._topSector.lower().longitude();
  
    final Angle deltaLan = layerParameters._topSector.getDeltaLatitude();
    final Angle deltaLon = layerParameters._topSector.getDeltaLongitude();
  
    final Angle tileHeight = deltaLan.div(layerParameters._splitsByLatitude);
    final Angle tileWidth = deltaLon.div(layerParameters._splitsByLongitude);
  
    for (int row = 0; row < layerParameters._splitsByLatitude; row++)
    {
      final Angle tileLatFrom = tileHeight.times(row).add(fromLatitude);
      final Angle tileLatTo = tileLatFrom.add(tileHeight);
  
      for (int col = 0; col < layerParameters._splitsByLongitude; col++)
      {
        final Angle tileLonFrom = tileWidth.times(col).add(fromLongitude);
        final Angle tileLonTo = tileLonFrom.add(tileWidth);
  
        final Geodetic2D tileLower = new Geodetic2D(tileLatFrom, tileLonFrom);
        final Geodetic2D tileUpper = new Geodetic2D(tileLatTo, tileLonTo);
        final Sector sector = new Sector(tileLower, tileUpper);
  
  //      Tile* tile = new Tile(_texturizer, NULL, sector, _parameters->_topLevel, row, col);
        Tile tile = new Tile(_texturizer, null, sector, 0, row, col);
        _topLevelTiles.add(tile);
      }
    }
  
    context.getLogger().logInfo("Created %d top level tiles", _topLevelTiles.size());
  
    _topTilesJustCreated = true;
  }

  private boolean _firstRender;

  private void pruneTopLevelTiles()
  {
    for (int i = 0; i < _topLevelTiles.size(); i++)
    {
      Tile tile = _topLevelTiles.get(i);
      tile.prune(_texturizer, _elevationDataProvider);
    }
  }

  private Sector _lastVisibleSector;

  private java.util.ArrayList<VisibleSectorListenerEntry> _visibleSectorListeners = new java.util.ArrayList<VisibleSectorListenerEntry>();

  private long _texturePriority;

  private float _verticalExaggeration;

  public TileRenderer(TileTessellator tessellator, ElevationDataProvider elevationDataProvider, float verticalExaggeration, TileTexturizer texturizer, LayerSet layerSet, TilesRenderParameters parameters, boolean showStatistics, long texturePriority)
  {
     _tessellator = tessellator;
     _elevationDataProvider = elevationDataProvider;
     _verticalExaggeration = verticalExaggeration;
     _texturizer = texturizer;
     _layerSet = layerSet;
     _parameters = parameters;
     _showStatistics = showStatistics;
     _topTilesJustCreated = false;
     _lastSplitTimer = null;
     _lastCamera = null;
     _firstRender = false;
     _context = null;
     _lastVisibleSector = null;
     _texturePriority = texturePriority;
    _layerSet.setChangeListener(this);
  }

  public void dispose()
  {
    clearTopLevelTiles();
  
    if (_tessellator != null)
       _tessellator.dispose();
    if (_elevationDataProvider != null)
       _elevationDataProvider.dispose();
    if (_texturizer != null)
       _texturizer.dispose();
    if (_parameters != null)
       _parameters.dispose();
  
    if (_lastSplitTimer != null)
       _lastSplitTimer.dispose();
  
    if (_lastVisibleSector != null)
       _lastVisibleSector.dispose();
  
    final int visibleSectorListenersCount = _visibleSectorListeners.size();
    for (int i = 0; i < visibleSectorListenersCount; i++)
    {
      VisibleSectorListenerEntry entry = _visibleSectorListeners.get(i);
      if (entry != null)
         entry.dispose();
    }
  }

  public final void initialize(G3MContext context)
  {
    _context = context;
  
    clearTopLevelTiles();
    createTopLevelTiles(context);
  
    if (_lastSplitTimer != null)
       _lastSplitTimer.dispose();
    _lastSplitTimer = context.getFactory().createTimer();
  
    _layerSet.initialize(context);
    _texturizer.initialize(context, _parameters);
    if (_elevationDataProvider != null)
    {
      _elevationDataProvider.initialize(context);
    }
  }

  public final void render(G3MRenderContext rc, GLState parentState)
  {
    // Saving camera for use in onTouchEvent
    _lastCamera = rc.getCurrentCamera();
  
    TilesStatistics statistics = new TilesStatistics();
  
    TileRenderContext trc = new TileRenderContext(_tessellator, _elevationDataProvider, _texturizer, _layerSet, _parameters, statistics, _lastSplitTimer, _firstRender, _texturePriority, _verticalExaggeration); // if first render, force full render
  
    final int topLevelTilesCount = _topLevelTiles.size();
  
    if (_firstRender && _parameters._forceTopLevelTilesRenderOnStart)
    {
      // force one render pass of the topLevel tiles to make the (toplevel) textures loaded
      // as they will be used as last-chance fallback texture for any tile.
      _firstRender = false;
  
      for (int i = 0; i < topLevelTilesCount; i++)
      {
        Tile tile = _topLevelTiles.get(i);
        tile.render(rc, trc, parentState, null);
      }
    }
    else
    {
      java.util.LinkedList<Tile> toVisit = new java.util.LinkedList<Tile>();
      for (int i = 0; i < topLevelTilesCount; i++)
      {
        toVisit.addLast(_topLevelTiles.get(i));
      }
  
      while (toVisit.size() > 0)
      {
        java.util.LinkedList<Tile> toVisitInNextIteration = new java.util.LinkedList<Tile>();
  
        for (java.util.Iterator<Tile> iter = toVisit.iterator(); iter.hasNext();)
        {
          Tile tile = iter.next();
  
          tile.render(rc, trc, parentState, toVisitInNextIteration);
        }
  
        toVisit = toVisitInNextIteration;
      }
    }
  
    if (_showStatistics)
    {
      statistics.log(rc.getLogger());
    }
  
  
    final Sector renderedSector = statistics.getRenderedSector();
    if (renderedSector != null)
    {
      if ((_lastVisibleSector == null) || !renderedSector.isEqualsTo(_lastVisibleSector))
      {
        if (_lastVisibleSector != null)
           _lastVisibleSector.dispose();
        _lastVisibleSector = new Sector(renderedSector);
      }
    }
  
    if (_lastVisibleSector != null)
    {
      final int visibleSectorListenersCount = _visibleSectorListeners.size();
      for (int i = 0; i < visibleSectorListenersCount; i++)
      {
        VisibleSectorListenerEntry entry = _visibleSectorListeners.get(i);
  
        entry.tryToNotifyListener(_lastVisibleSector, rc);
      }
    }
  
  }

  public final boolean onTouchEvent(G3MEventContext ec, TouchEvent touchEvent)
  {
    if (_lastCamera == null)
    {
      return false;
    }
  
    if (touchEvent.getType() == TouchEventType.LongPress)
    {
      final Vector2I pixel = touchEvent.getTouch(0).getPos();
      final Vector3D ray = _lastCamera.pixel2Ray(pixel);
      final Vector3D origin = _lastCamera.getCartesianPosition();
  
      final Planet planet = ec.getPlanet();
  
      final Vector3D positionCartesian = planet.closestIntersection(origin, ray);
      if (positionCartesian.isNan())
      {
        return false;
      }
  
      final Geodetic3D position = planet.toGeodetic3D(positionCartesian);
  
      final int topLevelTilesSize = _topLevelTiles.size();
      for (int i = 0; i < topLevelTilesSize; i++)
      {
        final Tile tile = _topLevelTiles.get(i).getDeepestTileContaining(position);
        if (tile != null)
        {
          ILogger.instance().logInfo("Touched on %s", tile.description());
          if (_texturizer.onTerrainTouchEvent(ec, position, tile, _layerSet))
          {
            return true;
          }
        }
      }
  
    }
  
    return false;
  }

  public final void onResizeViewportEvent(G3MEventContext ec, int width, int height)
  {

  }

  public final boolean isReadyToRender(G3MRenderContext rc)
  {
    if (!_layerSet.isReady())
    {
      return false;
    }
  
    if (_elevationDataProvider != null)
    {
      if (!_elevationDataProvider.isReadyToRender(rc))
      {
        return false;
      }
    }
  
    if (_topTilesJustCreated)
    {
      _topTilesJustCreated = false;
  
      final int topLevelTilesCount = _topLevelTiles.size();
  
      if (_parameters._forceTopLevelTilesRenderOnStart)
      {
        TilesStatistics statistics = new TilesStatistics();
  
        TileRenderContext trc = new TileRenderContext(_tessellator, _elevationDataProvider, _texturizer, _layerSet, _parameters, statistics, _lastSplitTimer, true, _texturePriority, _verticalExaggeration);
  
        for (int i = 0; i < topLevelTilesCount; i++)
        {
          Tile tile = _topLevelTiles.get(i);
          tile.prepareForFullRendering(rc, trc);
        }
      }
  
      if (_texturizer != null)
      {
        for (int i = 0; i < topLevelTilesCount; i++)
        {
          Tile tile = _topLevelTiles.get(i);
          _texturizer.justCreatedTopTile(rc, tile, _layerSet);
        }
      }
    }
  
    if (_parameters._forceTopLevelTilesRenderOnStart)
    {
      final int topLevelTilesCount = _topLevelTiles.size();
      for (int i = 0; i < topLevelTilesCount; i++)
      {
        Tile tile = _topLevelTiles.get(i);
        if (!tile.isTextureSolved())
        {
          return false;
        }
      }
  
      if (_tessellator != null)
      {
        if (!_tessellator.isReady(rc))
        {
          return false;
        }
      }
  
      if (_texturizer != null)
      {
        if (!_texturizer.isReady(rc, _layerSet))
        {
          return false;
        }
      }
    }
  
    return true;
  }


  public final void start()
  {
    _firstRender = true;
  }

  public final void stop()
  {
    _firstRender = false;
  }

  public final void onResume(G3MContext context)
  {

  }

  public final void onPause(G3MContext context)
  {
    recreateTiles();
  }

  public final void onDestroy(G3MContext context)
  {

  }

  public final void setEnable(boolean enable)
  {
    super.setEnable(enable);

    if (!enable)
    {
      pruneTopLevelTiles();
    }
  }

  public final void changed(LayerSet layerSet)
  {
    // recreateTiles();
  
    // recreateTiles() delete tiles, then meshes, and delete textures from the GPU so it has to be executed in the OpenGL thread
    _context.getThreadUtils().invokeInRendererThread(new RecreateTilesTask(this), true);
  }

  public final void recreateTiles()
  {
    pruneTopLevelTiles();
    clearTopLevelTiles();
    _firstRender = true;
    createTopLevelTiles(_context);
  }

  /**
   Answer the visible-sector, it can be null if globe was not yet rendered.
   */
  public final Sector getVisibleSector()
  {
    return _lastVisibleSector;
  }

  /**
   Add a listener for notification of visible-sector changes.

   @param stabilizationInterval How many time the visible-sector has to be settled (without changes) before triggering the event.  Useful for avoid process while the camera is being moved (as in animations).  If stabilizationInterval is zero, the event is triggered inmediatly.
   */
  public final void addVisibleSectorListener(VisibleSectorListener listener, TimeInterval stabilizationInterval)
  {
    _visibleSectorListeners.add(new VisibleSectorListenerEntry(listener, stabilizationInterval));
  }

  /**
   Add a listener for notification of visible-sector changes.

   The event is triggered immediately without waiting for the visible-sector get settled.
   */
  public final void addVisibleSectorListener(VisibleSectorListener listener)
  {
    addVisibleSectorListener(listener, TimeInterval.zero());
  }

  /**
   * Set the download-priority used by Tiles (for downloading textures).
   *
   * @param texturePriority: new value for download priority of textures
   */
  public final void setTexturePriority(long texturePriority)
  {
    _texturePriority = texturePriority;
  }

  /**
   * Return the current value for the download priority of textures
   *
   * @return _texturePriority: long
   */
  public final long getTexturePriority()
  {
    return _texturePriority;
  }

  /**
   * @see Renderer#isTileRenderer()
   */
  public final boolean isTileRenderer()
  {
    return true;
  }

}