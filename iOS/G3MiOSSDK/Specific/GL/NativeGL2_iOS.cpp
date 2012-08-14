//
//  NativeGL2_iOS.cpp
//  G3MiOSSDK
//
//  Created by Agustín Trujillo Pino on 13/08/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//

#include <iostream>

#include "NativeGL2_iOS.hpp"
#include <OpenGLES/ES2/glext.h>



std::vector<int> NativeGL2_iOS::genTextures(int n) const 
{
  GLuint textureID[n];    
  glGenTextures(n, textureID);
  std::vector<int> ts;
  for(int i = 0; i < n; i++){
    ts.push_back(textureID[i]);
  }
  return ts;
}


void NativeGL2_iOS::deleteTextures(int n, const int textures[]) const
{
  unsigned int ts[n];
  for(int i = 0; i < n; i++){
    ts[i] = textures[i];
  }
  glDeleteTextures(n, ts);
}


FBOContext NativeGL2_iOS::initFBORender2Texture()
{
  GLuint fboHandle; 
  
  // obtain current current frame buffer parameters
  GLint defaultFrameBuffer;
  glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, 
                                        GL_COLOR_ATTACHMENT0, 
                                        GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, 
                                        &defaultFrameBuffer);
  
  // generate new frane buffer
  glGenFramebuffers(1, &fboHandle);
    
  // return FBOContext
  return FBOContext(fboHandle, defaultFrameBuffer);
}

void NativeGL2_iOS::startRenderFBO(GLuint handle, int texID, int width, int height)
{
  // create buffer for render to texture
  glBindFramebuffer(GL_FRAMEBUFFER, handle);
  glBindTexture(GL_TEXTURE_2D, texID);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height,
               0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, NULL);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER_APPLE, GL_COLOR_ATTACHMENT0, 
                         GL_TEXTURE_2D, texID, 0);
  
  // init Frame Buffer to draw
  glBindTexture(GL_TEXTURE_2D, 0);
  glBindFramebuffer(GL_FRAMEBUFFER, handle);
  glDisable(GL_DEPTH_TEST);
  glViewport(0,0, width, height);
  glEnable(GL_BLEND);
  glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
}

void NativeGL2_iOS::getViewport(int viewport[4])
{
  glGetIntegerv(GL_VIEWPORT, viewport);
}
