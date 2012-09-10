//
//  AbstractMesh.cpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 04/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#include "AbstractMesh.hpp"

#include "MutableVector3D.hpp"
#include "Color.hpp"
#include "Box.hpp"
#include "GL.hpp"

#include "IFloatBuffer.hpp"


AbstractMesh::AbstractMesh(const GLPrimitive primitive,
                           bool owner,
                           const Vector3D& center,
                           IFloatBuffer* vertices,
                           const Color* flatColor,
                           IFloatBuffer* colors,
                           const float colorsIntensity) :
_primitive(primitive),
_owner(owner),
_vertices(vertices),
_flatColor(flatColor),
_colors(colors),
_colorsIntensity(colorsIntensity),
_extent(NULL),
_center(center),
_translationMatrix(center.isNan()? NULL:
                   new MutableMatrix44D(MutableMatrix44D::createTranslationMatrix(center)))
{
}

int AbstractMesh::getVertexCount() const {
  return _vertices->size() / 3;
}

const Vector3D AbstractMesh::getVertex(int i) const {
  const int p = i * 3;
  return Vector3D(_vertices->get(p) + _center.x(),
                  _vertices->get(p+1) + _center.y(),
                  _vertices->get(p+2) + _center.z());
}

Extent* AbstractMesh::computeExtent() const {
  const int nVertices = getVertexCount();
  
  if (nVertices <= 0) {
    return NULL;
  }
  
  double minx=1e10, miny=1e10, minz=1e10;
  double maxx=-1e10, maxy=-1e10, maxz=-1e10;
  
  for (int i=0; i < nVertices; i++) {
    Vector3D v = getVertex(i);
    const double x = v.x();
    const double y = v.y();
    const double z = v.z();
    
    
    if (x < minx) minx = x;
    if (x > maxx) maxx = x;
    
    if (y < miny) miny = y;
    if (y > maxy) maxy = y;
    
    if (z < minz) minz = z;
    if (z > maxz) maxz = z;
  }
  
  return new Box(Vector3D(minx, miny, minz), Vector3D(maxx, maxy, maxz));
}

void AbstractMesh::preRender(GL* gl) const {
  gl->enableVerticesPosition();
  
  if (_colors == NULL) {
    gl->disableVertexColor();
  }
  else {
    gl->enableVertexColor(_colors, _colorsIntensity);
  }
  
  if (_flatColor == NULL) {
    gl->disableVertexFlatColor();
  }
  else {
    gl->enableVertexFlatColor(*_flatColor, _colorsIntensity);
  }
  
  gl->vertexPointer(3, 0, _vertices);
  
  if (_translationMatrix != NULL) {
    gl->pushMatrix();
    gl->multMatrixf(MutableMatrix44D::createTranslationMatrix(_center));
  }
}

void AbstractMesh::postRender(GL* gl) const {
  
  if (_translationMatrix != NULL) {
    gl->popMatrix();
  }
  
  gl->disableVerticesPosition();
  
}

AbstractMesh::~AbstractMesh()
{
#ifdef C_CODE
  
  if (_owner){
    delete _vertices;
    if (_colors != NULL) delete _colors;
    if (_flatColor != NULL) delete _flatColor;
  }
  
  if (_extent != NULL) delete _extent;
  if (_translationMatrix != NULL) delete _translationMatrix;
  
#endif
}
