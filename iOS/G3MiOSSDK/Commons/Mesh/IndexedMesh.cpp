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


IndexedMesh::~IndexedMesh()
{
#ifdef C_CODE
  
  if (_owner){
    delete[] _vertices;
    delete[] _indexes;
    if (_normals != NULL) delete[] _normals;
    if (_colors != NULL) delete[] _colors;
    if (_flatColor != NULL) delete _flatColor;
  }
  
  if (_extent != NULL) delete _extent;
  
#endif
}

IndexedMesh::IndexedMesh(bool owner,
                         const GLPrimitive primitive,
                         CenterStrategy strategy,
                         Vector3D center,
                         const int numVertices,
                         const float vertices[],
                         const int indexes[],
                         const int numIndex, 
                         const Color* flatColor,
                         const float colors[],
                         const float colorsIntensity,
                         const float normals[]):
AbstractMesh(owner,
             primitive,
             strategy,
             center,
             numVertices,
             vertices, 
             flatColor,
             colors,
             colorsIntensity,
             normals),
_indexes(indexes),
_numIndex(numIndex)
{
  if (strategy!=NoCenter) 
    printf ("IndexedMesh array constructor: this center Strategy is not yet implemented\n");
}


IndexedMesh::IndexedMesh(std::vector<MutableVector3D>& vertices, 
                         const GLPrimitive primitive,
                         CenterStrategy strategy,
                         Vector3D center,                         
                         std::vector<int>& indexes,
                         const Color* flatColor,
                         std::vector<Color>* colors,
                         const float colorsIntensity,
                         std::vector<MutableVector3D>* normals):
AbstractMesh(vertices, 
             primitive,
             strategy,
             center,                         
             flatColor,
             colors,
             colorsIntensity,
             normals),
_numIndex(indexes.size())
{
  int * ind = new int[indexes.size()];
  for (int i = 0; i < indexes.size(); i++) {
    ind[i] = indexes[i];
  }
  _indexes = ind;
}

void IndexedMesh::render(const RenderContext* rc) const {
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
      break;
  }
  
  postRender(gl); //Calling AbstractRender

}