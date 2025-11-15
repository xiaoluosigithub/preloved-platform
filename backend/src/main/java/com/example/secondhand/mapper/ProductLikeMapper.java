package com.example.secondhand.mapper;

import org.apache.ibatis.annotations.*;
import com.example.secondhand.model.entity.ProductLike;

@Mapper
public interface ProductLikeMapper {
  @Insert("INSERT INTO product_like(user_id, product_id) VALUES(#{userId}, #{productId})")
  @Options(useGeneratedKeys=true, keyProperty="id")
  int insert(ProductLike l);

  @Delete("DELETE FROM product_like WHERE user_id=#{userId} AND product_id=#{productId}")
  int delete(@Param("userId") Long userId, @Param("productId") Long productId);

  @Select("SELECT COUNT(1) FROM product_like WHERE product_id=#{productId}")
  int countByProduct(@Param("productId") Long productId);

  @Select("SELECT 1 FROM product_like WHERE user_id=#{userId} AND product_id=#{productId} LIMIT 1")
  Integer exists(@Param("userId") Long userId, @Param("productId") Long productId);

  @Select("<script>SELECT p.id as productId,p.title,p.price,p.images,p.status,p.created_at as createdAt, IF(EXISTS(SELECT 1 FROM favorite f WHERE f.user_id=#{userId} AND f.product_id=p.id),1,0) as favorited FROM product_like l JOIN product p ON l.product_id=p.id WHERE l.user_id=#{userId} ORDER BY l.created_at DESC LIMIT #{offset},#{limit}</script>")
  java.util.List<java.util.Map<String,Object>> listProductsByUser(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

  @Select("SELECT COUNT(1) FROM product_like WHERE user_id=#{userId}")
  int countByUser(@Param("userId") Long userId);

  @Delete("DELETE FROM product_like WHERE product_id=#{productId}")
  int deleteByProduct(@Param("productId") Long productId);
}