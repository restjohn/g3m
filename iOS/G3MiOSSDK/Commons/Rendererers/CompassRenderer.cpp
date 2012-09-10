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

CompassRenderer::~CompassRenderer(){
  if (_mesh != NULL){
    delete _mesh;
  }
}

void CompassRenderer::initialize(const InitializationContext* ic){
  
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
  
  _mesh = new DirectMesh(TriangleStrip, true, vertices.getCenter(), vertices.create(), flatColor, NULL, 1.0);
  
}

void CompassRenderer::render(const RenderContext* rc){
  
  _mesh->render(rc);
  
}
