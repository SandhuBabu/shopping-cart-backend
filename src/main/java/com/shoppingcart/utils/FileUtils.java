package com.shoppingcart.utils;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;


@Service
public class FileUtils {
    public String saveImage(MultipartFile file, Long productId) {
        try {

            String rootDir = System.getProperty("user.dir");
            String subdir = "resources/images/products";
            Path location = Paths.get(rootDir, subdir);
            if (!Files.exists(location)) {
                Files.createDirectories(location);
            }

            String fileName = Objects.requireNonNull(file.getOriginalFilename()).replace(' ', '-');

            byte[] bytes = file.getBytes();
            Path imagePath = location.resolve(
                    "product" + productId.toString() + fileName);
            Files.write(imagePath, bytes);


            return "http://localhost:8080/images/products/product" + productId.toString() + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return null;
        }
    }

    public boolean deleteImage(String imageName) {
        String root=System.getProperty("user.dir");
        String filePath = root+"/resources/images/products/"+imageName;
        File file = new File(filePath);


        System.out.println(file.getAbsolutePath());

        if(!file.exists()) {
            System.out.println("NO_PATH");
            return false;
        }

        return file.delete();
    }
}
