package com.example.secondhand.mapper;

import com.example.secondhand.model.entity.Category;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface CategoryMapper {
  @Select("SELECT id,name,created_at as createdAt FROM category ORDER BY id")
  List<Category> findAll();

  @Insert("INSERT INTO category(name) VALUES(#{name})")
  @Options(useGeneratedKeys=true,keyProperty="id")
  int insert(Category c);

  @Select("SELECT id,name,created_at as createdAt FROM category WHERE id=#{id}")
  Category findById(Integer id);

  @Select("SELECT id,name,created_at as createdAt FROM category WHERE name=#{name}")
  Category findByName(String name);

  @Delete("DELETE FROM category WHERE id=#{id}")
  int delete(Integer id);
}