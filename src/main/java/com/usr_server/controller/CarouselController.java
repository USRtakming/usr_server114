package com.usr_server.controller;


import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.core.io.InputStreamResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.usr_server.Entity.CarouselEntity;
import com.usr_server.service.CarouselService;


@RestController
@RequestMapping("/api/carousel")
public class CarouselController {

    @Autowired
    private CarouselService carouselService;
    
    // 保存文件的路徑，可以根據需要進行修改
    private static String UPLOAD_DIR = "src/main/resources/static/uploads/CarouselImage";

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    static {
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("png", "image/png");
        MIME_TYPE_MAP.put("gif", "image/gif");
    }
    
    //    查全部
    // http://localhost:8081/api/carousel/allCarousel
    @GetMapping("/allCarousel")
    public List<CarouselEntity> getAllCarousels() {
        List<CarouselEntity> carousels = carouselService.getAllCarousels();
        if (carousels == null || carousels.isEmpty()) {
            System.out.println("未找到輪播數據");
        } else {
            System.out.println("找到輪播數據");
        }
        return carousels;
    }

    //   用id查詢 
    // http://localhost:8081/api/carousel/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarouselById(@PathVariable Long id) {
        try {
            CarouselEntity carousel = carouselService.getCarouselById(id);
            return new ResponseEntity<>(carousel, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("取得輪播失敗", HttpStatus.BAD_REQUEST);
        }
    }
    
    //   創建
    @PostMapping("/create")
    public ResponseEntity<?> createCarousel(
    		@RequestParam("image") MultipartFile file, 
    		@RequestParam("imageName") String imageName) {
        Logger logger = LoggerFactory.getLogger(CarouselController.class);

        if (file.isEmpty()) {
            logger.error("文件為空");
            return new ResponseEntity<>("請選擇要上傳的文件", HttpStatus.BAD_REQUEST);
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                logger.info("創建上傳目錄：" + uploadPath.toString());
            } catch (IOException e) {
                logger.error("創建上傳目錄失敗", e);
                return new ResponseEntity<>("創建上傳目錄失敗", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // 覆蓋已存在的文件
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("文件上傳成功並覆蓋：" + filePath.toString());

            // 創建 CarouselEntity 並設置屬性
            CarouselEntity carousel = new CarouselEntity();
            carousel.setImageName(imageName);
            carousel.setImage(fileName); // 設置圖片的 URL


            CarouselEntity savedCarousel = carouselService.saveCarousel(carousel);
            logger.info("輪播圖已保存：" + savedCarousel.toString());

            return new ResponseEntity<>(savedCarousel, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("輪播圖創建失敗", e);
            return new ResponseEntity<>("輪播圖創建失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //    查圖片
    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<?> getImage(@PathVariable String filename) {
        Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);

        if (Files.exists(filePath)) {
            try {
                // 不直接創建 FileInputStream，而是使用 Files.newInputStream 來獲取流
                InputStream inputStream = Files.newInputStream(filePath);
                String mimeType = Files.probeContentType(filePath);
                if (mimeType == null) {
                    mimeType = "application/octet-stream";
                }

                // 使用 InputStreamResource 來處理響應
                InputStreamResource resource = new InputStreamResource(inputStream);

                // 返回響應
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(mimeType))
                        .body(resource);
            } catch (IOException e) {
                e.printStackTrace();
                return new ResponseEntity<>("讀取圖片失敗", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("圖片不存在", HttpStatus.NOT_FOUND);
        }
    }

 // 修改
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCarousel(
        @PathVariable Long id,
        @RequestParam(value = "image", required = false) MultipartFile file,
        @RequestParam(value = "imageName", required = false) String imageName) {

        Logger logger = LoggerFactory.getLogger(CarouselController.class);

        try {
        	//查詢輪播圖是否存在
            CarouselEntity existingCarousel = carouselService.getCarouselById(id);

            if (existingCarousel == null) {
                return new ResponseEntity<>("輪播圖不存在", HttpStatus.NOT_FOUND);
            }
            //更新圖名
            if (imageName != null) {
                existingCarousel.setImageName(imageName);
            }
			//處理圖片文件
            if (file != null && !file.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    logger.info("創建上傳目錄：" + uploadPath.toString());
                }
				//上傳新圖片文件
                try (InputStream inputStream = file.getInputStream()) {
                    String fileName = file.getOriginalFilename();
                    Path filePath = uploadPath.resolve(fileName);

                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("文件上傳成功並覆蓋：" + filePath.toString());
                    // 更新輪播圖實體中的圖片名稱
                    existingCarousel.setImage(fileName); // 更新圖片 URL
                } catch (IOException e) {
                    logger.error("文件上傳失敗", e);
                    return new ResponseEntity<>("文件上傳失敗", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            //保存更新到資料庫
            CarouselEntity updatedCarousel = carouselService.saveCarousel(existingCarousel);
            logger.info("輪播圖已更新：" + updatedCarousel.toString());

            return new ResponseEntity<>(updatedCarousel, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("更新輪播圖失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
 // 刪除
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCarousel(@PathVariable Long id) {
        Logger logger = LoggerFactory.getLogger(CarouselController.class);

        try {
        	//查詢輪播圖是否存在
            CarouselEntity carousel = carouselService.getCarouselById(id);
            if (carousel == null) {
            	//404
                return new ResponseEntity<>("輪播圖不存在", HttpStatus.NOT_FOUND);
            }

            // 刪除圖片文件
            Path filePath = Paths.get(UPLOAD_DIR).resolve(carousel.getImage());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("已刪除圖片文件：" + filePath.toString());
            }

            // 刪除資料庫中的實體
            carouselService.deleteCarousel(id);
            return new ResponseEntity<>("輪播圖已刪除成功", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("刪除輪播圖失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
}
