package com.example.secondhand.model.entity;

import java.sql.Timestamp;

public class User {
  private Long id;
  private String username;
  private String password;
  private String nickname;
  private String role;
  private String avatar;
  private String signature;
  private Timestamp createdAt;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getUsername() { return username; }
  public void setUsername(String u) { this.username = u; }
  public String getPassword() { return password; }
  public void setPassword(String p) { this.password = p; }
  public String getNickname() { return nickname; }
  public void setNickname(String n) { this.nickname = n; }
  public String getRole() { return role; }
  public void setRole(String r) { this.role = r; }
  public String getAvatar() { return avatar; }
  public void setAvatar(String a) { this.avatar = a; }
  public String getSignature() { return signature; }
  public void setSignature(String s) { this.signature = s; }
  public Timestamp getCreatedAt() { return createdAt; }
  public void setCreatedAt(Timestamp t) { this.createdAt = t; }
}