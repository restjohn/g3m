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
#include "GL.hpp"
#include "Camera.hpp"

#include "MeshBuilder.hpp"

CompassRenderer::~CompassRenderer(){
  if (_mesh != NULL){
    delete _mesh;
  }
}

Mesh* CompassRenderer::createMesh(const RenderContext* rc){

  GLTextureId texId = rc->getTexturesHandler()->getGLTextureIdFromFileName(_textureName, 
                                                                           _texWidth, _texHeight, 
                                                                           true);
  const double halfSize = 70;
  return MeshBuilder::createQuadXYMesh(Vector2D(halfSize, halfSize), 
                                       Vector2D(-halfSize, -halfSize), texId);

}

void CompassRenderer::render(const RenderContext* rc){
  
  if (_mesh == NULL){
    _mesh = createMesh(rc);
  }

  GL* gl = rc->getGL();
  
  // init modelview matrix
  int currentViewport[4];
  gl->getViewport(currentViewport);
  int halfWidth = currentViewport[2] / 2;
  int halfHeight = currentViewport[3] / 2;
  MutableMatrix44D M = MutableMatrix44D::createOrthographicProjectionMatrix(-halfWidth, halfWidth,
                                                                            -halfHeight, halfHeight,
                                                                            -halfWidth, halfWidth);
  gl->setProjection(M);
  gl->loadMatrixf(MutableMatrix44D::identity());
  
  //Bottom right corner
  Vector3D trans(halfWidth * 0.75, halfHeight * 0.75, 0);
  MutableMatrix44D T = MutableMatrix44D::createTranslationMatrix(trans);
  
  //Compass orientation
  //Angle heading = rc->getCurrentCamera()->calculateHeading();
  Angle heading = Angle::fromDegrees(20);
  
  //Pitch
  //Angle pitch = rc->getCurrentCamera()->calculatePitch();
  Angle pitch = Angle::fromDegrees(50);
  
  MutableMatrix44D R = MutableMatrix44D::createRotationMatrix(heading, Vector3D(0,0,1));
  
  MutableMatrix44D R2 = MutableMatrix44D::createRotationMatrix(pitch, Vector3D(1,0,0));
  
  gl->multMatrixf(T.multiply(R).multiply(R2));
  
  gl->disableDepthTest();
  gl->enableBlend();
  gl->setBlendFuncSrcAlpha();
  
  gl->pushMatrix();
  // draw mesh
  _mesh->render(rc);
  
  gl->popMatrix();
  
  gl->disableBlend();
  gl->enableDepthTest();
}
