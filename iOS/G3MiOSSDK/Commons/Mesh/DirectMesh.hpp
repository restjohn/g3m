//
//  DirectMesh.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 06/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_DirectMesh_hpp
#define G3MiOSSDK_DirectMesh_hpp

#include "AbstractMesh.hpp"

#include "Planet.hpp"

class DirectMesh: public AbstractMesh{

  
  
  DirectMesh(bool owner,
                           const GLPrimitive primitive,
                           CenterStrategy strategy,
                           Vector3D center,
                           const int numVertices,
                           const float vertices[], 
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
               normals)
  {
    
  }
  
  DirectMesh(std::vector<MutableVector3D>& vertices,
               const GLPrimitive primitive,
               CenterStrategy strategy,
               Vector3D center,
               const Color* flatColor = NULL,
               std::vector<Color>* colors = NULL,
               const float colorsIntensity = (float)0.0,
             std::vector<MutableVector3D>* normals = NULL):
  AbstractMesh(vertices, 
               primitive,
               strategy,
               center,                         
               flatColor,
               colors,
               colorsIntensity,
               normals)
  {
    
    
  }
  
  
public:
  
  static DirectMesh* createFromVector3D(bool owner,
                                         const GLPrimitive primitive,
                                         CenterStrategy strategy,
                                         Vector3D center,
                                         const int numVertices,
                                         const float vertices[],
                                         const Color* flatColor = NULL,
                                         const float colors[] = NULL,
                                         const float colorsIntensity = (float)0.0,
                                         const float normals[] = NULL) {
    return new DirectMesh(owner, primitive, strategy, center, numVertices, vertices,
                          flatColor, colors, colorsIntensity, normals);
  }
  
  
  static DirectMesh* createFromVector3D(std::vector<MutableVector3D>& vertices,
                                         const GLPrimitive primitive,
                                         CenterStrategy strategy,
                                         Vector3D center,
                                         const Color* flatColor = NULL,
                                         std::vector<Color>* colors = NULL,
                                         const float colorsIntensity = (float)0.0,
                                         std::vector<MutableVector3D>* normals = NULL) {
    return new DirectMesh(vertices, primitive, strategy, center,
                           flatColor, colors, colorsIntensity, normals);
  }
  
  
  static DirectMesh* createFromGeodetic3D(const Planet *planet,
                                           bool owner,
                                           const GLPrimitive primitive,
                                           CenterStrategy strategy,
                                           Vector3D center,
                                           const int numVertices,
                                           float vertices[],
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
    return new DirectMesh(owner, primitive, strategy, center, numVertices, vertices,
                          flatColor, colors, colorsIntensity, normals);
  }
  
  void render(const RenderContext* rc) const;
};


#endif
