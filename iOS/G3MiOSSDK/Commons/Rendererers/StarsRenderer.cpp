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

#include "FloatBufferBuilderFromGeodetic.hpp"
#include "Color.hpp"
#include "DirectMesh.hpp"
#include "GL.hpp"
#include "Camera.hpp"


StarsRenderer::~StarsRenderer(){
  if (_mesh1 != NULL){
    delete _mesh1;
  }
  if (_mesh2 != NULL){
    delete _mesh2;
  }
  if (_mesh3 != NULL){
    delete _mesh3;
  }
  if (_mesh4 != NULL){
    delete _mesh4;
  }
}

void StarsRenderer::initialize(const InitializationContext* ic){
  
  const Planet * planet = ic->getPlanet();
  _starsHeight = planet->getRadii().x() * 20.0; //Stars at height X20
  Planet starsSphere("stars", Vector3D(_starsHeight, _starsHeight, _starsHeight));
  
  FloatBufferBuilderFromGeodetic stars1(FirstVertex, &starsSphere, Vector3D::zero());
  FloatBufferBuilderFromGeodetic stars2(FirstVertex, &starsSphere, Vector3D::zero());
  FloatBufferBuilderFromGeodetic stars3(FirstVertex, &starsSphere, Vector3D::zero());
  FloatBufferBuilderFromGeodetic stars4(FirstVertex, &starsSphere, Vector3D::zero());
  
  
  for (int i = 0; i < _nStars; i++) {
    float lat = (float)(rand() % 3600) / 10;
    float lon = (float)(rand() % 3600) / 10;
    Geodetic3D g = Geodetic3D::fromDegrees(lat, lon, 0);
    
    if (lat < 180 && lon < 180){
        stars1.add(g);
    }
    
    if (lat >= 180 && lon < 180){
      stars2.add(g);
    }
    
    if (lat < 180 && lon >= 180){
      stars3.add(g);
    }
    
    if (lat >= 180 && lon >= 180){
      stars4.add(g);
    }
  }
  
  Color* color = new Color(Color::white()); //White stars
  
  //Creating mesh
  _mesh1 = new DirectMesh(Points,
                        true,
                        stars1.getCenter(),
                        stars1.create(),
                        color,
                        NULL,
                        0.0);
  
  _mesh2 = new DirectMesh(Points,
                         true,
                         stars2.getCenter(),
                         stars2.create(),
                         color,
                         NULL,
                         0.0);
  
  _mesh3 = new DirectMesh(Points,
                         true,
                         stars3.getCenter(),
                         stars3.create(),
                         color,
                         NULL,
                         0.0);
  
  _mesh4 = new DirectMesh(Points,
                         true,
                         stars4.getCenter(),
                         stars4.create(),
                         color,
                         NULL,
                         0.0);
}

void StarsRenderer::render(const RenderContext* rc){
  
  //Distance from camera to origin
  double d = rc->getCurrentCamera()->getCartesianPosition().length();
  
  rc->getCurrentCamera()->changeProjectionToZFarValue(d + _starsHeight, rc);
  
  const Frustum* f = rc->getCurrentCamera()->getFrustumInModelCoordinates();
  
  Box* box1 = (Box*) _mesh1->getExtent();
  if (box1 != NULL && f->touchesWithBox(box1)){
    _mesh1->render(rc);
  }
  
  Box* box2 = (Box*) _mesh2->getExtent();
  if (box2 != NULL && f->touchesWithBox(box2)){
    _mesh2->render(rc);
  }
  
  Box* box3 = (Box*) _mesh3->getExtent();
  if (box3 != NULL && f->touchesWithBox(box3)){
    _mesh3->render(rc);
  }
  
  Box* box4 = (Box*) _mesh4->getExtent();
  if (box4 != NULL && f->touchesWithBox(box4)){
    _mesh4->render(rc);
  }
  
  rc->getCurrentCamera()->resetProjection(rc);
  
}