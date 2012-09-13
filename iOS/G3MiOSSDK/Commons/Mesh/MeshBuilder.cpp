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
#include "TexturedMesh.hpp"

#include "FloatBufferBuilderFromCartesian2D.hpp"
#include "FloatBufferBuilderFromCartesian3D.hpp"
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
