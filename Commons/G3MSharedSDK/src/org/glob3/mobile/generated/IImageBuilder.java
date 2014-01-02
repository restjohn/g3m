package org.glob3.mobile.generated; 
//
//  IImageBuilder.cpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 1/2/14.
//
//

//
//  IImageBuilder.hpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 1/2/14.
//
//


//class G3MContext;
//class IImageBuilderListener;


public interface IImageBuilder
{

  void dispose();

  void build(G3MContext context, IImageBuilderListener listener, boolean deleteListener);

  String getImageName();

}