//
//  AbstractMesh.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 04/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_AbstractMesh_hpp
#define G3MiOSSDK_AbstractMesh_hpp

#include "Mesh.hpp"
#include "INativeGL.hpp"
#include "Vector3D.hpp"

class Color;
class Extent;

enum CenterStrategy {
  NoCenter,
  AveragedVertex,
  FirstVertex,
  GivenCenter
};

class AbstractMesh: public Mesh{
protected:
  
  Extent* computeExtent() const;
  
  //Sets vertices, colors and normal and push translation matrix before rendering
  void preRender(GL* gl) const; 
  //Undo preRender
  void postRender(GL* gl) const;
  
#ifdef C_CODE
  const float*         _vertices;
  const float*         _normals;
  const float *        _colors;
  const GLPrimitive    _primitive; 
#endif
  
#ifdef JAVA_CODE
  private final float[]         _vertices;
  private final float[]         _normals;
  private final float[]         _colors;
  private final GLPrimitive     _primitive; 
#endif
  
  const bool           _owner;
  const int            _numVertices;
  const Color *        _flatColor;
  const float          _colorsIntensity;
  mutable Extent *     _extent;
  
  CenterStrategy       _centerStrategy;
  Vector3D             _center;
  
public:
  
  ~AbstractMesh();
  
  AbstractMesh(std::vector<MutableVector3D>& vertices,
               const GLPrimitive primitive,
               CenterStrategy strategy,
               Vector3D center,
               const Color* flatColor = NULL,
               std::vector<Color>* colors = NULL,
               const float colorsIntensity = (float)0.0,
               std::vector<MutableVector3D>* normals = NULL);
  
  AbstractMesh(bool owner,
               const GLPrimitive primitive,
               CenterStrategy strategy,
               Vector3D center,
               const int numVertices,
               const float vertices[],
               const Color* flatColor = NULL,
               const float colors[] = NULL,
               const float colorsIntensity = (float)0.0,
               const float normals[] = NULL);
  
  Extent* getExtent() const {
    if (_extent == NULL) {
      _extent = computeExtent(); 
    }
    return _extent;
  }
  
  int getVertexCount() const {
    return _numVertices;
  }
  
  const Vector3D getVertex(int i) const {
    const int p = i * 3;
    return Vector3D(_vertices[p  ] + _center.x(),
                    _vertices[p+1] + _center.y(),
                    _vertices[p+2] + _center.z());
  }
  

};

#endif
