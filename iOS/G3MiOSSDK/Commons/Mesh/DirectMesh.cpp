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
  
  switch (_primitive) {
    case TriangleStrip:
      gl->drawTriangleStrip(_numIndex, _indexes);
      break;
    case Lines:
      gl->drawLines(_numIndex, _indexes);
      break;
    case LineLoop:
      gl->drawLineLoop(_numIndex, _indexes);
      break;
    default:
      ILogger::instance()->logError("Calling DirectMesh Render with invalid GLPrimitive");
      break;
  }
  
  postRender(gl); //Calling AbstractRender
  
}