package com.example.secondhand.mapper;

import com.example.secondhand.model.entity.Product;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ProductMapper {

  @Insert("INSERT INTO product(title,description,price,seller_id,category_id,images,status) VALUES(#{title},#{description},#{price},#{sellerId},#{categoryId},#{images},#{status})")
  @Options(useGeneratedKeys=true,keyProperty="id")
  int insert(Product p);

  @Update("<script>UPDATE product SET title=#{title},description=#{description},price=#{price},category_id=#{categoryId},images=#{images} WHERE id=#{id}</script>")
  int update(Product p);

  @Delete("DELETE FROM product WHERE id=#{id}")
  int delete(Long id);

  @Select("SELECT id,title,description,price,seller_id as sellerId,category_id as categoryId,images,status,created_at as createdAt,updated_at as updatedAt FROM product WHERE id=#{id}")
  Product findById(Long id);

  @Select("<script>SELECT id,title,description,price,seller_id as sellerId,category_id as categoryId,images,status,created_at as createdAt,updated_at as updatedAt FROM product WHERE status = 'AVAILABLE' <if test='categoryId != null'> AND category_id = #{categoryId} </if><if test='keyword != null and keyword != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%')) </if> ORDER BY created_at DESC LIMIT #{offset}, #{limit}</script>")
  List<Product> findByPage(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

  @Select("<script>SELECT COUNT(1) FROM product WHERE status = 'AVAILABLE' <if test='categoryId != null'> AND category_id = #{categoryId} </if><if test='keyword != null and keyword != \"\"'> AND (title LIKE CONCAT('%',#{keyword},'%') OR description LIKE CONCAT('%',#{keyword},'%')) </if></script>")
  int countByFilter(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword);

  @Update("UPDATE product SET status=#{status} WHERE id=#{id}")
  int updateStatus(@Param("id") Long id, @Param("status") String status);

  @Update("UPDATE product SET status=#{newStatus} WHERE id=#{id} AND status=#{expectStatus}")
  int updateStatusIfCurrent(@Param("id") Long id, @Param("newStatus") String newStatus, @Param("expectStatus") String expectStatus);

  @Select("SELECT id,title,description,price,seller_id as sellerId,category_id as categoryId,images,status,created_at as createdAt,updated_at as updatedAt FROM product WHERE seller_id=#{sellerId} ORDER BY created_at DESC LIMIT #{offset}, #{limit}")
  List<Product> findBySeller(@Param("sellerId") Long sellerId, @Param("offset") int offset, @Param("limit") int limit);

  @Select("SELECT COUNT(1) FROM product WHERE seller_id=#{sellerId}")
  int countBySeller(@Param("sellerId") Long sellerId);
}
