//
//  GPUTextureBuilder.hpp
//  G3MiOSSDK
//
//  Created by Agustín Trujillo Pino on 13/08/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//

#ifndef G3MiOSSDK_GPUTextureBuilder_hpp
#define G3MiOSSDK_GPUTextureBuilder_hpp

#include "TextureBuilder.hpp"
#include "Context.hpp"
#include "GL.hpp"


class GPUTextureBuilder:public TextureBuilder
{
private:

  
public:
  int createTextureFromImages(GL * gl, const std::vector<const IImage*>& vImages, int width, int height) const;
  
  int createTextureFromImages(GL * gl, const IFactory* factory,
                              const std::vector<const IImage*>& vImages, 
                              const std::vector<const Rectangle*>& vRectangles, 
                              int width, int height) const;
  
  void initialize(const InitializationContext* ic)
  {
    ic->getGL()->initFBORender2Texture();
  }
  
};


#endif
