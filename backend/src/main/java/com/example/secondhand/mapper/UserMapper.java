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

  @Select("<script>SELECT id,username,password,nickname,role,avatar,signature,created_at as createdAt FROM user <if test='keyword != null and keyword.length() > 0'> WHERE (username LIKE CONCAT('%',#{keyword},'%') OR nickname LIKE CONCAT('%',#{keyword},'%')) </if> ORDER BY created_at DESC LIMIT #{offset}, #{limit}</script>")
  java.util.List<User> listUsers(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

  @Select("<script>SELECT COUNT(1) FROM user <if test='keyword != null and keyword.length() > 0'> WHERE (username LIKE CONCAT('%',#{keyword},'%') OR nickname LIKE CONCAT('%',#{keyword},'%')) </if></script>")
  int countUsers(@Param("keyword") String keyword);

  @Update("UPDATE user SET role=#{role} WHERE id=#{id}")
  int updateRole(@Param("id") Long id, @Param("role") String role);
}