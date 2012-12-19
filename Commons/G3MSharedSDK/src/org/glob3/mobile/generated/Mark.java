package org.glob3.mobile.generated; 
//
//  Mark.cpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 06/06/12.
//  Copyright (c) 2012 IGO Software SL. All rights reserved.
//

//
//  Mark.hpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 06/06/12.
//  Copyright (c) 2012 IGO Software SL. All rights reserved.
//




//C++ TO JAVA CONVERTER NOTE: Java has no need of forward class declarations:
//class IImage;
//C++ TO JAVA CONVERTER NOTE: Java has no need of forward class declarations:
//class IFloatBuffer;
//C++ TO JAVA CONVERTER NOTE: Java has no need of forward class declarations:
//class IGLTextureId;
//C++ TO JAVA CONVERTER NOTE: Java has no need of forward class declarations:
//class MarkTouchListener;
//C++ TO JAVA CONVERTER NOTE: Java has no need of forward class declarations:
//class GLState;

public class Mark
{

	private final String _name;
	private URL _textureURL = new URL();
	private final Geodetic3D _position ;
	private Object _userData;
	private double _minDistanceToCamera;
	private MarkTouchListener _listener;

	private IGLTextureId _textureId;

	private Vector3D _cartesianPosition;
	private Vector3D getCartesianPosition(Planet planet)
	{
		if (_cartesianPosition == null)
		{
			_cartesianPosition = new Vector3D(planet.toCartesian(_position));
		}
		return _cartesianPosition;
	}

	private IFloatBuffer _vertices;
	private IFloatBuffer getVertices(Planet planet)
	{
		if (_vertices == null)
		{
			final Vector3D pos = getCartesianPosition(planet);
    
			FloatBufferBuilderFromCartesian3D vertex = new FloatBufferBuilderFromCartesian3D(CenterStrategy.noCenter(), Vector3D.zero());
			vertex.add(pos);
			vertex.add(pos);
			vertex.add(pos);
			vertex.add(pos);
    
			_vertices = vertex.create();
		}
		return _vertices;
	}

	private boolean _textureSolved;
	private IImage _textureImage;
	private int _textureWidth;
	private int _textureHeight;

	private boolean _renderedMark;

  public Mark(String name, URL textureURL, Geodetic3D position, Object userData, double minDistanceToCamera)
  {
	  this(name, textureURL, position, userData, minDistanceToCamera, null);
  }
  public Mark(String name, URL textureURL, Geodetic3D position, Object userData)
  {
	  this(name, textureURL, position, userData, 0, null);
  }
  public Mark(String name, URL textureURL, Geodetic3D position)
  {
	  this(name, textureURL, position, null, 0, null);
  }
//C++ TO JAVA CONVERTER NOTE: Java does not allow default values for parameters. Overloaded methods are inserted above.
//ORIGINAL LINE: Mark(const String name, const URL textureURL, const Geodetic3D position, Object* userData=null, double minDistanceToCamera=0, MarkTouchListener* listener=null) : _name(name), _textureURL(textureURL), _position(position), _userData(userData), _minDistanceToCamera(minDistanceToCamera), _listener(listener), _textureId(null), _cartesianPosition(null), _vertices(null), _textureSolved(false), _textureImage(null), _renderedMark(false), _textureWidth(0), _textureHeight(0)
  public Mark(String name, URL textureURL, Geodetic3D position, Object userData, double minDistanceToCamera, MarkTouchListener listener)
  {
	  _name = name;
	  _textureURL = new URL(textureURL);
	  _position = new Geodetic3D(position);
	  _userData = userData;
	  _minDistanceToCamera = minDistanceToCamera;
	  _listener = listener;
	  _textureId = null;
	  _cartesianPosition = null;
	  _vertices = null;
	  _textureSolved = false;
	  _textureImage = null;
	  _renderedMark = false;
	  _textureWidth = 0;
	  _textureHeight = 0;

  }

  public void dispose()
  {
	  if (_cartesianPosition != null)
		  _cartesianPosition.dispose();
	  if (_vertices != null)
		  _vertices.dispose();
  }

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const String getName() const
  public final String getName()
  {
	return _name;
  }

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const Geodetic3D getPosition() const
  public final Geodetic3D getPosition()
  {
	return _position;
  }

  public final void initialize(G3MContext context)
  {
	//  todo;
	if (!_textureSolved)
	{
	  IDownloader downloader = context.getDownloader();
  
	  downloader.requestImage(_textureURL, 1000000, TimeInterval.fromDays(30), new TextureDownloadListener(this), true);
	}
  }

//C++ TO JAVA CONVERTER NOTE: This was formerly a static local variable declaration (not allowed in Java):
  private Vector2D render_textureTranslation = new Vector2D(0.0, 0.0);
//C++ TO JAVA CONVERTER NOTE: This was formerly a static local variable declaration (not allowed in Java):
  private Vector2D render_textureScale = new Vector2D(1.0, 1.0);
  public final void render(G3MRenderContext rc, GLState parentState)
  {
	final Camera camera = rc.getCurrentCamera();
	final Planet planet = rc.getPlanet();
  
	final Vector3D cameraPosition = camera.getCartesianPosition();
	final Vector3D markPosition = getCartesianPosition(planet);
  
	final Vector3D markCameraVector = markPosition.sub(cameraPosition);
	final double distanceToCamera = markCameraVector.length();
	//_renderedMark = distanceToCamera <= _minDistanceToCamera;
	//const bool renderMark = true;
  
	//if (_renderedMark) {
	//const Vector3D normalAtMarkPosition = planet->geodeticSurfaceNormal(*markPosition);
  
	  if (_minDistanceToCamera!=0)
	  {
		  _renderedMark = distanceToCamera <= _minDistanceToCamera;
	  }
	  else
	  {
		  final Vector3D radius = rc.getPlanet().getRadii();
		  final double minDistanceToCamera = (radius._x + radius._y + radius._z) / 3 * 0.75;
  
		  _renderedMark = distanceToCamera <= minDistanceToCamera;
	  }
	  //  const bool renderMark = true;
  
	  if (_renderedMark)
	  {
		  final Vector3D normalAtMarkPosition = planet.geodeticSurfaceNormal(markPosition);
  
		  if (normalAtMarkPosition.angleBetween(markCameraVector)._radians > IMathUtils.instance().halfPi())
		  {
			  GL gl = rc.getGL();
  
//C++ TO JAVA CONVERTER NOTE: This static local variable declaration (not allowed in Java) has been moved just prior to the method:
//			  static Vector2D textureTranslation(0.0, 0.0);
//C++ TO JAVA CONVERTER NOTE: This static local variable declaration (not allowed in Java) has been moved just prior to the method:
//			  static Vector2D textureScale(1.0, 1.0);
			  gl.transformTexCoords(render_textureScale, render_textureTranslation);
  
			  if (_textureId == null)
			  {
				  //        IImage* image = rc->getFactory()->createImageFromFileName(_textureFilename);
				  //
				  //        _textureId = rc->getTexturesHandler()->getGLTextureId(image,
				  //                                                              GLFormat::rgba(),
				  //                                                              _textureFilename,
				  //                                                              false);
				  //
				  //        rc->getFactory()->deleteImage(image);
  
				  if (_textureImage != null)
				  {
					  _textureId = rc.getTexturesHandler().getGLTextureId(_textureImage, GLFormat.rgba(), _textureURL.getPath(), false);
  
					  rc.getFactory().deleteImage(_textureImage);
					  _textureImage = null;
				  }
			  }
  
			  if (_textureId != null)
			  {
				  gl.drawBillBoard(_textureId, getVertices(planet), camera.getViewPortRatio());
			  }
		  }
	  }
	//}
  }

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: boolean isReady() const
  public final boolean isReady()
  {
	  return _textureSolved;
  }

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: boolean isRendered() const
  public final boolean isRendered()
  {
	return _renderedMark;
  }

  public final void onTextureDownloadError()
  {
	  _textureSolved = true;
  
	  ILogger.instance().logError("Can't load image \"%s\"", _textureURL.getPath());
  }

  public final void onTextureDownload(IImage image)
  {
	  _textureSolved = true;
	  _textureImage = image.shallowCopy();
	  _textureWidth = _textureImage.getWidth();
	  _textureHeight = _textureImage.getHeight();
  }

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: int getTextureWidth() const
  public final int getTextureWidth()
  {
	  //  return (_textureImage == NULL) ? 0 : _textureImage->getWidth();
	  return _textureWidth;
  }
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: int getTextureHeight() const
  public final int getTextureHeight()
  {
	  //  return (_textureImage == NULL) ? 0 : _textureImage->getHeight();
	  return _textureHeight;
  }
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: Vector2I getTextureExtent() const
  public final Vector2I getTextureExtent()
  {
	  //  return (_textureImage == NULL) ? Vector2I::zero() : _textureImage->getExtent();
	  return new Vector2I(_textureWidth, _textureHeight);
  }

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const Object* getUserData() const
  public final Object getUserData()
  {
	return _userData;
  }

  public final void setUserData(Object userData)
  {
	  _userData = userData;
  }

//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: MarkTouchListener* getListener() const
  public final MarkTouchListener getListener()
  {
	return _listener;
  }

}