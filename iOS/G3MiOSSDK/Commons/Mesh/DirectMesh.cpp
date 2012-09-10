//
//  DirectMesh.cpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 06/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#include "DirectMesh.hpp"
#include "GL.hpp"
#include "IFloatBuffer.hpp"

DirectMesh::DirectMesh(const GLPrimitive primitive,
                       bool owner,
                       const Vector3D& center,
                       IFloatBuffer* vertices,
                       const Color* flatColor,
                       IFloatBuffer* colors,
                       const float colorsIntensity):
AbstractMesh(primitive,
             owner,
             center,
             vertices, 
             flatColor,
             colors,
             colorsIntensity)
{
  
}

void DirectMesh::render(const RenderContext* rc) const {
  GL *gl = rc->getGL();
  
  preRender(gl); //Calling AbstractRender
  
  int nVertices = getVertexCount();
  gl->drawArrays(_primitive, 0, nVertices);
  
  postRender(gl); //Calling AbstractRender
  
}