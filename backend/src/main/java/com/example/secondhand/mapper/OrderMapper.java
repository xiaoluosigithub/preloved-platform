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

  @Select("<script>SELECT DATE(created_at) as day, COUNT(1) as cnt, SUM(price) as amount FROM orders WHERE 1=1 <if test='from != null'> AND created_at &gt;= #{from} </if> <if test='to != null'> AND created_at &lt;= #{to} </if> AND status IN ('PAID','SHIPPED','RECEIVED') GROUP BY DATE(created_at) ORDER BY day</script>")
  java.util.List<java.util.Map<String, Object>> statsDaily(@Param("from") String from, @Param("to") String to);

  @Select("SELECT o.id, o.order_no as orderNo, o.buyer_id as buyerId, o.seller_id as sellerId, o.product_id as productId, o.product_title as productTitle, o.product_price as productPrice, o.price, o.status, o.receiver_name as receiverName, o.receiver_phone as receiverPhone, o.receiver_address as receiverAddress, o.shipping_no as shippingNo, o.shipping_company as shippingCompany, o.shipped_at as shippedAt, o.received_at as receivedAt, o.created_at as createdAt, o.updated_at as updatedAt, (SELECT username FROM user WHERE id=o.buyer_id) as buyerUsername, (SELECT username FROM user WHERE id=o.seller_id) as sellerUsername FROM orders o WHERE o.product_id = #{productId} ORDER BY o.id DESC")
  java.util.List<java.util.Map<String, Object>> findByProductWithNames(@Param("productId") Long productId);
}
