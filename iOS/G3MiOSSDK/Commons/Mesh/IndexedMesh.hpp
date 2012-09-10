//
//  IndexedMesh.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 22/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_IndexedMesh_h
#define G3MiOSSDK_IndexedMesh_h

#include "AbstractMesh.hpp"


class IndexedMesh : public AbstractMesh {
private:
  
  IIntBuffer* _indexes;
  
public:
  IndexedMesh(const GLPrimitive primitive,
              bool owner,
              const Vector3D& center,
              IFloatBuffer* vertices,
              IIntBuffer* indices,
              const Color* flatColor = NULL,
              IFloatBuffer* colors = NULL,
              const float colorsIntensity = (float)0.0);
  
  ~IndexedMesh();

  void render(const RenderContext* rc) const;
};

#endif
