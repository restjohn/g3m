//
//  CompositeMesh.cpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 11/17/12.
//
//

#include "CompositeMesh.hpp"

#include "Vector3D.hpp"

CompositeMesh::~CompositeMesh() {
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    Mesh* child = _children[i];
    delete child;
  }

  delete _boundingVolume;

#ifdef JAVA_CODE
  super.dispose();
#endif

}

/**
 * Calculate the average of the centers of the child meshes.
 * If there are no children, return the zero vector.
 * If there is one child, return its center.
 * Otherwise, calculate the minimum bounding box of all the 
 * child centers, and return the center of that.
 */
const Vector3D CompositeMesh::getCenter() const {
  if (_children.size() == 0) {
    return Vector3D::zero;
  }
  
  Vector3D center = _children[0]->getCenter();
  if (_children.size() == 1) {
    return center;
  }
    
  double minX = center._x, minY = center._y, maxX = minX, maxY = minY, maxZ = center._z;

  const size_t childrenCount = _children.size();
  for (size_t i = 1; i < childrenCount; i++) {
    Vector3D center = _children[i]->getCenter();
    if (center._x < minX) {
      minX = center._x;
    }
    else if (center._x > maxX) {
      maxX = center._x;
    }
    if (center._y < minY) {
      minY = center._y;
    }
    else if (center._y > maxY) {
      maxY = center._y;
    }
    if (center._z > maxZ) {
      maxZ = center._z;
    }
  }
    
  return Vector3D((minX + maxX) / 2, (minY + maxY) / 2, maxZ);
}

int CompositeMesh::getVertexCount() const {
  int result = 0;
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    Mesh* child = _children[i];
    result += child->getVertexCount();
  }
  return result;
}

bool CompositeMesh::isTransparent(const G3MRenderContext* rc) const {
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    Mesh* child = _children[i];
    if (child->isTransparent(rc)) {
      return true;
    }
  }
  return false;
}

const Vector3D CompositeMesh::getVertex(int index) const {
  int acumIndex = 0;
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    Mesh* child = _children[i];
    const int childIndex = index - acumIndex;
    const int childSize = child->getVertexCount();
    if (childIndex < childSize) {
      return child->getVertex(childIndex);
    }
    acumIndex += childSize;
  }
  return Vector3D::nan();
}

BoundingVolume* CompositeMesh::calculateBoundingVolume() const {
  const int childrenCount = _children.size();
  if (childrenCount == 0) {
    return NULL;
  }

  BoundingVolume* result = _children[0]->getBoundingVolume();
  for (int i = 1; i < childrenCount; i++) {
    Mesh* child = _children[i];
    BoundingVolume* newResult = result->mergedWith( child->getBoundingVolume() );
    delete result;
    result = newResult;
  }

  return result;
}

BoundingVolume* CompositeMesh::getBoundingVolume() const {
  if (_boundingVolume == NULL) {
    _boundingVolume = calculateBoundingVolume();
  }
  return _boundingVolume;
}

void CompositeMesh::addMesh(Mesh* mesh) {
  delete _boundingVolume;
  _boundingVolume = NULL;

  _children.push_back(mesh);
}

void CompositeMesh::rawRender(const G3MRenderContext* rc,
                              const GLState* parentGLState) const {
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    Mesh* child = _children[i];
    child->render(rc, parentGLState);
  }
}

void CompositeMesh::showNormals(bool v) const {
  const int childrenCount = _children.size();
  for (int i = 0; i < childrenCount; i++) {
    Mesh* child = _children[i];
    child->showNormals(v);
  }
}
