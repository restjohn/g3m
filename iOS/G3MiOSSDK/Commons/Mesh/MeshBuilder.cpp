//
//  MeshBuilder.cpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 13/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#include "MeshBuilder.hpp"

#include "Vector3D.hpp"
#include "Vector2D.hpp"
#include "GLTextureId.hpp"

#include "DirectMesh.hpp"
#include "IndexedMesh.hpp"
#include "TexturedMesh.hpp"

#include "FloatBufferBuilderFromCartesian2D.hpp"
#include "FloatBufferBuilderFromCartesian3D.hpp"
#include "FloatBufferBuilderFromGeodetic.hpp"
#include "IntBufferBuilder.hpp"
#include "Color.hpp"


Mesh* MeshBuilder::createQuadXYMesh(const Vector2D& max, 
                                    const Vector2D& min, 
                                    const GLTextureId& texId){

  FloatBufferBuilderFromCartesian3D vertices(FirstVertex, Vector3D::nan());
  vertices.add(min.x(), min.y(), 0);
  vertices.add(max.x(), min.y(), 0);
  vertices.add(min.x(), max.y(), 0);
  vertices.add(max.y(), max.x(), 0);
  
  Color* flatColor = new Color(Color::white());
  
  Mesh* dMesh = new DirectMesh(TriangleStrip, true, vertices.getCenter(), vertices.create(), flatColor, NULL, 1.0);
  
  if (!texId.isValid()) {
    return dMesh;
  } else{
    FloatBufferBuilderFromCartesian2D texCoor;
    texCoor.add(1,1);
    texCoor.add(1,0);
    texCoor.add(0,1);
    texCoor.add(0,0);
    
    TextureMapping* texMap = new SimpleTextureMapping(texId,
                                                      texCoor.create(),
                                                      true);
    
    return new TexturedMesh(dMesh, true, texMap, true);
    
  }
  
}

Mesh* MeshBuilder::createEllipsoidMesh(const Vector3D& radii, int resolution, const GLTextureId& texId){
  
  Planet planet = Planet("Sphere", radii);
  
  //Vertices with Center in zero
#ifdef C_CODE
  FloatBufferBuilderFromGeodetic vertices(GivenCenter, &planet, Vector3D::zero());
#else
  FloatBufferBuilderFromGeodetic vertices(CenterStrategy.GivenCenter, planet, Vector3D::zero());
#endif
  
  const double res_1 = (double) resolution - 1;
  
  for(double i = 0.0; i < resolution; i++){
    const Angle lon = Angle::fromDegrees( (i * 360 / res_1) -180);
    for (double j = 0.0; j < resolution; j++) {
      const Angle lat = Angle::fromDegrees( (j * 180.0 / res_1)  -90.0 );
      const Geodetic2D g(lat, lon);
      
      vertices.add(g);
    }
  }
  
  IntBufferBuilder indices;
  for (int j = 0; j < resolution - 1; j++) {
    if (j > 0){
      indices.add((int) (j * resolution));
    }
    for (int i = 0; i < resolution; i++) {
      indices.add(j * resolution + i);
      indices.add(j * resolution + i + resolution);
    }
    indices.add(j * resolution + 2 * resolution - 1);
  }
  
  
  IndexedMesh* iMesh = new IndexedMesh(TriangleStrip, 
                                       true, 
                                       vertices.getCenter(), 
                                       vertices.create(), 
                                       indices.create());
  
  if (texId.isValid()){
    
    FloatBufferBuilderFromCartesian2D texCoords;
    for(double i = 0.0; i < resolution; i++){
      double u = (i / res_1);
      for (double j = 0.0; j < resolution; j++) {
        const double v = 1.0 - (j / res_1);
        texCoords.add((float)u, (float)v);
      }
    }
    
    TextureMapping* texMap = new SimpleTextureMapping(texId,
                                                      texCoords.create(),
                                                      true);
    return new TexturedMesh(iMesh, true, texMap, true);         //Textured Mesh
    
  } else {
    return iMesh;   //Returning without texture
  }
  
  
  
}
