//
//  SimpleFBORenderer.cpp
//  G3MiOSSDK
//
//  Created by Agustín Trujillo Pino on 10/08/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//

#include <iostream>

#include <OpenGLES/ES2/gl.h>
#include <OpenGLES/ES2/glext.h>

#include "SimpleFBORenderer.hpp"
#include "GL.hpp"
#include "Context.hpp"
#include "Camera.hpp"


SimpleFBORenderer::~SimpleFBORenderer()
{
  delete _vertices;
  delete _indices;
  delete _texCoords;
}


void SimpleFBORenderer::initialize(const InitializationContext* ic)
{
  unsigned int numVertices = 8;
  int numIndices = 4;
  float x=7e6, y=1e6;
  
  float v[] = {
    x, -y, y,
    x, -y, -y,
    x, y, y,
    x, y, -y
  };
  
  int i[] = { 0, 1, 2, 3};
  
  float texCoords[] = {0, 0, 0, 1, 1, 0, 1, 1};
  
  unsigned char pixels[4][4][3] = {
    {{64,0,0},  {128,0,0},   {192,0,0},   {255,0,0}},  // Rojos 
    {{0,64,0},  {0,128,0},   {0,192,0},   {0,255,0}},  // Verdes 
    {{0,0,64},  {0,0,128},   {0,0,192},   {0,0,255}},  // Azules 
    {{64,64,0}, {128,128,0}, {192,192,0}, {255,255,0}} // Amarillos 
  };
  
  // create vertices and indices in dinamic memory
  _vertices = new float [numVertices*3];
  memcpy(_vertices, v, numVertices*3*sizeof(float));
  _indices = new int [numIndices];
  memcpy(_indices, i, numIndices*sizeof(unsigned int));
  _texCoords = new float [numVertices*2];
  memcpy(_texCoords, texCoords, numVertices*2*sizeof(float));
  
  // create texture
  glGenTextures(1, &_idTexture);
  glBindTexture(GL_TEXTURE_2D, _idTexture);  
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 4, 4, 0, GL_RGB, GL_UNSIGNED_BYTE, pixels);
  
  // create textures for FBO
  glGenTextures(2, _fboTexture);
}  

unsigned int fboHandle; 
unsigned int fboTex;
int defaultFramebuffer;



void SimpleFBORenderer::renderFBO(const RenderContext* rc, int numTexture)
{
  // create buffer for render to texture
  GLuint fbo_width = 256;
  GLuint fbo_height = 256;
  glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
  glBindTexture(GL_TEXTURE_2D, _fboTexture[numTexture]);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,fbo_width, fbo_height,
               0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, NULL);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER_APPLE, GL_COLOR_ATTACHMENT0, 
                         GL_TEXTURE_2D, _fboTexture[numTexture], 0);

  
  //glEnable(GL_TEXTURE_2D);
  glBindTexture(GL_TEXTURE_2D, 0);

  glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
  glDisable(GL_DEPTH_TEST);
  
  // save current viewport
  GLint currentViewport[4];
  glGetIntegerv(GL_VIEWPORT, currentViewport);
  
  glViewport(0,0, 256, 256);
    
  float v1[] = {
    0,    256,
    0,    0, 
    256,  256,
    256,  0
  };
    
  float v2[] = {
    80, 200,
    80, 100,
    200, 200,
    200, 100
  };
  
  int i[] = { 0, 1, 2, 3};
  
  unsigned char pixels1[] = {
    128,  128,  128,  255,
    255,  0,    0,    255,
    0,    255,  0,    255,
    0,    0,    255,  255
  };
  
  unsigned char pixels2[] = {
    255,  255,  0,    128,
    0,    255,  255,  128,
    255,  0,    255,  128,
    0,    0,    0,    128
  };
  
  float texCoords[] = {0, 1, 0, 0, 1, 1, 1, 0};
  
  GL *gl = rc->getGL();
  
  MutableMatrix44D M0 = rc->getNextCamera()->getProjectionMatrix();
  MutableMatrix44D M1 = MutableMatrix44D::createOrthographicProjectionMatrix(0, 256, 0, 256, -1, 1);
  gl->setProjection(M1);
  gl->pushMatrix();
  gl->loadMatrixf(MutableMatrix44D::identity());
  
  if (numTexture==0) {
  
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    
    gl->enableVerticesPosition();
    gl->transformTexCoords(1.0, 1.0, 0.0, 0.0);
    
    gl->setTextureCoordinates(2, 0, texCoords);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 2, 2, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels1);
    gl->vertexPointer(2, 0, v1);
    gl->drawTriangleStrip(4, i);
    
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, 2, 2, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels2);
    gl->vertexPointer(2, 0, v2);
    gl->drawTriangleStrip(4, i);
  }
  else {
    glClearColor(0, 1, 0, 1);
    glClear(GL_COLOR_BUFFER_BIT);
  }
  
  // restore viewport
  glBindFramebuffer(GL_FRAMEBUFFER, defaultFramebuffer);
  glViewport(currentViewport[0], currentViewport[1], currentViewport[2], currentViewport[3]);
    
  glEnable(GL_DEPTH_TEST);
  glDisable(GL_BLEND);
  gl->setProjection(M0);
  gl->popMatrix();

}


int SimpleFBORenderer::render(const RenderContext* rc)
{    
  static bool firstTime = true;
  
  GL *gl = rc->getGL();
  gl->enableTextures();
  gl->enableTexture2D();
  
  //glBindTexture(GL_TEXTURE_2D, _idTexture);
  
  gl->transformTexCoords(1.0, 1.0, 0.0, 0.0);  
  gl->enableVerticesPosition();
  
  // draw first square
  if (firstTime) {
    firstTime = false;
    renderFBO(rc, 0);
    renderFBO(rc, 1);
  }

  glBindTexture(GL_TEXTURE_2D, _fboTexture[0]);
  gl->setTextureCoordinates(2, 0, _texCoords);
  gl->vertexPointer(3, 0, _vertices);
  gl->pushMatrix();
  gl->multMatrixf(MutableMatrix44D::createTranslationMatrix(Vector3D(0,-1.2e6,0)));
  gl->drawTriangleStrip(4, _indices);
  gl->popMatrix();
  
  // draw second square
  //renderFBO(rc, 1);
  glBindTexture(GL_TEXTURE_2D, _fboTexture[1]);
  gl->setTextureCoordinates(2, 0, _texCoords);
  gl->vertexPointer(3, 0, _vertices);
  gl->pushMatrix();
  gl->multMatrixf(MutableMatrix44D::createTranslationMatrix(Vector3D(0,1.2e6,0)));
  gl->drawTriangleStrip(4, _indices);
  gl->popMatrix();
  
  
  gl->disableVerticesPosition();  
  gl->disableTexture2D();
  gl->disableTextures();
  
  return MAX_TIME_TO_RENDER;
}

