package com.example.secondhand.common;

public class Result<T> {
  private int code;
  private String msg;
  private T data;

  public Result(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
  }

  public static <T> Result<T> ok(T data) {
    return new Result<>(0, "ok", data);
  }

  public static Result<String> ok() {
    return new Result<>(0, "ok", null);
  }

  public static Result<String> fail(String msg) {
    return new Result<>(-1, msg, null);
  }

  public int getCode() { return code; }
  public String getMsg() { return msg; }
  public T getData() { return data; }
  public void setCode(int c) { this.code = c; }
  public void setMsg(String m) { this.msg = m; }
  public void setData(T d) { this.data = d; }
}