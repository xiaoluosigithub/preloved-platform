package com.example.secondhand.service;

import java.util.List;
import java.util.Map;
import com.example.secondhand.model.entity.Comment;

public interface CommentService {
  int addComment(Comment c);
  List<Map<String,Object>> listComments(Long productId, int page, int size);
  int countComments(Long productId);
}