//
//  GPUTextureBuilder.cpp
//  G3MiOSSDK
//
//  Created by Agustín Trujillo Pino on 13/08/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//

#include <iostream>

#include "GPUTextureBuilder.hpp"
#include "Context.hpp"
#include "GL.hpp"
#include "Camera.hpp"

#include <OpenGLES/ES2/gl.h>
#include <OpenGLES/ES2/glext.h>


extern unsigned int fboHandle; 
extern int          defaultFramebuffer;


void GPUTextureBuilder::initialize(const InitializationContext* ic)
{
  _fboContext = ic->getGL()->initFBORender2Texture();
  
  fboHandle = _fboContext._fboHandle;
  defaultFramebuffer = _fboContext._defaultFrameBuffer;
  
}


int GPUTextureBuilder::renderImageInFBO(const RenderContext* rc) const
{
  unsigned int fbo_width = 256;
  unsigned int fbo_height = 256;

  // init params
  GL* gl = rc->getGL();
  gl->enableTextures();
  gl->enableTexture2D();
  gl->transformTexCoords(1.0, 1.0, 0.0, 0.0);  
  gl->enableVerticesPosition();
  
  int __agustin_note; // this function must be private
  // get texture id
  int texID = gl->getTextureID();
  
  // create buffer for render to texture
  glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
  glBindTexture(GL_TEXTURE_2D, texID);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,fbo_width, fbo_height,
               0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, NULL);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER_APPLE, GL_COLOR_ATTACHMENT0, 
                         GL_TEXTURE_2D, texID, 0);
  
  // init Frame Buffer to draw
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
    
  MutableMatrix44D M0 = rc->getNextCamera()->getProjectionMatrix();
  MutableMatrix44D M1 = MutableMatrix44D::createOrthographicProjectionMatrix(0, 256, 0, 256, -1, 1);
  gl->setProjection(M1);
  gl->pushMatrix();
  gl->loadMatrixf(MutableMatrix44D::identity());
  
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
  
  // restore viewport
  glBindFramebuffer(GL_FRAMEBUFFER, defaultFramebuffer);
  glViewport(currentViewport[0], currentViewport[1], currentViewport[2], currentViewport[3]);
  
  glEnable(GL_DEPTH_TEST);
  glDisable(GL_BLEND);
  gl->setProjection(M0);
  gl->popMatrix();
  
  return texID;
}




int GPUTextureBuilder::createTextureFromImages(const RenderContext* rc,
                                               const std::vector<const IImage*>& vImages, 
                                               const std::vector<const Rectangle*>& vRectangles, 
                                               int width, int height) const 
{
  
  /*printf ("createTextureFromImages. width=%d  height=%d\n");
  for (unsigned int n=0; n<vImages.size(); n++) 
    printf ("--- image=%d width=%d height=%d. Rectangle xy=%.2f, %.2f  w=%f, h=%f\n",
            n, vImages[n]->getWidth(), vImages[n]->getHeight(),
            vRectangles[n]->_x, vRectangles[n]->_y,
            vRectangles[n]->_width, vRectangles[n]->_height);*/
  
  if (width!=256 || height!=256) 
    printf ("**** GPUTextureBuilder only works with 256x256 textures in the image output!!\n");
  
  return renderImageInFBO(rc);
  
/*  
  const IImage* base;
  int i = 0; //First image to merge
  Rectangle baseRec(0,0, width, height);
  if (vRectangles[0]->equalTo(baseRec)){
    base = vImages[0];
    i = 1;
  } else{
    base = factory->createImageFromSize(width, height);
  }
  
  for (; i < vImages.size(); i++) {
    IImage* im2 = base->combineWith(*vImages[i], *vRectangles[i], width, height);
    
    if (base != vImages[0]) {
      delete base;
    }
    
    base = im2;
  }
  
  int texID = rc->getGL()->uploadTexture(base, width, height);
  
  if (base != vImages[0]) {
    delete base;
  }
  
  return texID;*/
}


int __agustin_at_work;
// THIS FUNCTION STILL IS IN CPU VERSION

int GPUTextureBuilder::createTextureFromImages(const RenderContext* rc,
                                               const std::vector<const IImage*>& images,
                                               int width, int height) const 
{
  printf ("GPUTextureBuilder::createTextureFromImages still not implemented!! (using CPU version)\n");
  
/*  const int imagesSize = images.size();
  
  if (imagesSize == 0) {
    return -1;
  }
  
  const IImage* im = images[0];
  const IImage* im2 = NULL;
  for (int i = 1; i < imagesSize; i++) {
    const IImage* imTrans = images[i];
    im2 = im->combineWith(*imTrans, width, height);
    if (i > 1) {
      delete im;
    }
    im = im2;
  }
  
  int texID = rc->getGL()->uploadTexture(im, width, height);
  
  if (imagesSize > 1) {
    delete im;
  }
  
  return texID;*/
}


