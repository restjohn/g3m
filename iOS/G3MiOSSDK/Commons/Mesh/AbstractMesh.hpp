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

class AbstractMesh: public Mesh{
protected:
  
  const bool        _owner;
  GLPrimitive       _primitive;
  Vector3D          _center;
  const MutableMatrix44D* _translationMatrix;
  IFloatBuffer*     _vertices;
  const Color*      _flatColor;
  IFloatBuffer*     _colors;
  const float       _colorsIntensity;
  mutable Extent*   _extent;
  
  Extent* computeExtent() const;
  
  //Sets vertices, colors and normal and push translation matrix before rendering
  void preRender(GL* gl) const; 
  //Undo preRender
  void postRender(GL* gl) const;
  
public:
  
  ~AbstractMesh();
  
  AbstractMesh(const GLPrimitive primitive,
               bool owner,
               const Vector3D& center,
               IFloatBuffer* vertices,
               const Color* flatColor,
               IFloatBuffer* colors,
               const float colorsIntensity);
  
  Extent* getExtent() const {
    if (_extent == NULL) {
      _extent = computeExtent(); 
    }
    return _extent;
  }
  
  int getVertexCount() const;
  
  const Vector3D getVertex(int i) const;
  
  
};

#endif
