package com.example.secondhand.mapper;

import com.example.secondhand.model.entity.Order;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderMapper {

  @Insert("INSERT INTO orders(order_no, buyer_id, seller_id, product_id, product_title, product_price, price) " +
          "VALUES(#{orderNo}, #{buyerId}, #{sellerId}, #{productId}, #{productTitle}, #{productPrice}, #{price})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Order order);

  @Select("SELECT id, order_no as orderNo, buyer_id as buyerId, seller_id as sellerId, product_id as productId, " +
          "price, status, product_title as productTitle, product_price as productPrice, created_at as createdAt " +
          "FROM orders WHERE id = #{id}")
  Order findById(@Param("id") Long id);

  @Select("SELECT id, order_no as orderNo, buyer_id as buyerId, seller_id as sellerId, product_id as productId, " +
          "price, status, product_title as productTitle, product_price as productPrice, created_at as createdAt " +
          "FROM orders WHERE buyer_id = #{buyerId} ORDER BY id DESC")
  List<Order> findByBuyer(@Param("buyerId") Long buyerId);

  @Select("SELECT id, order_no as orderNo, buyer_id as buyerId, seller_id as sellerId, product_id as productId, " +
          "price, status, product_title as productTitle, product_price as productPrice, created_at as createdAt " +
          "FROM orders WHERE seller_id = #{sellerId} ORDER BY id DESC")
  List<Order> findBySeller(@Param("sellerId") Long sellerId);

  @Update("UPDATE orders SET status = #{status} WHERE id = #{id}")
  int updateStatus(@Param("id") Long id, @Param("status") String status);

  @Select("SELECT COUNT(1) FROM orders WHERE product_id = #{productId} AND status IN ('PENDING','PAID','SHIPPED','RECEIVED')")
  int countActiveByProduct(@Param("productId") Long productId);
}