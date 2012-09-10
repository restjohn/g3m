//
//  IndexedMesh.cpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 22/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#include <stdlib.h>

#include "IndexedMesh.hpp"
#include "Box.hpp"
#include "GL.hpp"
#include "INativeGL.hpp"
#include "IFloatBuffer.hpp"
#include "IIntBuffer.hpp"
#include "Color.hpp"
#include "Vector3D.hpp"

IndexedMesh::~IndexedMesh()
{
#ifdef C_CODE
  if (_owner){
    delete _indexes;
  }
#endif
}

IndexedMesh::IndexedMesh(const GLPrimitive primitive,
                         bool owner,
                         const Vector3D& center,
                         IFloatBuffer* vertices,
                         IIntBuffer* indices,
                         const Color* flatColor,
                         IFloatBuffer* colors,
                         const float colorsIntensity) :
AbstractMesh(primitive,
             owner,
             center,
             vertices,
             flatColor,
             colors,
             colorsIntensity),
_indexes(indices)
{
}

void IndexedMesh::render(const RenderContext* rc) const {
  GL *gl = rc->getGL();
  
  preRender(gl); //Calling AbstractRender
  
  switch (_primitive) {
    case TriangleStrip:
      gl->drawTriangleStrip(_indexes);
      break;
    case Lines:
      gl->drawLines(_indexes);
      break;
    case LineLoop:
      gl->drawLineLoop(_indexes);
      break;
    case Points:
      gl->drawPoints(_indexes);
      break;
    default:
      ILogger::instance()->logError("Calling IndexedMesh Render with invalid GLPrimitive");
      break;
  }
  
  postRender(gl); //Calling AbstractRender
}
