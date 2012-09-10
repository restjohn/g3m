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

class DirectMesh;

class CompassRenderer: public Renderer{
  
  DirectMesh* _mesh;
  
public:
  
  ~CompassRenderer();
  
  void initialize(const InitializationContext* ic);
  
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
