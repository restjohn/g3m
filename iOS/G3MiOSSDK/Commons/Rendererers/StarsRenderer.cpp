//
//  StarsRenderer.cpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 06/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#include "StarsRenderer.hpp"

#include "Context.hpp"
#include "Geodetic3D.hpp"
#include "Planet.hpp"
#include "DirectMesh.hpp"
#include "MutableVector3D.hpp"

#include <cstdlib>
#include <vector>
#include <cstring>


StarsRenderer::~StarsRenderer(){
  if (_mesh != NULL){
    delete _mesh;
  }
}

void StarsRenderer::initialize(const InitializationContext* ic){
  
  const Planet * planet = ic->getPlanet();
  
  double starsHeight = planet->getRadii().x() * 2;
  
  std::vector<MutableVector3D> stars;
  
  for (int i = 0; i < _nStars; i++) {
    float lat = (float)(rand() % 36000) / 10;
    float lon = (float)(rand() % 36000) / 10;
    
    Geodetic3D g = Geodetic3D::fromDegrees(lat, lon, starsHeight);
    Vector3D pos = planet->toCartesian(g);
    
    stars.push_back(pos.asMutableVector3D());
  }
  
  int todo_create_mesh;
  //_mesh = DirectMesh::createFromVector3D(true, Points, NoCenter, Vector3D(0,0,0), stars);
}

void render(const RenderContext* rc){
  
}