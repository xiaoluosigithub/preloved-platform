package com.example.secondhand.controller;

import com.example.secondhand.common.Result;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

  @PostMapping("/image")
  public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) return Result.fail("file empty");
    String original = file.getOriginalFilename();
    String ext = "";
    if (original != null && original.contains(".")) {
      ext = original.substring(original.lastIndexOf('.'));
    }
    String name = UUID.randomUUID().toString().replaceAll("-", "") + ext;
    // String dir = "src/main/resources/static/uploads";
    String dir = "E:/JavaWeb/SecondHandSystem/test";
    File destDir = new File(dir);
    if (!destDir.exists()) destDir.mkdirs();
    File dest = new File(destDir, name);
    try {
      file.transferTo(dest);
      String url = "/uploads/" + name;
      return Result.ok(url);
    } catch (IOException e) {
      return Result.fail("upload error");
    }
  }
}