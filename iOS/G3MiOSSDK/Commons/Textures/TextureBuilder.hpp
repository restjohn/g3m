//
//  TextureBuilder.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 23/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_TextureBuilder_hpp
#define G3MiOSSDK_TextureBuilder_hpp

#include "IImage.hpp"
#include "GL.hpp"
#include "IFactory.hpp"
#include <vector>

class InitializationContext;
class RenderContext;

class TextureBuilder
{
public:
  virtual int createTextureFromImages(const RenderContext* rc, 
                                      const std::vector<const IImage*>& vImages, 
                                      int width, int height) const = 0;
  
  virtual int createTextureFromImages(const RenderContext* rc, 
                                      const std::vector<const IImage*>& vImages, 
                                      const std::vector<const Rectangle*>& vRectangles, 
                                      int width, int height) const = 0;
  
  virtual void initialize(const InitializationContext* ic) = 0;
    
  virtual ~TextureBuilder() {} 
};



#endif
