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


extern GLuint fboHandle; 
extern GLuint fboTex;


void NativeGL2_iOS::initFBORender2Texture()
{
  // obtain current current frame buffer parameters
  GLint defaultFrameBuffer;
  glGetFramebufferAttachmentParameteriv(GL_FRAMEBUFFER, 
                                        GL_COLOR_ATTACHMENT0, 
                                        GL_FRAMEBUFFER_ATTACHMENT_OBJECT_NAME, 
                                        &defaultFrameBuffer);
  
  // create buffer for render to texture
  GLuint fbo_width = 256;
  GLuint fbo_height = 256;
  glGenFramebuffers(1, &fboHandle);
  glGenTextures(1, &fboTex);      
  glBindFramebuffer(GL_FRAMEBUFFER, fboHandle);
  glBindTexture(GL_TEXTURE_2D, fboTex);
  glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB,fbo_width, fbo_height,
               0, GL_RGB, GL_UNSIGNED_SHORT_5_6_5, NULL);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
  glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
  glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER_APPLE, GL_COLOR_ATTACHMENT0, 
                         GL_TEXTURE_2D, fboTex, 0);
  
  // FBO status check
  GLenum status;
  status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
  switch(status) {
    case GL_FRAMEBUFFER_COMPLETE:
      printf ("fbo complete\n");
      break;
      
    case GL_FRAMEBUFFER_UNSUPPORTED:
      printf ("fbo unsupported\n");
      break;
      
    default:
      printf ("Framebuffer Error\n");
      break;
  }
  
  glBindFramebuffer(GL_FRAMEBUFFER, defaultFrameBuffer);    
}
