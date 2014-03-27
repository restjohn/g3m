//
//  SGNode.cpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 11/8/12.
//
//

#include "SGNode.hpp"

#include "GLGlobalState.hpp"
//#include "GPUProgramState.hpp"

#include "SGShape.hpp"
#include "Box.hpp"

SGNode::~SGNode() {
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    SGNode* child = _children[i];
    delete child;
  }
}


void SGNode::initialize(const G3MContext* context,
                        SGShape *shape) {
  _context = context;
  _shape = shape;

  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    SGNode* child = _children[i];
    child->initialize(context, shape);
  }
}

void SGNode::addNode(SGNode* child) {
  //  child->setParent(this);
  _children.push_back(child);
  if (_context != NULL) {
    child->initialize(_context, _shape);
  }
}

bool SGNode::isReadyToRender(const G3MRenderContext* rc) {
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    SGNode* child = _children[i];
    if (!child->isReadyToRender(rc)) {
      return false;
    }
  }

  return true;
}

void SGNode::prepareRender(const G3MRenderContext* rc) {

}

void SGNode::cleanUpRender(const G3MRenderContext* rc) {

}

void SGNode::render(const G3MRenderContext* rc, const GLState* parentGLState, bool renderNotReadyShapes) {

//  ILogger::instance()->logInfo("Rendering SG: " + description());

  const GLState* glState = createState(rc, parentGLState);
  if (glState != NULL) {

    prepareRender(rc);

    rawRender(rc, glState);

    const int childrenCount = _children.size();
    for (int i = 0; i < childrenCount; i++) {
      SGNode* child = _children[i];
      child->render(rc, glState, renderNotReadyShapes);
    }

    cleanUpRender(rc);
  } else{
    ILogger::instance()->logError("NO GLSTATE");
  }
}

Box* SGNode::getCopyBoundingBox() {
  Box* boundingBox = NULL;
  for (int i=0; i<_children.size(); i++) {
    Box* boundBoxChild = _children[i]->getCopyBoundingBox();
    if (boundBoxChild != NULL) {
      if (boundingBox == NULL)
        boundingBox = boundBoxChild;
        else {
          Box* tempBox = boundingBox->mergedWithBox(boundBoxChild);
          delete boundingBox;
          delete boundBoxChild;
          boundingBox = tempBox;
        }
    }
  }
  return boundingBox;
}

void SGNode::zRender(const G3MRenderContext* rc,
                     const GLState* parentState){

  const GLState* glState = createZRenderState(rc, parentState);
  if (glState != NULL) {

    zRawRender(rc, glState);

    const int childrenCount = _children.size();
    for (int i = 0; i < childrenCount; i++) {
      SGNode* child = _children[i];
      child->zRender(rc, glState);
    }

    glState->_release();    //We always destroy render Z state

  } else{
    ILogger::instance()->logError("NO GLSTATE FOR ZRENDER");
  }

}
