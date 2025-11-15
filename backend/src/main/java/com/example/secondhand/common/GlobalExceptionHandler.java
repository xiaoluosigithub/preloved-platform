package com.example.secondhand.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  public Result<String> handle(Exception ex){
    return Result.fail(ex.getMessage());
  }
}