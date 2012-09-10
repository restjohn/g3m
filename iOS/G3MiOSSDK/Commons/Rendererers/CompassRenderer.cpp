//
//  CompassRenderer.cpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 10/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#include "CompassRenderer.hpp"

#include "DirectMesh.hpp"
#include "Color.hpp"

#include "FloatBufferBuilderFromCartesian2D.hpp"
#include "FloatBufferBuilderFromCartesian3D.hpp"
#include "TexturesHandler.hpp"
#include "TextureMapping.hpp"
#include "TexturedMesh.hpp"

CompassRenderer::~CompassRenderer(){
  if (_mesh != NULL){
    delete _mesh;
  }
}

Mesh* CompassRenderer::createMesh(const RenderContext* rc){
  
  FloatBufferBuilderFromCartesian2D texCoor;
  texCoor.add(1,1);
  texCoor.add(1,0);
  texCoor.add(0,1);
  texCoor.add(0,0);
  
  const double halfSize = 10;
  
  FloatBufferBuilderFromCartesian3D vertices(FirstVertex, Vector3D::nan());
  vertices.add(-halfSize, -halfSize, 0);
  vertices.add(halfSize, -halfSize, 0);
  vertices.add(-halfSize, halfSize, 0);
  vertices.add(halfSize, halfSize, 0);
  
  Color* flatColor = new Color(Color::white());
  
  Mesh* dMesh = new DirectMesh(TriangleStrip, true, vertices.getCenter(), vertices.create(), flatColor, NULL, 1.0);

  GLTextureId texId = GLTextureId::invalid();
  if (true){
    texId = rc->getTexturesHandler()->getGLTextureIdFromFileName(_textureName, _texWidth, _texHeight, true);
    if (!texId.isValid()) {
      rc->getLogger()->logError("Can't load file %s", _textureName.c_str());
      
      //If there's no texture a DirectMesh will be renderized
      return dMesh;
    }
  }
  
  TextureMapping* texMap = new SimpleTextureMapping(texId,
                                                    texCoor.create(),
                                                    true);
  
  return new TexturedMesh(dMesh, true, texMap, true);
  
}

void CompassRenderer::render(const RenderContext* rc){
  
  if (_mesh == NULL){
    _mesh = createMesh(rc);
  }
  
  _mesh->render(rc);
  
}
