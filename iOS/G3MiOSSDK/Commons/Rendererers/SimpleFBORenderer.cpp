//
//  SimpleFBORenderer.cpp
//  G3MiOSSDK
//
//  Created by Agustín Trujillo Pino on 10/08/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//

#include <iostream>

#include "SimpleFBORenderer.hpp"


SimpleFBORenderer::~SimpleFBORenderer()
{
  delete mesh;
}


void SimpleFBORenderer::initialize(const InitializationContext* ic)
{
  unsigned int numVertices = 4;
  int numIndices = 4;
  
  float v[] = {
    (float) 30, (float) -19, 5000,
    (float) 26, (float) -19, 5000,
    (float) 30, (float) -16,  5000,
    (float) 26, (float) -16,  5000
  };
  
  int i[] = { 0, 1, 2, 3};
  
  // create vertices and indices in dinamic memory
  float *vertices = new float [numVertices*3];
  memcpy(vertices, v, numVertices*3*sizeof(float));
  int *indices = new int [numIndices];
  memcpy(indices, i, numIndices*sizeof(unsigned int));
  
  // create mesh
  Color *flatColor = new Color(Color::fromRGBA(1.0, 1.0, 0.0, 1.0));
  mesh = IndexedMesh::CreateFromGeodetic3D(ic->getPlanet(), true, TriangleStrip, NoCenter, Vector3D(0,0,0), 
                                           4, vertices, indices, 4, flatColor);
}  


int SimpleFBORenderer::render(const RenderContext* rc)
{  
  mesh->render(rc);
  
  return MAX_TIME_TO_RENDER;
}
