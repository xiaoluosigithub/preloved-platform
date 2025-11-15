package com.example.secondhand.mapper;

import com.example.secondhand.model.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

  @Insert("INSERT INTO product(title,description,price,seller_id,category_id,images,status) VALUES(#{title},#{description},#{price},#{sellerId},#{categoryId},#{images},#{status})")
  @Options(useGeneratedKeys=true,keyProperty="id")
  int insert(Product p);

  @Update("<script>UPDATE product SET title=#{title},description=#{description},price=#{price},category_id=#{categoryId},images=#{images},status=#{status} WHERE id=#{id}</script>")
  int update(Product p);

  @Delete("DELETE FROM product WHERE id=#{id}")
  int delete(Long id);

  @Select("SELECT id,title,description,price,seller_id as sellerId,category_id as categoryId,images,status,created_at as createdAt,updated_at as updatedAt FROM product WHERE id=#{id}")
  Product findById(Long id);

  @Select("<script>SELECT id,title,description,price,seller_id as sellerId,category_id as categoryId,images,status,created_at as createdAt,updated_at as updatedAt FROM product <where><if test='categoryId != null'> AND category_id = #{categoryId} </if><if test='keyword != null and keyword.trim() != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%')) </if></where> ORDER BY created_at DESC LIMIT #{offset}, #{limit}</script>")
  List<Product> findByPage(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

  @Select("<script>SELECT COUNT(1) FROM product <where><if test='categoryId != null'> AND category_id = #{categoryId} </if><if test='keyword != null and keyword.trim() != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%')) </if></where></script>")
  int countByFilter(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword);
}