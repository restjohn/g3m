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


AbstractMesh::AbstractMesh(bool owner,
                         const GLPrimitive primitive,
                         CenterStrategy strategy,
                         Vector3D center,
                         const int numVertices,
                         const float vertices[],
                         const Color* flatColor,
                         const float colors[],
                         const float colorsIntensity,
                         const float normals[]):
_owner(owner),
_primitive(primitive),
_numVertices(numVertices),
_vertices(vertices),
_flatColor(flatColor),
_colors(colors),
_colorsIntensity(colorsIntensity), 
_normals(normals),
_extent(NULL),
_centerStrategy(strategy),
_center(center)
{
  if (strategy!=NoCenter) 
    printf ("IndexedMesh array constructor: this center Strategy is not yet implemented\n");
}


AbstractMesh::AbstractMesh(std::vector<MutableVector3D>& vertices, 
                         const GLPrimitive primitive,
                         CenterStrategy strategy,
                         Vector3D center,           
                         const Color* flatColor,
                         std::vector<Color>* colors,
                         const float colorsIntensity,
                         std::vector<MutableVector3D>* normals):
_owner(true),
_primitive(primitive),
_numVertices(vertices.size()),
_flatColor(flatColor),
_colorsIntensity(colorsIntensity),
_extent(NULL),
_centerStrategy(strategy),
_center(center)
{
  float * vert = new float[3* vertices.size()];
  int p = 0;
  
  switch (strategy) {
    case NoCenter:
      for (int i = 0; i < vertices.size(); i++) {
        vert[p++] = (float) vertices[i].x();
        vert[p++] = (float) vertices[i].y();
        vert[p++] = (float) vertices[i].z();
      }      
      break;
      
    case GivenCenter:
      for (int i = 0; i < vertices.size(); i++) {
        vert[p++] = (float) (vertices[i].x() - center.x());
        vert[p++] = (float) (vertices[i].y() - center.y());
        vert[p++] = (float) (vertices[i].z() - center.z());
      }      
      break;
      
    default:
      printf ("IndexedMesh vector constructor: this center Strategy is not yet implemented\n");
  }
  
  _vertices = vert;
  
  if (normals != NULL) {
    float * norm = new float[3 * vertices.size()];
    p = 0;
    for (int i = 0; i < vertices.size(); i++) {
      norm[p++] = (float) normals->at(i).x();
      norm[p++] = (float) normals->at(i).y();
      norm[p++] = (float) normals->at(i).z();
    }
    _normals = norm;
  }
  else {
    _normals = NULL;
  }
  
  if (colors != NULL) {
    float * vertexColor = new float[4 * colors->size()];
    for (int i = 0; i < colors->size(); i+=4){
      vertexColor[i] = colors->at(i).getRed();
      vertexColor[i+1] = colors->at(i).getGreen();
      vertexColor[i+2] = colors->at(i).getBlue();
      vertexColor[i+3] = colors->at(i).getAlpha();
    }
    _colors = vertexColor;
  }
  else {
    _colors = NULL; 
  }
}

Extent* AbstractMesh::computeExtent() const {
  if (_numVertices <= 0) {
    return NULL;
  }
  
  double minx=1e10, miny=1e10, minz=1e10;
  double maxx=-1e10, maxy=-1e10, maxz=-1e10;
  
  for (int i=0; i < _numVertices; i++) {
    const int p = i * 3;
    
    const double x = _vertices[p  ] + _center.x();
    const double y = _vertices[p+1] + _center.y();
    const double z = _vertices[p+2] + _center.z();
    
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
  
  if (_normals == NULL) {
    gl->disableVertexNormal();
  }
  else {
    gl->enableVertexNormal(_normals);
  }
  
  gl->vertexPointer(3, 0, _vertices);
  
  if (_centerStrategy != NoCenter) {
    gl->pushMatrix();
    gl->multMatrixf(MutableMatrix44D::createTranslationMatrix(_center));
  }
}

void AbstractMesh::postRender(GL* gl) const {
  
  if (_centerStrategy != NoCenter) {
    gl->popMatrix();
  }
  
  gl->disableVerticesPosition();

}

AbstractMesh::~AbstractMesh()
{
#ifdef C_CODE
  
  if (_owner){
    delete[] _vertices;
    if (_normals != NULL) delete[] _normals;
    if (_colors != NULL) delete[] _colors;
    if (_flatColor != NULL) delete _flatColor;
  }
  
  if (_extent != NULL) delete _extent;
  
#endif
}
