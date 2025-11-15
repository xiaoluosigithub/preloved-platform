package com.example.secondhand.service.impl;

import com.example.secondhand.mapper.CommentMapper;
import com.example.secondhand.model.entity.Comment;
import com.example.secondhand.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
  @Autowired private CommentMapper mapper;
  @Override
  public int addComment(Comment c) { return mapper.insert(c); }
  @Override
  public List<Map<String,Object>> listComments(Long productId, int page, int size) {
    if (page < 1) page = 1;
    int offset = (page - 1) * size;
    return mapper.findByProduct(productId, offset, size);
  }
  @Override
  public int countComments(Long productId) { return mapper.countByProduct(productId); }
}