package com.example.secondhand.mapper;

import com.example.secondhand.model.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
  @Insert("INSERT INTO user(username,password,nickname,role) VALUES(#{username},#{password},#{nickname},#{role})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(User user);

  @Select("SELECT id,username,password,nickname,role,avatar,signature,created_at as createdAt FROM user WHERE username=#{username}")
  User findByUsername(String username);

  @Select("SELECT id,username,password,nickname,role,avatar,signature,created_at as createdAt FROM user WHERE id=#{id}")
  User findById(Long id);

  @Update("UPDATE user SET nickname=#{nickname}, avatar=#{avatar}, signature=#{signature} WHERE id=#{id}")
  int updateProfile(User user);
}