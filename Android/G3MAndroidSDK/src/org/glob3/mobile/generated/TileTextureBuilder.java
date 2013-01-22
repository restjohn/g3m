

package org.glob3.mobile.generated;

public class TileTextureBuilder
    extends
      RCObject {
  private final MultiLayerTileTexturizer _texturizer;
  private Tile _tile;

  // const TileKey _tileKey;

  private java.util.ArrayList<Petition> _petitions = new java.util.ArrayList<Petition>();
  private int _petitionsCount;
  private int _stepsDone;

  private final IFactory _factory; // FINAL WORD REMOVE BY CONVERSOR RULE
  private final TexturesHandler _texturesHandler;
  private final TextureBuilder _textureBuilder;
  private final GL _gl;

  private final TilesRenderParameters _parameters;
  private final IDownloader _downloader;

  private final Mesh _tessellatorMesh;

  private final IFloatBuffer _texCoords;

  private final java.util.ArrayList<PetitionStatus> _status = new java.util.ArrayList<PetitionStatus>();
  private final java.util.ArrayList<Long> _requestsIds = new java.util.ArrayList<Long>();


  private boolean _finalized;
  private boolean _canceled;
  private boolean _anyCanceled;
  private boolean _alreadyStarted;

  public LeveledTexturedMesh _mesh;


  public TileTextureBuilder(final MultiLayerTileTexturizer texturizer,
                            final RenderContext rc,
                            final LayerSet layerSet,
                            final TilesRenderParameters parameters,
                            final IDownloader downloader,
                            final Tile tile)
  // _tileKey(tile->getKey()),
  {
    _texturizer = texturizer;
    _factory = rc.getFactory();
    _texturesHandler = rc.getTexturesHandler();
    _textureBuilder = rc.getTextureBuilder();
    _gl = rc.getGL();
    _parameters = parameters;
    _downloader = downloader;
    _tile = tile;
    _tessellatorMesh = null;
    _stepsDone = 0;
    _anyCanceled = false;
    _mesh = null;
    _texCoords = null;
    _finalized = false;
    _canceled = false;
    _alreadyStarted = false;
    _petitions = layerSet.createTileMapPetitions(rc, tile,
        parameters._tileTextureWidth, parameters._tileTextureHeight);

    _petitionsCount = _petitions.size();

    for (int i = 0; i < _petitionsCount; i++) {
      _status.add(PetitionStatus.STATUS_PENDING);
    }
  }


  public TileTextureBuilder(final MultiLayerTileTexturizer texturizer,
                            final RenderContext rc,
                            final LayerSet layerSet,
                            final TilesRenderParameters parameters,
                            final IDownloader downloader,
                            final Tile tile,
                            final Mesh tessellatorMesh,
                            final IFloatBuffer texCoords)
  // _tileKey(tile->getKey()),
  {
    _texturizer = texturizer;
    _factory = rc.getFactory();
    _texturesHandler = rc.getTexturesHandler();
    _textureBuilder = rc.getTextureBuilder();
    _gl = rc.getGL();
    _parameters = parameters;
    _downloader = downloader;
    _tile = tile;
    _tessellatorMesh = tessellatorMesh;
    _stepsDone = 0;
    _anyCanceled = false;
    _mesh = null;
    _texCoords = texCoords;
    _finalized = false;
    _canceled = false;
    _alreadyStarted = false;
    _petitions = layerSet.createTileMapPetitions(rc, tile,
        parameters._tileTextureWidth, parameters._tileTextureHeight);

    _petitionsCount = _petitions.size();

    for (int i = 0; i < _petitionsCount; i++) {
      _status.add(PetitionStatus.STATUS_PENDING);
    }

    _mesh = createMesh();
  }


  public final void start() {
    if (_canceled) {
      return;
    }
    if (_alreadyStarted) {
      return;
    }
    _alreadyStarted = true;

    if (_tile == null) {
      return;
    }

    for (int i = 0; i < _petitionsCount; i++) {
      final Petition petition = _petitions.get(i);

      // const long priority = _tile->getLevel() * 1000000 + _tile->getRow()
      // * 1000 + _tile->getColumn();
      final long priority = _tile.getLevel();

      final long requestId = _downloader.requestImage(
          new URL(petition.getURL()), priority,
          new BuilderDownloadStepDownloadListener(this, i), true);

      _requestsIds.add(requestId);
    }
  }


  @Override
  public void dispose() {
    if (!_finalized && !_canceled) {
      cancel();
    }

    deletePetitions();
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: Rectangle* getImageRectangleInTexture(const Sector&
  // wholeSector, const Sector& imageSector, int textureWidth, int
  // textureHeight) const
  public final Rectangle getImageRectangleInTexture(final Sector wholeSector,
                                                    final Sector imageSector,
                                                    final int textureWidth,
                                                    final int textureHeight) {
    final Vector2D lowerFactor = wholeSector.getUVCoordinates(imageSector.lower());

    final double widthFactor = imageSector.getDeltaLongitude().div(
        wholeSector.getDeltaLongitude());
    final double heightFactor = imageSector.getDeltaLatitude().div(
        wholeSector.getDeltaLatitude());

    return new Rectangle(lowerFactor._x * textureWidth, (1.0 - lowerFactor._y)
                                                        * textureHeight,
        widthFactor * textureWidth, heightFactor * textureHeight);
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: void composeAndUploadTexture() const
  public final void composeAndUploadTexture() {
    final java.util.ArrayList<IImage> images = new java.util.ArrayList<IImage>();
    final java.util.ArrayList<Rectangle> rectangles = new java.util.ArrayList<Rectangle>();
    String textureId = _tile.getKey().tinyDescription();

    final int textureWidth = _parameters._tileTextureWidth;
    final int textureHeight = _parameters._tileTextureHeight;

    final Sector tileSector = _tile.getSector();

    for (int i = 0; i < _petitionsCount; i++) {
      final Petition petition = _petitions.get(i);
      final IImage image = petition.getImage();

      if (image != null) {
        images.add(image);

        rectangles.add(getImageRectangleInTexture(tileSector,
            petition.getSector(), textureWidth, textureHeight));

        textureId += petition.getURL().getPath();
        textureId += "_";
      }
    }

    if (images.size() > 0) {
      // int __TESTING_mipmapping;
      final boolean isMipmap = false;

      final IImage image = _textureBuilder.createTextureFromImages(_gl,
          _factory, images, rectangles, textureWidth, textureHeight);

      final IGLTextureId glTextureId = _texturesHandler.getGLTextureId(image,
          GLFormat.rgba(), textureId, isMipmap);

      if (glTextureId != null) {
        if (!_mesh.setGLTextureIdForLevel(0, glTextureId)) {
          _texturesHandler.releaseGLTextureId(glTextureId);
        }
      }

      if (image != null) {
        image.dispose();
      }
    }
  }


  @Override
  public final void finalize() {
    if (!_finalized) {
      _finalized = true;

      if (!_canceled && (_tile != null) && (_mesh != null)) {
        composeAndUploadTexture();
      }

      _tile.setTextureSolved(true);
    }
  }


  public final void deletePetitions() {
    for (int i = 0; i < _petitionsCount; i++) {
      final Petition petition = _petitions.get(i);
      if (petition != null) {
        petition.dispose();
      }
    }
    _petitions.clear();
    _petitionsCount = 0;
  }


  public final void stepDone() {
    _stepsDone++;

    if (_stepsDone == _petitionsCount) {
      if (_anyCanceled) {
        ILogger.instance().logInfo("Completed with cancelation\n");
      }

      finalize();
    }
  }


  public final void cancel() {
    if (_canceled) {
      return;
    }

    _canceled = true;

    if (!_finalized) {
      for (int i = 0; i < _requestsIds.size(); i++) {
        final long requestId = _requestsIds.get(i);
        _downloader.cancelRequest(requestId);
      }
    }
    _requestsIds.clear();
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: boolean isCanceled() const
  public final boolean isCanceled() {
    return _canceled;
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: void checkIsPending(int position) const
  public final void checkIsPending(final int position) {
    if (_status.get(position) != PetitionStatus.STATUS_PENDING) {
      ILogger.instance().logError(
          "Logic error: Expected STATUS_PENDING at position #%d but found status: %d\n",
          position, _status.get(position));
    }
  }


  public final void stepDownloaded(final int position,
                                   final IImage image) {
    if (_canceled) {
      return;
    }
    checkIsPending(position);

    System.out.println("Donwloaded Image: " + image.description());

    _status.set(position, PetitionStatus.STATUS_DOWNLOADED);
    _petitions.get(position).setImage(image.shallowCopy());

    stepDone();
  }


  public final void stepCanceled(final int position) {
    if (_canceled) {
      return;
    }
    checkIsPending(position);

    _anyCanceled = true;

    _status.set(position, PetitionStatus.STATUS_CANCELED);

    stepDone();
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: LeveledTexturedMesh* createMesh() const
  public final LeveledTexturedMesh createMesh() {
    final java.util.ArrayList<LazyTextureMapping> mappings = new java.util.ArrayList<LazyTextureMapping>();

    Tile ancestor = _tile;
    boolean fallbackSolved = false;
    while (ancestor != null) {
      LazyTextureMapping mapping;
      if (fallbackSolved) {
        mapping = null;
      }
      else {
        mapping = new LazyTextureMapping(new LTMInitializer(_tile, ancestor,
            _texCoords), _texturesHandler, false);
      }

      if (ancestor != _tile) {
        if (!fallbackSolved) {
          final IGLTextureId glTextureId = _texturizer.getTopLevelGLTextureIdForTile(ancestor);
          if (glTextureId != null) {
            _texturesHandler.retainGLTextureId(glTextureId);
            mapping.setGLTextureId(glTextureId);
            fallbackSolved = true;
          }
        }
      }
      else {
        if (mapping.getGLTextureId() != null) {
          ILogger.instance().logInfo("break (point) on me 3\n");
        }
      }

      mappings.add(mapping);
      ancestor = ancestor.getParent();
    }

    if (mappings.size() != _tile.getLevel() + 1) {
      ILogger.instance().logInfo("pleae break (point) me\n");
    }

    return new LeveledTexturedMesh(_tessellatorMesh, false, mappings);
  }


  public final LeveledTexturedMesh getMesh() {
    return _mesh;
  }


  public final void cleanMesh() {
    if (_mesh != null) {
      _mesh = null;
    }
  }


  public final void cleanTile() {
    if (_tile != null) {
      _tile = null;
    }
  }

}
