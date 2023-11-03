package com.shoppingcart.utils;

import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class FileUtils {
    public static String saveImage(MultipartFile file, Long productId) {
        try {

            String rootDir = System.getProperty("user.dir");
            String subdir = "resources/images/products";
            Path location = Paths.get(rootDir, subdir);
            if (!Files.exists(location)) {
                Files.createDirectories(location);
            }

            byte[] bytes = file.getBytes();
            Path imagePath = location.resolve(
                    "product" + productId.toString() + Objects.requireNonNull(
                                    file.getOriginalFilename())
                            .replace(' ', '_')
            );
            Files.write(imagePath, bytes);


            return "http://localhost:8080/images/products/product" + productId.toString() + file.getOriginalFilename();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}
