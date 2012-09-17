//
//  NativeGL_iOS.hpp
//  G3MiOSSDK
//
//  Created by José Miguel S N on 31/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#ifndef G3MiOSSDK_NativeGL_iOS_hpp
#define G3MiOSSDK_NativeGL_iOS_hpp

#include <OpenGLES/ES2/gl.h>

#include "INativeGL.hpp"




class NativeGL2_iOS: public INativeGL
{
  
  inline GLError getError(GLenum e) const {
    switch (e) {
      case GL_NO_ERROR:
        return NoError;
      case GL_INVALID_ENUM:
        return InvalidEnum;
      case GL_INVALID_VALUE:
        return InvalidValue;
      case GL_INVALID_OPERATION:
        return InvalidOperation;
      case GL_OUT_OF_MEMORY:
        return OutOfMemory;
    }
    return UnknownError;
  }
  
  inline GLenum getEnum(GLBlendFactor b) const {
    switch (b) {
      case SrcAlpha:
        return GL_SRC_ALPHA;
      case OneMinusSrcAlpha:
        return GL_ONE_MINUS_SRC_ALPHA;
    }
  }
  
  inline GLenum getEnum(GLAlignment a) const {
    switch (a) {
      case Unpack:
        return GL_UNPACK_ALIGNMENT;
      case Pack:
        return GL_PACK_ALIGNMENT;
    }
  }
  
  inline GLenum getEnum(GLTextureType t) const {
    switch (t) {
      case Texture2D:
        return GL_TEXTURE_2D;
    }
  }
  
  inline GLenum getEnum(GLTextureParameter t) const {
    switch (t) {
      case MinFilter:
        return GL_TEXTURE_MIN_FILTER;
      case MagFilter:
        return GL_TEXTURE_MAG_FILTER;
      case WrapS:
        return GL_TEXTURE_WRAP_S;
      case WrapT:
        return GL_TEXTURE_WRAP_T;
    }
  }
  
  inline GLint getValue(GLTextureParameterValue t) const {
    switch (t) {
      case Linear:
        return GL_LINEAR;
      case ClampToEdge:
        return GL_CLAMP_TO_EDGE;
    }
  }
  
  inline GLenum getEnum(GLFormat f) const {
    switch (f) {
      case RGBA:
        return GL_RGBA;
    }
  }
  
  inline GLenum getEnum(GLVariable f) const {
    switch (f) {
      case Viewport:
        return GL_VIEWPORT;
    }
  }
  
public:
  
  void useProgram(int program) const {
    glUseProgram(program);
  }
  
  int getAttribLocation(int program, const std::string& name) const {
    return glGetAttribLocation(program, name.c_str());
  }
  
  int getUniformLocation(int program, const std::string& name) const {
    return glGetUniformLocation(program, name.c_str());
  }
  
  void uniform2f(int loc, float x, float y) const {
    glUniform2f(loc, x, y);
  }
  
  void uniform1f(int loc, float x) const {
    glUniform1f(loc, x);
  }
  
  void uniform1i(int loc, int v) const {
    glUniform1i(loc, v);
  }
  
  void uniformMatrix4fv(int location, int count, bool transpose, const float value[]) const {
    glUniformMatrix4fv(location, count, transpose, value);
  }
  
  void clearColor(float red, float green, float blue, float alpha) const {
    glClearColor(red, green, blue, alpha);
  }
  
  void clear(int buffers) const {
    glClear(buffers);
  }
  
  void uniform4f(int location, float v0, float v1, float v2, float v3) const {
    glUniform4f(location, v0, v1, v2, v3);
  }
  
  void enable(int feature) const {
    glEnable(feature);
  }
  
  void disable(int feature) const {
    glDisable(feature);
  }
  
  void polygonOffset(float factor, float units) const {
    glPolygonOffset(factor, units);
  }
  
  void vertexAttribPointer(int index,
                           int size,
                           bool normalized,
                           int stride,
                           IFloatBuffer* buffer) const {
    float* pointer = ((FloatBuffer_iOS*) buffer)->getPointer();
    glVertexAttribPointer(index, size, GL_FLOAT, normalized, stride, pointer);
  }
  
  void drawElements(int mode,
                    int count,
                    IIntBuffer* buffer) const {
    int has_to_set_GL_UNSIGNED_INT; //???????
    int* pointer = ((IntBuffer_iOS*) buffer)->getPointer();
    glDrawElements(mode, count, GL_UNSIGNED_INT, pointer);
  }
  
  void lineWidth(float width) const {
    glLineWidth(width);
  }
  
  GLError getError() const {
    return getError(glGetError());
  }
  
  void blendFunc(GLBlendFactor sfactor, GLBlendFactor dfactor) const {
    glBlendFunc(getEnum(sfactor), getEnum(dfactor));
  }
  
  void bindTexture(GLTextureType target, int texture) const {
    glBindTexture(getEnum(target), texture);
  }
  
  void deleteTextures(int n, const int textures[]) const {
    unsigned int ts[n];
    for(int i = 0; i < n; i++){
      ts[i] = textures[i];
    }
    glDeleteTextures(n, ts);
  }
  
  void enableVertexAttribArray(int location) const {
    glEnableVertexAttribArray(location);
  }
  
  void disableVertexAttribArray(int location) const {
    glDisableVertexAttribArray(location);
  }
  
  void pixelStorei(GLAlignment pname, int param) const {
    glPixelStorei(getEnum(pname), param);
  }
  
  std::vector<GLTextureId> genTextures(int n) const {
    GLuint textures[n];
    glGenTextures(n, textures);
    std::vector<GLTextureId> ts;
    for(int i = 0; i < n; i++){
      ts.push_back( GLTextureId(textures[i]) );
    }
    return ts;
  }
  
  void texParameteri(GLTextureType target,
                     GLTextureParameter par,
                     GLTextureParameterValue v) const {
    glTexParameteri(getEnum(target), getEnum(par), getValue(v));
  }
  
  void texImage2D(const IImage* image, GLFormat format) const {
    unsigned char* data = ((Image_iOS*) image)->createByteArrayRGBA8888();
    
    glTexImage2D(GL_TEXTURE_2D,
                 0, 
                 getEnum(format),
                 image->getWidth(), 
                 image->getHeight(), 
                 0, 
                 getEnum(format),
                 GL_UNSIGNED_BYTE, 
                 data);
    
    delete [] data;
  }
  
  void generateMipmap(GLTextureType target) const {
    glGenerateMipmap(getEnum(target));
  }
  
  void drawArrays(int mode, int first, int count) const {
    glDrawArrays(mode, first, count);
  }
  
  void cullFace(int c) const {
    glCullFace(c);
  }
  
  void getIntegerv(GLVariable v, int i[]) const {
    glGetIntegerv(getEnum(v), i);
  }
  
  int CullFace_Front() const{
    return GL_FRONT;
  }
  int CullFace_Back() const{
    return GL_BACK;
  }
  int CullFace_FrontAndBack() const{
    return GL_FRONT_AND_BACK;
  }
  int BufferType_ColorBuffer() const{
    return GL_COLOR_BUFFER_BIT;
  }
  int BufferType_DepthBuffer() const{
    return GL_DEPTH_BUFFER_BIT;
  }
  int Feature_PolygonOffsetFill() const{
    return GL_POLYGON_OFFSET_FILL;
  }
  int Feature_DepthTest() const{
    return GL_DEPTH_TEST;
  }
  int Feature_Blend() const{
    return GL_BLEND;
  }
  int Feature_CullFace() const{
    return GL_CULL_FACE;
  }
  int Type_Float() const{
    return GL_FLOAT;
  }
  int Type_UnsignedByte() const{
    return GL_UNSIGNED_BYTE;
  }
  int Type_UnsignedInt() const{
    return GL_UNSIGNED_INT;
  }
  int Type_Int() const{
    return GL_INT;
  }
  int Primitive_TriangleStrip() const{
    return GL_TRIANGLE_STRIP;
  }
  virtual int Primitive_Lines() const{
    return GL_LINES;
  }
  virtual int Primitive_LineLoop() const{
    return GL_LINE_LOOP;
  }
  virtual int Primitive_Points() const{
    return GL_POINTS;
  }
  
};

#endif
