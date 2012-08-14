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
