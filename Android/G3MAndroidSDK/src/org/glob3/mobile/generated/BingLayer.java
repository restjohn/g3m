

package org.glob3.mobile.generated;

public class BingLayer
    extends
      Layer {

  private final Sector _sector;
  private URL _mapServerURL = new URL();
  private final String _key;
  private final Language _locale;
  private final MapType _mapType;
  private String _tilePetitionString;
  private java.util.ArrayList<String> _subDomains = new java.util.ArrayList<String>();
  private boolean _isReady;

  private boolean _jsonIsReady = false;


  public BingLayer(final URL mapServerURL,
                   final LayerCondition condition,
                   final Sector sector,
                   final MapType mapType,
                   final Language locale,
                   final String key) {
    super(condition);
    _sector = new Sector(sector);
    _mapServerURL = new URL(mapServerURL);
    _mapType = mapType;
    _locale = locale;
    _key = key;
    _tilePetitionString = new String();
    _isReady = false;

  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: boolean isReady()const
  @Override
  public final boolean isReady() {
    return _isReady;
  }


  public final void setTilePetitionString(final String tilePetitionString) {
    _tilePetitionString = tilePetitionString;
    _isReady = true;
    _jsonIsReady = true;
  }


  public final void setSubDomains(final java.util.ArrayList<String> subDomains) {
    _subDomains = subDomains;
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: String getLocale()const
  public final String getLocale() {
    if (_locale == Language.English) {
      return "en-US";
    }
    if (_locale == Language.Spanish) {
      return "es-ES";
    }
    if (_locale == Language.German) {
      return "de-DE";
    }
    if (_locale == Language.French) {
      return "fr-FR";
    }
    if (_locale == Language.Dutch) {
      return "nl-BE";
    }
    if (_locale == Language.Italian) {
      return "it-IT";
    }
    return "en-US";
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: String getMapTypeString() const
  public final String getMapTypeString() {
    if (_mapType == MapType.Road) {
      return "Road";
    }
    if (_mapType == MapType.Aerial) {
      return "Aerial";
    }
    if (_mapType == MapType.Hybrid) {
      return "AerialWithLabels";
    }
    return "Aerial";
  }


  @Override
  public final void initialize(final InitializationContext ic) {

    String tileURL = "";
    tileURL += _mapServerURL.getPath();
    tileURL += "/";
    tileURL += getMapTypeString();
    tileURL += "?key=";
    tileURL += _key;

    final URL url = new URL(tileURL);
    final Long requestId = ic.getDownloader().requestBuffer(url, 100000000,
        new TokenDownloadListener(this), true);

    if (requestId == -1) {
      throw new RuntimeException("arrrrg");
    }

  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: java.util.ArrayList<Petition*> getMapPetitions(const
  // RenderContext* rc, const Tile* tile, int width, int height) const
  @Override
  public final java.util.ArrayList<Petition> getMapPetitions(final RenderContext rc,
                                                             final Tile tile,
                                                             final int width,
                                                             final int height) {
    final java.util.ArrayList<Petition> petitions = new java.util.ArrayList<Petition>();
    if (_jsonIsReady) {
      final Sector tileSector = tile.getSector();

      if (!_sector.touchesWith(tileSector)) {
        return petitions;
      }

      final Sector sector = tileSector.intersection(_sector);


      // Server name
      String req = _mapServerURL.getPath();

      // If the server refer to itself as localhost...
      final int pos = req.indexOf("localhost");
      if (pos != -1) {
        req = req.substring(pos + 9);

        final int pos2 = req.indexOf("/", 8);
        final String newHost = req.substring(0, pos2);

        req = newHost + req;
      }

      // Key:AgOLISvN2b3012i-odPJjVxhB1dyU6avZ2vG9Ub6Z9-mEpgZHre-1rE8o-DUinUH

      // TODO: calculate the level correctly
      final int level = tile.getLevel() + 2;

      final xyTuple lowerTileXY = getTileXY(tileSector.lower(), level);
      final xyTuple upperTileXY = getTileXY(tileSector.upper(), level);

      final int deltaX = upperTileXY.x - lowerTileXY.x;
      final int deltaY = lowerTileXY.y - upperTileXY.y;

      final java.util.ArrayList<Integer> requiredTiles = new java.util.ArrayList<Integer>();

      int currentSubDomain = 0;
      final int numSubDomains = _subDomains.size();

      for (int x = lowerTileXY.x; x <= lowerTileXY.x + deltaX; x++) {
        for (int y = upperTileXY.y; y <= upperTileXY.y + deltaY; y++) {
          final int[] tileXY = new int[2];
          tileXY[0] = x;
          tileXY[1] = y;
          final Sector bingSector = getBingTileAsSector(tileXY, level);

          if (!bingSector.touchesWith(tileSector)) {
            continue;
          }

          // set the quadkey
          String url = IStringUtils.instance().replaceSubstring(
              _tilePetitionString, "{quadkey}", getQuadKey(tileXY, level));

          // set the subDomain (round-robbin)
          url = IStringUtils.instance().replaceSubstring(url, "{subdomain}",
              _subDomains.get(currentSubDomain % numSubDomains));
          currentSubDomain++;
          petitions.add(new Petition(bingSector, new URL(url)));

        }

      }
      if (lowerTileXY != null) {
        lowerTileXY.dispose();
      }
      if (upperTileXY != null) {
        upperTileXY.dispose();
      }

    }
    return petitions;
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: String getQuadKey(const int tileXY[], const int
  // level)const
  public final String getQuadKey(final int[] tileXY,
                                 final int level) {

    final int tileX = tileXY[0];
    final int tileY = tileXY[1];
    String quadKey = "";
    for (int i = level; i > 0; i--) {
      byte digit = (byte) '0';
      final int mask = 1 << (i - 1);
      if ((tileX & mask) != 0) {
        digit++;
      }
      if ((tileY & mask) != 0) {
        digit++;
        digit++;
      }
      quadKey += digit;
    }

    return quadKey;
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: xyTuple* getTileXY(const Geodetic2D latLon, const int
  // level)const
  public final xyTuple getTileXY(final Geodetic2D latLon,
                                 final int level) {

    // LatLon to Pixels XY
    final int mapSize = 256 << level;
    final double lonDeg = latLon.longitude().degrees();
    double latDeg = latLon.latitude().degrees();
    if (latDeg < -85.05112878) {
      latDeg = -85.05112878;
    }
    if (latDeg > 85.05112878) {
      latDeg = 85.05112878;
    }

    double x = (lonDeg + 180.0) / 360;
    final double sinLat = IMathUtils.instance().sin(
        latDeg * IMathUtils.instance().pi() / 180.0);
    double y = 0.5 - IMathUtils.instance().log((1 + sinLat) / (1 - sinLat))
               / (4.0 * IMathUtils.instance().pi());

    x = x * mapSize + 0.5;
    y = y * mapSize + 0.5;


    if (x < 0) {
      x = 0;
    }
    if (y < 0) {
      y = 0;
    }
    if (x > (mapSize - 1)) {
      x = mapSize - 1;
    }
    if (y > (mapSize - 1)) {
      y = mapSize - 1;
    }

    final int pixelX = (int) x;
    final int pixelY = (int) y;

    // Pixel XY to Tile XY
    final int tileX = pixelX / 256;
    final int tileY = pixelY / 256;

    final xyTuple tileXY = new xyTuple();

    tileXY.x = tileX;
    tileXY.y = tileY;

    return tileXY;
  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: Sector getBingTileAsSector(const int tileXY[], const int
  // level)const
  public final Sector getBingTileAsSector(final int[] tileXY,
                                          final int level) {


    final Geodetic2D topLeft = getLatLon(tileXY, level);
    final int maxTile = ((int) IMathUtils.instance().pow((double) 2,
        (double) level)) - 1;

    final Angle lowerLon = topLeft.longitude();
    final Angle upperLat = topLeft.latitude();

    final int[] tileBelow = new int[2];
    tileBelow[0] = tileXY[0];
    double lowerLatDeg;
    if (tileXY[1] + 1 > maxTile) {
      lowerLatDeg = -85.05112878;
    }
    else {
      tileBelow[1] = tileXY[1] + 1;
      lowerLatDeg = getLatLon(tileBelow, level).latitude().degrees();
    }


    final int[] tileRight = new int[2];
    double upperLonDeg;
    tileRight[1] = tileXY[1];
    if (tileXY[0] + 1 > maxTile) {
      upperLonDeg = 180.0;
    }
    else {
      tileRight[0] = tileXY[0] + 1;
      upperLonDeg = getLatLon(tileRight, level).longitude().degrees();
    }

    return new Sector(
        new Geodetic2D(Angle.fromDegrees(lowerLatDeg), lowerLon),
        new Geodetic2D(upperLat, Angle.fromDegrees(upperLonDeg)));

  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: Geodetic2D getLatLon(const int tileXY[], const int
  // level)const
  public final Geodetic2D getLatLon(final int[] tileXY,
                                    final int level) {


    int pixelX = tileXY[0] * 256;
    int pixelY = tileXY[1] * 256;

    // Pixel XY to LatLon
    final int mapSize = 256 << level;
    if (pixelX < 0) {
      pixelX = 0;
    }
    if (pixelY < 0) {
      pixelY = 0;
    }
    if (pixelX > mapSize - 1) {
      pixelX = mapSize - 1;
    }
    if (pixelY > mapSize - 1) {
      pixelY = mapSize - 1;
    }
    final double x = (((double) pixelX) / ((double) mapSize)) - 0.5;
    final double y = 0.5 - (((double) pixelY) / ((double) mapSize));

    final double latDeg = 90.0
                          - 360.0
                          * IMathUtils.instance().atan(
                              IMathUtils.instance().exp(
                                  -y * 2.0 * IMathUtils.instance().pi()))
                          / IMathUtils.instance().pi();
    final double lonDeg = 360.0 * x;

    return new Geodetic2D(Angle.fromDegrees(latDeg), Angle.fromDegrees(lonDeg));

  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: URL getFeatureInfoURL(const Geodetic2D& g, const
  // IFactory* factory, const Sector& tileSector, int width, int height)
  // const
  @Override
  public final URL getFeatureInfoURL(final Geodetic2D g,
                                     final IFactory factory,
                                     final Sector tileSector,
                                     final int width,
                                     final int height) {
    return URL.nullURL();

  }


  // C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
  // Java:
  // ORIGINAL LINE: boolean isTransparent() const
  @Override
  public final boolean isTransparent() {
    return false;
  }
}
