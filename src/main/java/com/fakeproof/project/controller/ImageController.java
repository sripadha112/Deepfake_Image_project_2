package com.fakeproof.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fakeproof.project.model.ImageRecord;
import com.fakeproof.project.service.ImageService;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam MultipartFile file) {
        try {
            ImageRecord saved = service.registerImage(file);
            return ResponseEntity.ok("Image registered successfully: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Tampered image or invalid file.");
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestParam MultipartFile file) {
        try {
            boolean valid = service.validateImage(file);
            if (valid) return ResponseEntity.ok("Image is authentic.");
            else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Image is tampered or not registered.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error validating image/ Image is tampered/ it's original version is already exist");
        }
    }
    
    @GetMapping("/test")
    public String testApi(){
    	return "working fine";
    }
    
}
