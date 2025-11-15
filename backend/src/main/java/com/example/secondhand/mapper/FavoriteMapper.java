package com.example.secondhand.mapper;

import org.apache.ibatis.annotations.*;
import com.example.secondhand.model.entity.Favorite;

@Mapper
public interface FavoriteMapper {
  @Insert("INSERT INTO favorite(user_id, product_id) VALUES(#{userId}, #{productId})")
  @Options(useGeneratedKeys=true, keyProperty="id")
  int insert(Favorite fav);

  @Delete("DELETE FROM favorite WHERE user_id=#{userId} AND product_id=#{productId}")
  int delete(@Param("userId") Long userId, @Param("productId") Long productId);

  @Select("SELECT COUNT(1) FROM favorite WHERE product_id=#{productId}")
  int countByProduct(@Param("productId") Long productId);

  @Select("SELECT 1 FROM favorite WHERE user_id=#{userId} AND product_id=#{productId} LIMIT 1")
  Integer exists(@Param("userId") Long userId, @Param("productId") Long productId);

  @Select("<script>SELECT p.id as productId,p.title,p.price,p.images,p.status,p.created_at as createdAt, IF(EXISTS(SELECT 1 FROM product_like pl WHERE pl.user_id=#{userId} AND pl.product_id=p.id),1,0) as liked FROM favorite f JOIN product p ON f.product_id=p.id WHERE f.user_id=#{userId} ORDER BY f.created_at DESC LIMIT #{offset},#{limit}</script>")
  java.util.List<java.util.Map<String,Object>> listProductsByUser(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

  @Select("SELECT COUNT(1) FROM favorite WHERE user_id=#{userId}")
  int countByUser(@Param("userId") Long userId);

  @Delete("DELETE FROM favorite WHERE product_id=#{productId}")
  int deleteByProduct(@Param("productId") Long productId);
}