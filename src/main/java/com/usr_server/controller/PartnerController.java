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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.usr_server.Entity.PartnerEntity;
import com.usr_server.service.PartnerService;

@RestController
@RequestMapping("/api/Partner")
public class PartnerController {
	@Autowired
    private PartnerService PartnerService;
    
    // 保存文件的路徑，可以根據需要進行修改
    private static String UPLOAD_DIR = "src/main/resources/static/uploads/PartnerImage";

    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    static {
        MIME_TYPE_MAP.put("jpg", "image/jpeg");
        MIME_TYPE_MAP.put("jpeg", "image/jpeg");
        MIME_TYPE_MAP.put("png", "image/png");
        MIME_TYPE_MAP.put("gif", "image/gif");
    }
    
    //    查全部
    // http://localhost:8081/api/Partner/allPartner
    @GetMapping("/allPartner")
    public List<PartnerEntity> getAllPartners() {
        List<PartnerEntity> Partners = PartnerService.getAllPartners();
        if (Partners == null || Partners.isEmpty()) {
            System.out.println("未找到輪播數據");
        } else {
            System.out.println("找到輪播數據");
        }
        return Partners;
    }

    //   用id查詢 
    // http://localhost:8081/api/Partner/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getPartnerById(@PathVariable Long id) {
        try {
            PartnerEntity Partner = PartnerService.getPartnerById(id);
            return new ResponseEntity<>(Partner, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); 
            return new ResponseEntity<>("取得輪播失敗", HttpStatus.BAD_REQUEST);
        }
    }
    
    //   創建
    @PostMapping("/create")
    public ResponseEntity<?> createPartner(
    		@RequestParam("organizeName") String organizeName,
    		@RequestParam("positionName") String positionName,
    		@RequestParam("partnerName") String imageName, 
    		@RequestParam("image") MultipartFile file) {
        Logger logger = LoggerFactory.getLogger(PartnerController.class);

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

            // 創建 PartnerEntity 並設置屬性
            PartnerEntity Partner = new PartnerEntity();
            Partner.setOrganizeName(organizeName);
            Partner.setPositionName(positionName);
            Partner.setPartnerName(imageName);
            Partner.setPartnerImage(fileName); // 設置圖片的 URL


            PartnerEntity savedPartner = PartnerService.savePartner(Partner);
            logger.info("夥伴已保存：" + savedPartner.toString());

            return new ResponseEntity<>(savedPartner, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("夥伴創建失敗", e);
            return new ResponseEntity<>("夥伴創建失敗", HttpStatus.INTERNAL_SERVER_ERROR);
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
    public ResponseEntity<?> updatePartner(
        @PathVariable Long id,
        @RequestParam(value = "organizeName", required = false) String organizeName,
		@RequestParam(value = "positionName", required = false) String positionName,
		@RequestParam(value = "partnerName", required = false) String imageName, 
		@RequestParam(value = "image", required = false) MultipartFile file) {

        Logger logger = LoggerFactory.getLogger(PartnerController.class);

        try {
        	//查詢夥伴是否存在
            PartnerEntity existingPartner = PartnerService.getPartnerById(id);

            if (existingPartner == null) {
                return new ResponseEntity<>("夥伴不存在", HttpStatus.NOT_FOUND);
            }
            
            if (organizeName != null) {
                existingPartner.setOrganizeName(organizeName);
            }
            
            if (positionName != null) {
                existingPartner.setPositionName(positionName);
            }
            
            //更新圖名
            if (imageName != null) {
                existingPartner.setPartnerName(imageName);
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
                    // 更新夥伴實體中的圖片名稱
                    existingPartner.setPartnerImage(fileName); // 更新圖片 URL
                } catch (IOException e) {
                    logger.error("文件上傳失敗", e);
                    return new ResponseEntity<>("文件上傳失敗", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
            //保存更新到資料庫
            PartnerEntity updatedPartner = PartnerService.savePartner(existingPartner);
            logger.info("夥伴已更新：" + updatedPartner.toString());

            return new ResponseEntity<>(updatedPartner, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("更新夥伴失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
 // 刪除
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePartner(@PathVariable Long id) {
        Logger logger = LoggerFactory.getLogger(PartnerController.class);

        try {
        	//查詢夥伴是否存在
            PartnerEntity Partner = PartnerService.getPartnerById(id);
            if (Partner == null) {
            	//404
                return new ResponseEntity<>("夥伴不存在", HttpStatus.NOT_FOUND);
            }

            // 刪除圖片文件
            Path filePath = Paths.get(UPLOAD_DIR).resolve(Partner.getPartnerImage());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("已刪除圖片文件：" + filePath.toString());
            }

            // 刪除資料庫中的實體
            PartnerService.deletePartner(id);
            return new ResponseEntity<>("夥伴已刪除成功", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("刪除夥伴失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
