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
  if (_mesh != NULL){
    delete _mesh;
  }
}

void StarsRenderer::initialize(const InitializationContext* ic){
  
  int todo_set_stars_further_from_origin; 
  
  const Planet * planet = ic->getPlanet();
  _starsHeight = planet->getRadii().x() * 20.0;
  Planet starsSphere("stars", Vector3D(_starsHeight, _starsHeight, _starsHeight));
  
  FloatBufferBuilderFromGeodetic stars(NoCenter, &starsSphere, Vector3D::zero());
  
  for (int i = 0; i < _nStars; i++) {
    float lat = (float)(rand() % 36000) / 10;
    float lon = (float)(rand() % 36000) / 10;
    
    Geodetic3D g = Geodetic3D::fromDegrees(lat, lon, 0);
    stars.add(g);
  }
  
  Color* color = new Color(Color::white()); //White stars
  
  //Creating mesh
  _mesh = new DirectMesh(Points,
                        true,
                        Vector3D::zero(),
                        stars.create(),
                        color,
                        NULL,
                        0.0);
}

void StarsRenderer::render(const RenderContext* rc){
  
  int todo_change_zfar; //Stars beyond 0,0,0 are not rendered
  int change_frustum;
  
  double d = rc->getCurrentCamera()->getCartesianPosition().length();
  
  rc->getCurrentCamera()->changeProjectionToZFarValue((d + _starsHeight), rc);
  
  _mesh->render(rc);
  
  rc->getCurrentCamera()->resetProjection(rc);
  
}