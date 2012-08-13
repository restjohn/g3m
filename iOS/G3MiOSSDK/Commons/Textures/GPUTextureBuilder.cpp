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




extern unsigned int fboHandle; 
extern unsigned int fboTex;
extern int          defaultFramebuffer;


void GPUTextureBuilder::initialize(const InitializationContext* ic)
{
  _fboContext = ic->getGL()->initFBORender2Texture();
  
  fboHandle = _fboContext._fboHandle;
  fboTex = _fboContext._fboTex;
  defaultFramebuffer = _fboContext._defaultFrameBuffer;
  
}


int GPUTextureBuilder::createTextureFromImages(GL * gl, const IFactory* factory,
                                               const std::vector<const IImage*>& vImages, 
                                               const std::vector<const Rectangle*>& vRectangles, 
                                               int width, int height) const 
{
  /*
  printf ("createTextureFromImages. width=%d  height=%d\n");
  for (unsigned int n=0; n<vImages.size(); n++) 
    printf ("--- image=%d width=%d height=%d. Rectangle xy=%.2f, %.2f  w=%f, h=%f\n",
            n, vImages[n]->getWidth(), vImages[n]->getHeight(),
            vRectangles[n]->_x, vRectangles[n]->_y,
            vRectangles[n]->_width, vRectangles[n]->_height);*/
  
  if (width!=256 || height!=256) 
    printf ("**** GPUTextureBuilder only works with 256x256 textures in the image output!!\n");
  
  
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
  
  int texID = gl->uploadTexture(base, width, height);
  
  if (base != vImages[0]) {
    delete base;
  }
  
  return texID;
}


int __agustin_at_work;
// THIS FUNCTION STILL IS IN CPU VERSION

int GPUTextureBuilder::createTextureFromImages(GL * gl, 
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
  
  int texID = gl->uploadTexture(im, width, height);
  
  if (imagesSize > 1) {
    delete im;
  }
  
  return texID;*/
}


