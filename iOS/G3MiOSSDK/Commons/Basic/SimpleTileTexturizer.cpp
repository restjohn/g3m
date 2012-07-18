//
//  SimpleTileTexturizer.cpp
//  G3MiOSSDK
//
//  Created by Diego Gomez Deck on 12/07/12.
//  Copyright (c) 2012 IGO Software SL. All rights reserved.
//

#include "SimpleTileTexturizer.hpp"

#include "Context.hpp"
#include "TextureMapping.hpp"
#include "TexturedMesh.hpp"

#include "TilePetitions.hpp"


TilePetitions* SimpleTileTexturizer::getTilePetitions(const Tile* tile)
{
  
  std::string url = "http://www.arkive.org/images/browse/world-map.jpg"; //FIXED

  //SAVING PETITION
  TilePetitions *tt = new TilePetitions(tile->getLevel(), tile->getRow(), tile->getColumn(), this);
  tt->add(url, tile->getSector());
  
  return tt;
}

std::vector<MutableVector2D> SimpleTileTexturizer::createTextureCoordinates() const
{
  std::vector<MutableVector2D> texCoor;
  
  int res = _parameters->_tileResolution;

  const double lonRes1 = (double) (res -1), latRes1 = (double) (res-1);
  for(double i = 0.0; i < res; i++){
    double u = (i / lonRes1);
    for (double j = 0.0; j < res; j++) {
      const double v = (j / latRes1);
      MutableVector2D v2d(v,u);
      texCoor.push_back(v2d);
    }
  }
  
  return texCoor;
}

Mesh* SimpleTileTexturizer::getMesh(Tile* tile, Mesh* tessellatorMesh)
{
  bool dummy = true;
  if (dummy){
    //CHESSBOARD TEXTURE
    int texID = _rc->getTexturesHandler()->getTextureIdFromFileName(_rc, "NoImage.jpg", _parameters->_tileTextureWidth, _parameters->_tileTextureHeight);
    TextureMapping * tMap = new TextureMapping(texID, createTextureCoordinates());
    return new TexturedMesh(tessellatorMesh, false, tMap, true);
    
  } else{
    //THE TEXTURE HAS BEEN LOADED
    for (int i = 0; i < _finishedTiles.size(); i++) {
      FinishedTile& ft = _finishedTiles[i];
      if (tile->getLevel() == ft._level &&
          tile->getRow() == ft._row &&
          tile->getColumn() == ft._column){
        
        
        //Texture Solved
        tile->setTextureSolved(true);
        
        TextureMapping * tMap = new TextureMapping(ft._texID, createTextureCoordinates());
        return new TexturedMesh(tessellatorMesh, false, tMap, true);
      }
    }
    
    return NULL;
  }
}

Mesh* SimpleTileTexturizer::texturize(const RenderContext* rc,
                                      Tile* tile,
                                      Mesh* tessellatorMesh,
                                      Mesh* previousMesh) {
  
  _rc = rc; //STORING CONTEXT
  
  Mesh* texMesh = getMesh(tile, tessellatorMesh);
  if (texMesh != NULL){
    delete previousMesh;
    return texMesh;
  }
  
  //THROWING AND CREATING THE PETITIONS
  int priority = 10;
  TilePetitions *tp = getTilePetitions(tile);
  Downloader* d = rc->getDownloader();
  for (int i = 0; i < tp->getNumPetitions(); i++) {
    const std::string& url = tp->getPetition(i).getURL();
    d->request(url, priority, tp);
  }
  
  //WE TRY AGAIN IN CASE PETITIONS WERE ATTENDED QUICKLY
  texMesh = getMesh(tile, tessellatorMesh);
  if (texMesh != NULL){
    delete previousMesh;
    return texMesh;
  }
  
  return NULL;
}

void SimpleTileTexturizer::onTilePetitionsFinished(TilePetitions * tp)
{
  //TAKING JUST FIRST!!!
  const ByteBuffer* bb = tp->getPetition(0).getByteBuffer();
  IImage *im = _rc->getFactory()->createImageFromData(*bb);
  
  const std::string& url = tp->getPetition(0).getURL();   
  int texID = _rc->getTexturesHandler()->getTextureId(_rc, im, url, _parameters->_tileTextureWidth, _parameters->_tileTextureHeight);
  
  //RELEASING MEMORY
  _rc->getFactory()->deleteImage(im);

  //Tile finished
  FinishedTile ft;
  ft._column = tp->getColumn();
  ft._level = tp->getLevel();
  ft._row = tp->getRow();
  ft._texID = texID;
  _finishedTiles.push_back(ft);
}

void SimpleTileTexturizer::tileToBeDeleted(Tile* tile) {
  int index = -1;
  for (int i = 0; i < _finishedTiles.size(); i++) {
    FinishedTile& ft = _finishedTiles[i];
    if (tile->getLevel() == ft._level &&
        tile->getRow() == ft._row &&
        tile->getColumn() == ft._column){
      index = i;
      break;
    }
  }
  
  if (index > -1){
    _rc->getTexturesHandler()->takeTexture(_rc, _finishedTiles[index]._texID);
    _finishedTiles.erase(_finishedTiles.begin()+index);
  }
}
