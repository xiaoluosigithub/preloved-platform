package com.example.secondhand.mapper;

import org.apache.ibatis.annotations.*;
import com.example.secondhand.model.entity.Comment;
import java.util.List;
import java.util.Map;

@Mapper
public interface CommentMapper {
  @Insert("INSERT INTO comment(product_id, user_id, content) VALUES(#{productId}, #{userId}, #{content})")
  @Options(useGeneratedKeys=true, keyProperty="id")
  int insert(Comment c);

  @Select("<script>SELECT c.id, c.product_id, c.user_id, c.content, c.created_at, u.username, u.nickname, u.avatar FROM comment c LEFT JOIN `user` u ON c.user_id = u.id WHERE c.product_id = #{productId} ORDER BY c.created_at DESC LIMIT #{offset}, #{limit}</script>")
  @Results({
    @Result(column="id", property="id"),
    @Result(column="product_id", property="productId"),
    @Result(column="user_id", property="userId"),
    @Result(column="content", property="content"),
    @Result(column="created_at", property="createdAt")
  })
  List<Map<String,Object>> findByProduct(@Param("productId") Long productId, @Param("offset") int offset, @Param("limit") int limit);

  @Select("SELECT COUNT(1) FROM comment WHERE product_id=#{productId}")
  int countByProduct(@Param("productId") Long productId);

  @Delete("DELETE FROM comment WHERE product_id=#{productId}")
  int deleteByProduct(@Param("productId") Long productId);
}