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


/*
int LatLonMeshRenderer::render(const RenderContext* rc)
{  
  static GLuint id=0;
  GL *gl = rc->getGL();
  gl->enableTextures();
  gl->enableTexture2D();
  
  if (id==0) {
    glGenTextures(1, &id);
    glBindTexture(GL_TEXTURE_2D, id);
    unsigned char pixels[4][4][3] = {
      {{64,0,0},  {128,0,0},   {192,0,0},   {255,0,0}},  // Rojos 
      {{0,64,0},  {0,128,0},   {0,192,0},   {0,255,0}},  // Verdes 
      {{0,0,64},  {0,0,128},   {0,0,192},   {0,0,255}},  // Azules 
      {{64,64,0}, {128,128,0}, {192,192,0}, {255,255,0}} // Amarillos 
    };
    
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 4, 4, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);
  }
  else {
    glBindTexture(GL_TEXTURE_2D, id);
  }
  
  float texCoords[] = { 0, 0, 1, 0, 0, 1, 1, 1};
  gl->setTextureCoordinates(2, 0, texCoords);
  
  //mesh->render(rc);
  {
    gl->enableVerticesPosition();
    //gl->disableVertexColor();
    //gl->disableVertexFlatColor();
    //gl->disableVertexNormal();
    gl->vertexPointer(3, 0, vertices);
    gl->drawTriangleStrip(4, indices);
    gl->disableVerticesPosition();
  }
  
  gl->disableTextures();
  gl->disableTexture2D();
  
  return MAX_TIME_TO_RENDER;
}*/

