

package org.glob3.mobile.generated;

//
// MultiLayerTileTexturizer.cpp
// G3MiOSSDK
//
// Created by Diego Gomez Deck on 08/08/12.
//
//

//
// MultiLayerTileTexturizer.hpp
// G3MiOSSDK
//
// Created by Diego Gomez Deck on 08/08/12.
//
//


// C++ TO JAVA CONVERTER NOTE: Java has no need of forward class
// declarations:
// class IGLTextureId;
// C++ TO JAVA CONVERTER NOTE: Java has no need of forward class
// declarations:
// class TileTextureBuilder;
// C++ TO JAVA CONVERTER NOTE: Java has no need of forward class
// declarations:
// class LayerSet;
// C++ TO JAVA CONVERTER NOTE: Java has no need of forward class
// declarations:
// class IDownloader;
// C++ TO JAVA CONVERTER NOTE: Java has no need of forward class
// declarations:
// class LeveledTexturedMesh;
// C++ TO JAVA CONVERTER NOTE: Java has no need of forward class
// declarations:
// class IFloatBuffer;

public class MultiLayerTileTexturizer
    extends
      TileTexturizer {
  public final LayerSet _layerSet;
  private TilesRenderParameters _parameters;

  private IFloatBuffer _texCoordsCache;


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: IFloatBuffer* getTextureCoordinates(const
  // TileRenderContext* trc) const
  private IFloatBuffer getTextureCoordinates(final TileRenderContext trc) {
    if (_texCoordsCache == null) {
      _texCoordsCache = trc.getTessellator().createUnitTextCoords();
    }
    return _texCoordsCache;
  }

  private int _pendingTopTileRequests;

  private TexturesHandler _texturesHandler;


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: LeveledTexturedMesh* getMesh(Tile* tile) const
  private LeveledTexturedMesh getMesh(final Tile tile) {
    final TileTextureBuilderHolder tileBuilderHolder = (TileTextureBuilderHolder) tile.getTexturizerData();
    return (tileBuilderHolder == null) ? null
                                      : tileBuilderHolder.get().getMesh();
  }


  public MultiLayerTileTexturizer(final LayerSet layerSet) {
    _layerSet = layerSet;
    _parameters = null;
    _texCoordsCache = null;
    _pendingTopTileRequests = 0;
    _texturesHandler = null;

  }


  public final void countTopTileRequest() {
    _pendingTopTileRequests--;
  }


  @Override
  public void dispose() {
    if (_texCoordsCache != null) {
      if (_texCoordsCache != null) {
        _texCoordsCache.dispose();
      }
      _texCoordsCache = null;
    }
  }


  @Override
  public final boolean isReady(final RenderContext rc) {
    return (_pendingTopTileRequests <= 0) && _layerSet.isReady();
  }


  @Override
  public final void initialize(final InitializationContext ic,
                               final TilesRenderParameters parameters) {
    _parameters = parameters;
    _layerSet.initialize(ic);
  }


  @Override
  public final Mesh texturize(final RenderContext rc,
                              final TileRenderContext trc,
                              final Tile tile,
                              final Mesh tessellatorMesh,
                              final Mesh previousMesh) {
    _texturesHandler = rc.getTexturesHandler();


    TileTextureBuilderHolder builderHolder = (TileTextureBuilderHolder) tile.getTexturizerData();

    if (builderHolder == null) {
      builderHolder = new TileTextureBuilderHolder(new TileTextureBuilder(
          this, rc, _layerSet, _parameters, rc.getDownloader(), tile,
          tessellatorMesh, getTextureCoordinates(trc)));
      tile.setTexturizerData(builderHolder);
    }

    if (trc.isForcedFullRender()) {
      builderHolder.get().start();
    }
    else {
      // C++ TO JAVA CONVERTER TODO TASK: Java does not allow declaring
      // types within methods:
      // class BuilderStartTask : public FrameTask
      // {
      // private:
      // TileTextureBuilder* _builder;
      //
      // public:
      // BuilderStartTask(TileTextureBuilder* builder) : _builder(builder)
      // {
      // _builder->_retain();
      // }
      //
      // virtual ~BuilderStartTask()
      // {
      // _builder->_release();
      // }
      //
      // void execute(const RenderContext* rc)
      // {
      // _builder->start();
      // }
      //
      // boolean isCanceled(const RenderContext *rc)
      // {
      // return _builder->isCanceled();
      // }
      // };
      rc.getFrameTasksExecutor().addPreRenderTask(
          new BuilderStartTask(builderHolder.get()));
    }

    tile.setTexturizerDirty(false);
    return builderHolder.get().getMesh();
  }


  @Override
  public final void tileToBeDeleted(final Tile tile,
                                    final Mesh mesh) {

    final TileTextureBuilderHolder builderHolder = (TileTextureBuilderHolder) tile.getTexturizerData();

    if (builderHolder != null) {
      builderHolder.get().cancel();
      builderHolder.get().cleanTile();
      builderHolder.get().cleanMesh();
    }
    else {
      if (mesh != null) {
        ILogger.instance().logInfo("break (point) on me 4\n");
      }
    }
  }


  @Override
  public final boolean tileMeetsRenderCriteria(final Tile tile) {
    return false;
  }


  @Override
  public final void justCreatedTopTile(final RenderContext rc,
                                       final Tile tile) {
    final java.util.ArrayList<Petition> petitions = _layerSet.createTileMapPetitions(
        rc, tile, _parameters._tileTextureWidth,
        _parameters._tileTextureHeight);

    _pendingTopTileRequests += petitions.size();

    final int priority = 1000000000; // very big priority for toplevel tiles
    for (int i = 0; i < petitions.size(); i++) {
      final Petition petition = petitions.get(i);
      rc.getDownloader().requestImage(new URL(petition.getURL()), priority,
          new TopTileDownloadListener(this), true);

      if (petition != null) {
        petition.dispose();
      }
    }
  }


  @Override
  public final void ancestorTexturedSolvedChanged(final Tile tile,
                                                  final Tile ancestorTile,
                                                  final boolean textureSolved) {
    if (!textureSolved) {
      return;
    }

    if (tile.isTextureSolved()) {
      return;
    }

    final LeveledTexturedMesh ancestorMesh = getMesh(ancestorTile);
    if (ancestorMesh == null) {
      return;
    }

    final IGLTextureId glTextureId = ancestorMesh.getTopLevelGLTextureId();
    if (glTextureId == null) {
      return;
    }

    final LeveledTexturedMesh tileMesh = getMesh(tile);
    if (tileMesh == null) {
      return;
    }

    final int level = tile.getLevel() - ancestorTile.getLevel()
                      - _parameters._topLevel;
    _texturesHandler.retainGLTextureId(glTextureId);
    if (!tileMesh.setGLTextureIdForLevel(level, glTextureId)) {
      _texturesHandler.releaseGLTextureId(glTextureId);
    }
  }


  public final IGLTextureId getTopLevelGLTextureIdForTile(final Tile tile) {
    final LeveledTexturedMesh mesh = (LeveledTexturedMesh) tile.getTexturizedMesh();

    return (mesh == null) ? null : mesh.getTopLevelGLTextureId();
  }


  @Override
  public final void onTerrainTouchEvent(final EventContext ec,
                                        final Geodetic3D position,
                                        final Tile tile) {
    _layerSet.onTerrainTouchEvent(ec, position, tile);
  }


  @Override
  public final void tileMeshToBeDeleted(final Tile tile,
                                        final Mesh mesh) {
    final TileTextureBuilderHolder builderHolder = (TileTextureBuilderHolder) tile.getTexturizerData();
    if (builderHolder != null) {
      builderHolder.get().cancel();
      builderHolder.get().cleanMesh();
    }
    else {
      if (mesh != null) {
        ILogger.instance().logInfo("break (point) on me 5\n");
      }
    }
  }

}
