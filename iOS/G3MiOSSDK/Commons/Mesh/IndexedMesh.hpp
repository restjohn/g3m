//
//  IndexedMesh.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 22/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_IndexedMesh_h
#define G3MiOSSDK_IndexedMesh_h

#include "Mesh.hpp"
#include "Color.hpp"
#include "MutableVector2D.hpp"
#include "MutableVector3D.hpp"
#include "Geodetic3D.hpp"
#include "Planet.hpp"

#include <vector>

#include "INativeGL.hpp"

#include "AbstractMesh.hpp"

//enum CenterStrategy {
//  NoCenter,
//  AveragedVertex,
//  FirstVertex,
//  GivenCenter
//};


class IndexedMesh : public AbstractMesh {
private:
  IndexedMesh(std::vector<MutableVector3D>& vertices,
              const GLPrimitive primitive,
              CenterStrategy strategy,
              Vector3D center,
              std::vector<int>& indexes,
              const Color* flatColor = NULL,
              std::vector<Color>* colors = NULL,
              const float colorsIntensity = (float)0.0,
              std::vector<MutableVector3D>* normals = NULL);
  
  IndexedMesh(bool owner,
              const GLPrimitive primitive,
              CenterStrategy strategy,
              Vector3D center,
              const int numVertices,
              const float vertices[],
              const int indexes[],
              const int numIndex, 
              const Color* flatColor = NULL,
              const float colors[] = NULL,
              const float colorsIntensity = (float)0.0,
              const float normals[] = NULL);
  
#ifdef C_CODE
  const int*           _indexes; 
#endif
  
#ifdef JAVA_CODE
  private final int[]           _indexes; 
#endif

  const int            _numIndex;
  
public:
  
  ~IndexedMesh();

    
  static IndexedMesh* createFromVector3D(bool owner,
                                         const GLPrimitive primitive,
                                         CenterStrategy strategy,
                                         Vector3D center,
                                         const int numVertices,
                                         const float vertices[],
                                         const int indexes[],
                                         const int numIndex, 
                                         const Color* flatColor = NULL,
                                         const float colors[] = NULL,
                                         const float colorsIntensity = (float)0.0,
                                         const float normals[] = NULL) {
    return new IndexedMesh(owner, primitive, strategy, center, numVertices, vertices,
                           indexes, numIndex, flatColor, colors, colorsIntensity, normals);
  }

    
  static IndexedMesh* createFromVector3D(std::vector<MutableVector3D>& vertices,
                                         const GLPrimitive primitive,
                                         CenterStrategy strategy,
                                         Vector3D center,
                                         std::vector<int>& indexes,
                                         const Color* flatColor = NULL,
                                         std::vector<Color>* colors = NULL,
                                         const float colorsIntensity = (float)0.0,
                                         std::vector<MutableVector3D>* normals = NULL) {
    return new IndexedMesh(vertices, primitive, strategy, center, indexes,
                           flatColor, colors, colorsIntensity, normals);
  }

  
  static IndexedMesh* createFromGeodetic3D(const Planet *planet,
                                           bool owner,
                                           const GLPrimitive primitive,
                                           CenterStrategy strategy,
                                           Vector3D center,
                                           const int numVertices,
                                           float vertices[],
                                           const int indexes[],
                                           const int numIndex, 
                                           const Color* flatColor = NULL,
                                           const float colors[] = NULL,
                                           const float colorsIntensity = (float)0.0,
                                           const float normals[] = NULL) {
    // convert vertices to latlon coordinates
    for (unsigned int n=0; n<numVertices*3; n+=3) {
      const Geodetic3D g(Angle::fromDegrees(vertices[n]), Angle::fromDegrees(vertices[n+1]), vertices[n+2]);
      const Vector3D v = planet->toCartesian(g);
      vertices[n]   = (float) v.x();
      vertices[n+1] = (float) v.y();
      vertices[n+2] = (float) v.z();
    }
    
    // create indexed mesh
    return new IndexedMesh(owner, primitive, strategy, center, numVertices, vertices,
                           indexes, numIndex, flatColor, colors, colorsIntensity, normals);
  }

    
  virtual void render(const RenderContext* rc) const;
};

#endif
