package com.example.secondhand.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.secondhand.mapper.ProductLikeMapper;
import com.example.secondhand.service.LikeService;
import com.example.secondhand.model.entity.ProductLike;

@Service
public class LikeServiceImpl implements LikeService {
  @Autowired private ProductLikeMapper mapper;
  @Override
  public boolean toggleLike(Long userId, Long productId){
    Integer ex = mapper.exists(userId, productId);
    if(ex != null && ex == 1){
      mapper.delete(userId, productId);
      return false;
    } else {
      ProductLike l = new ProductLike();
      l.setUserId(userId);
      l.setProductId(productId);
      mapper.insert(l);
      return true;
    }
  }
  @Override public int countByProduct(Long productId){ return mapper.countByProduct(productId); }
  @Override public boolean exists(Long userId, Long productId){ return mapper.exists(userId, productId) != null; }
}