//
//  MeshBuilder.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 13/09/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_MeshBuilder_hpp
#define G3MiOSSDK_MeshBuilder_hpp

class Vector2D;
class GLTextureId;
class Mesh;

class MeshBuilder{
public:
  
  static Mesh* createQuadXYMesh(const Vector2D& max, const Vector2D& min, const GLTextureId& texId);  
  
};

#endif
