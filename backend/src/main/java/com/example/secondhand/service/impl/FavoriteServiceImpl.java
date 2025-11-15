package com.example.secondhand.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.secondhand.mapper.FavoriteMapper;
import com.example.secondhand.service.FavoriteService;
import com.example.secondhand.model.entity.Favorite;

@Service
public class FavoriteServiceImpl implements FavoriteService {
  @Autowired private FavoriteMapper mapper;
  @Override
  public boolean toggleFavorite(Long userId, Long productId){
    Integer ex = mapper.exists(userId, productId);
    if(ex != null && ex == 1){
      mapper.delete(userId, productId);
      return false;
    } else {
      Favorite f = new Favorite();
      f.setUserId(userId);
      f.setProductId(productId);
      mapper.insert(f);
      return true;
    }
  }
  @Override public int countByProduct(Long productId){ return mapper.countByProduct(productId); }
  @Override public boolean exists(Long userId, Long productId){ return mapper.exists(userId, productId) != null; }
}