package com.example.secondhand.service;

import com.example.secondhand.model.entity.User;

public interface UserService {
  int createUser(User user);
  User findByUsername(String username);
  User findById(Long id);
}