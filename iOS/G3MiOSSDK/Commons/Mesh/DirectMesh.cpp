//
//  DirectMesh.cpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 06/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#include "DirectMesh.hpp"

void DirectMesh::render(const RenderContext* rc) const {
  GL *gl = rc->getGL();
  
  preRender(gl); //Calling AbstractRender
  
  int todo_draw_array;
  
  postRender(gl); //Calling AbstractRender
  
}