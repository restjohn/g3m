

package org.glob3.mobile.generated;

public class TokenDownloadListener
    implements
      IBufferDownloadListener {
  private final BingLayer _bingLayer;


  public TokenDownloadListener(final BingLayer bingLayer) {
    _bingLayer = bingLayer;
  }


  public final void onDownload(final URL url,
                               final IByteBuffer buffer) {


    final String String = buffer.getAsString();
    final JSONBaseObject json = IJSONParser.instance().parse(String);

    final String authentication = json.getObject().getObjectForKey(
        "authenticationResultCode").getString().getValue();
    if (authentication.compareTo("ValidCredentials") != 0) {
      ILogger.instance().logError(
          "Could not validate against Bing. Please check your key!");
    }
    else {
      final JSONObject data = json.getObject().getObjectForKey("resourceSets").getArray().getElement(
          0).getObject().getObjectForKey("resources").getArray().getElement(0).getObject();

      final JSONArray subDomArray = data.getObjectForKey("imageUrlSubdomains").getArray();
      final java.util.ArrayList<String> subdomains = new java.util.ArrayList<String>();
      final int numSubdomains = subDomArray.getSize();
      for (int i = 0; i < numSubdomains; i++) {
        subdomains.add(subDomArray.getElement(i).getString().getValue());
      }
      _bingLayer.setSubDomains(subdomains);


      String tileURL = data.getObjectForKey("imageUrl").getString().getValue();

      // set language
      tileURL = IStringUtils.instance().replaceSubstring(tileURL, "{culture}",
          _bingLayer.getLocale());

      _bingLayer.setTilePetitionString(tileURL);

      IJSONParser.instance().deleteJSONData(json);
    }
  }


  public final void onError(final URL url) {
    System.out.println("onError");
  }


  public final void onCancel(final URL url) {
    System.out.println("onCancel");

  }


  public final void onCanceledDownload(final URL url,
                                       final IByteBuffer data) {
    System.out.println("onCanceledDownload");

  }


  public void dispose() {
  }

}
