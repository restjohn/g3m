//
//  SceneLighting.h
//  G3MiOSSDK
//
//  Created by Jose Miguel SN on 23/08/13.
//
//

#ifndef __G3MiOSSDK__SceneLighting__
#define __G3MiOSSDK__SceneLighting__

#include <iostream>

class GLState;

class SceneLighting{
public:
  virtual ~SceneLighting(){}
  virtual void modifyGLState(GLState* glState) = 0;
};

class DefaultSceneLighting: public SceneLighting{

public:
  void modifyGLState(GLState* glState);
  
};

#endif /* defined(__G3MiOSSDK__SceneLighting__) */