//
//  CompassRenderer.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 10/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_CompassRenderer_hpp
#define G3MiOSSDK_CompassRenderer_hpp

#include "Renderer.hpp"
#include <string>

class Mesh;

class CompassRenderer: public Renderer{
  
  Mesh* _mesh;
  const std::string _textureName;
  const int _texWidth;
  const int _texHeight;
  
  Mesh* createMesh(const RenderContext* rc);
  
public:
  
  CompassRenderer(const std::string& texName, int width, int height):
  _textureName(texName),
  _texWidth(width),
  _texHeight(height),
  _mesh(NULL){
  }
  
  ~CompassRenderer();
  
  void initialize(const InitializationContext* ic){}
  
  bool isReadyToRender(const RenderContext* rc){
    return true;
  }
  
  void render(const RenderContext* rc);
  
  bool onTouchEvent(const EventContext* ec, const TouchEvent* touchEvent){ 
    return false;
  };
  
  void onResizeViewportEvent(const EventContext* ec, int width, int height){ };
  
  void start(){ };
  
  void stop(){ };
  
  
};

#endif
