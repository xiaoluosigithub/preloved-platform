package com.example.secondhand.service.impl;

import com.example.secondhand.mapper.UserMapper;
import com.example.secondhand.model.entity.User;
import com.example.secondhand.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private UserMapper userMapper;

  @Override
  public int createUser(User user) {
    return userMapper.insert(user);
  }

  @Override
  public User findByUsername(String username) {
    return userMapper.findByUsername(username);
  }

  @Override
  public User findById(Long id) {
    return userMapper.findById(id);
  }

  @Override
  public int updateProfile(User user) {
    return userMapper.updateProfile(user);
  }
}