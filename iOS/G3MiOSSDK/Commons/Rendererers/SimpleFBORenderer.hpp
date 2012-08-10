//
//  SimpleFBORenderer.hpp
//  G3MiOSSDK
//
//  Created by Agustín Trujillo Pino on 10/08/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//

#ifndef G3MiOSSDK_SimpleFBORenderer_hpp
#define G3MiOSSDK_SimpleFBORenderer_hpp

#include "Renderer.hpp"
#include "IndexedMesh.hpp"


class SimpleFBORenderer: public Renderer {
  
private:
  Mesh *mesh;
  
  
public:
  ~SimpleFBORenderer();
  
  void initialize(const InitializationContext* ic);  
  
  int render(const RenderContext* rc);
  
  bool onTouchEvent(const EventContext* ec,
                    const TouchEvent* touchEvent) {
    return false;
  };
  
  void onResizeViewportEvent(const EventContext* ec,
                             int width, int height) {}
  
  bool isReadyToRender(const RenderContext* rc) {
    return true;
  }
  
};


#endif
