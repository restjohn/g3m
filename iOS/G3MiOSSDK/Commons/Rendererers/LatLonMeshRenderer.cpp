//
//  LatLonMeshRenderer.cpp
//  G3MiOSSDK
//
//  Created by Agustín Trujillo Pino on 02/08/12.
//  Copyright (c) 2012 Universidad de Las Palmas. All rights reserved.
//


#include "LatLonMeshRenderer.hpp"
#include "GL.hpp"

#include "FloatBufferBuilderFromGeodetic.hpp"
#include "IntBufferBuilder.hpp"
#include "IIntBuffer.hpp"


LatLonMeshRenderer::~LatLonMeshRenderer()
{
  delete _mesh;
}

/*
void LatLonMeshRenderer::initialize(const InitializationContext* ic)
{
#ifdef C_CODE
  FloatBufferBuilderFromGeodetic vertices(FirstVertex, ic->getPlanet(), Geodetic2D::zero());
#else
  FloatBufferBuilderFromGeodetic vertices(CenterStrategy.FirstVertex, ic->getPlanet(), Geodetic2D::zero());  
#endif
  vertices.add(Geodetic3D::fromDegrees(28.753213, -17.898788, 500) );
  vertices.add(Geodetic3D::fromDegrees(28.680347, -17.898788, 500) );
  vertices.add(Geodetic3D::fromDegrees(28.753213, -17.83287,  500) );
  vertices.add(Geodetic3D::fromDegrees(28.680347, -17.83287,  500) );
  
  IntBufferBuilder index;
  for (int i = 0; i < 4; i++) {
    index.add(i);
  }

  Color *flatColor = new Color(Color::fromRGBA((float)1.0, (float)1.0, (float)0.0, (float)1.0));
  
#ifdef C_CODE
  _mesh = new IndexedMesh(TriangleStrip,
                          true,
                          vertices.getCenter(),
                          vertices.create(),
                          index.create(),
                          flatColor);
#endif
#ifdef JAVA_CODE
  _mesh = new IndexedMesh(GLPrimitive.TriangleStrip,
                      true,
                      vertices.getCenter(),
                      vertices.create(),
                      index.create(),
                      flatColor);
#endif

}*/  


 void LatLonMeshRenderer::initialize(const InitializationContext* ic)
 {
 unsigned int numVertices = 4;
 int numIndices = 4;
 
 float v[] = {
 (float) 28.753213, (float) -17.898788, 500,
 (float) 28.680347, (float) -17.898788, 500,
 (float) 28.753213, (float) -17.83287,  500,
 (float) 28.680347, (float) -17.83287,  500
 };
 
 int i[] = { 0, 1, 2, 3};
 
 // create vertices and indices in dinamic memory
 float* vertices = new float [numVertices*3];
 int*   indices  = new int [numIndices];
 
 Color *flatColor = new Color(Color::fromRGBA((float)1.0, (float)1.0, (float)0.0, (float)1.0));
 
 #ifdef C_CODE 
 memcpy(vertices, v, numVertices*3*sizeof(float));
 memcpy(indices, i, numIndices*sizeof(unsigned int));
 // create mesh
 mesh = IndexedMesh::createFromGeodetic3D(ic->getPlanet(), true, Lines, NoCenter, Vector3D(0,0,0), 
 4, vertices, indices, 4, flatColor);
 #endif
 #ifdef JAVA_CODE
 System.arraycopy(v, 0, vertices, 0, v.length);
 System.arraycopy(i, 0, indices, 0, i.length);
 // create mesh
 mesh = IndexedMesh.createFromGeodetic3D(ic.getPlanet(), true, GLPrimitive.TriangleStrip, 
 CenterStrategy.NoCenter, new Vector3D((double)0.0,(double)0.0,(double)0.0), 
 4, vertices, indices, 4, flatColor);
 #endif
 
 }  



void LatLonMeshRenderer::render(const RenderContext* rc) {  
  _mesh->render(rc);
}
