//
//  DirectMesh.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 06/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_DirectMesh_hpp
#define G3MiOSSDK_DirectMesh_hpp

#include "AbstractMesh.hpp"

#include "Planet.hpp"

class DirectMesh: public AbstractMesh{
  
  DirectMesh(const GLPrimitive primitive,
             bool owner,
             const Vector3D& center,
             IFloatBuffer* vertices,
             const Color* flatColor,
             IFloatBuffer* colors,
             const float colorsIntensity);
public:
    void render(const RenderContext* rc) const;
};


#endif
