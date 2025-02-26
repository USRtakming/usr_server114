package com.usr_server.controller;

import java.io.File;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
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

import com.usr_server.Entity.ReportEntity;
import com.usr_server.service.ReportService;

@RestController
@RequestMapping("/api/report")
public class ReportController {

    @Autowired
    private ReportService reportService;
    
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    
    private static final String UPLOAD_DIR = "src/main/resources/static/uploadsFile/report";
    private static final Map<String, String> MIME_TYPE_MAP = new HashMap<>();
    
    static {
        MIME_TYPE_MAP.put("pdf", "application/pdf");
        MIME_TYPE_MAP.put("doc", "application/msword");
        MIME_TYPE_MAP.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
    }

    // 獲取全部資料
    @GetMapping("/allReport")
    public List<ReportEntity> getAllReport() {
        List<ReportEntity> reports = reportService.getAllReport();
        if (reports == null || reports.isEmpty()) {
            logger.info("未找到年度報告資料");
        } else {
            logger.info("找到全部年度報告資料");
        }
        return reports;
    }

    // 獲取特定ID資料
    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable Long id) {
        try {
            ReportEntity report = reportService.getReportById(id);
            return new ResponseEntity<>(report, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("找不到此ID的年度報告", e);
            return new ResponseEntity<>("找不到此ID的年度報告", HttpStatus.BAD_REQUEST);
        }
    }

    // 上傳報告資料
    @PostMapping("/create")
    public ResponseEntity<Object> createReport(
    		@RequestParam String year,
            @RequestParam("file") MultipartFile file) {
        try {
            // 確保目錄存在
            ensureUploadDirectoryExists();

            if (file.isEmpty()) {
                logger.error("文件為空"); 
                return new ResponseEntity<>("請選擇要上傳的文件", HttpStatus.BAD_REQUEST);
            }

            String fileName = file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("文件上傳成功並覆蓋：" + filePath.toString());

                // 保存報告資料
                ReportEntity report = new ReportEntity();
                report.setName(fileName);
                report.setYear(year);
                report.setFilePath(filePath.toString());

                // 調用服務層保存資料庫
                ReportEntity savedReport = reportService.saveReports(report);
                logger.info("年度報告已保存：" + savedReport.toString());
                return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
            }
        } catch (IOException e) {
            logger.error("年度報告創建失敗", e);
            return new ResponseEntity<>("檔案上傳失敗: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 更新報告資料並處理檔案
 // 更新報告資料並處理檔案
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(@PathVariable Long id,
            @RequestParam(value = "year", required = false) String year, @RequestParam(value = "file", required = false) MultipartFile file) {

        try {
            ReportEntity existingReport = reportService.getReportById(id);
            if (existingReport == null) {
                return new ResponseEntity<>("年度報告資料不存在", HttpStatus.NOT_FOUND);
            }

            if (year != null) {
                existingReport.setYear(year);
            }

            if (file != null && !file.isEmpty()) {
                // 刪除舊文件
                Path oldFilePath = Paths.get(UPLOAD_DIR).resolve(existingReport.getFilePath());
                if (Files.exists(oldFilePath)) {
                    Files.delete(oldFilePath);
                    logger.info("刪除舊檔案：" + oldFilePath);
                }

                // 使用時間戳來避免檔案名稱重複
                String newFileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                Path newFilePath = Paths.get(UPLOAD_DIR).resolve(newFileName);
                try (InputStream inputStream = file.getInputStream()) {
                    Files.copy(inputStream, newFilePath, StandardCopyOption.REPLACE_EXISTING);
                    existingReport.setFilePath(newFilePath.toString());  // 設置檔案路徑
                    existingReport.setName(newFileName);  // 設置檔案名稱
                    logger.info("文件上傳成功並覆蓋：" + newFilePath.toString());
                } catch (IOException e) {
                    logger.error("文件上傳失敗", e);
                    return new ResponseEntity<>("文件上傳失敗", HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }

            // 更新報告資料
            ReportEntity updatedReport = reportService.saveReports(existingReport);
            logger.info("年度報告資料已更新：" + updatedReport.toString());
            return new ResponseEntity<>(updatedReport, HttpStatus.OK);

        } catch (Exception e) {
            logger.error("更新年度報告失敗", e);
            return new ResponseEntity<>("更新年度報告失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 刪除報告
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable Long id) {
        try {
            ReportEntity report = reportService.getReportById(id);
            if (report == null) {
                return new ResponseEntity<>("年度報告不存在", HttpStatus.NOT_FOUND);
            }

            // 刪除圖片文件
            Path filePath = Paths.get(UPLOAD_DIR).resolve(report.getFilePath());
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                logger.info("已刪除檔案文件：" + filePath.toString());
            }

            reportService.deleteReportS(id);
            return new ResponseEntity<>("年度報告已刪除成功", HttpStatus.OK);

        } catch (Exception e) {
            logger.error("刪除年度報告失敗", e);
            return new ResponseEntity<>("刪除年度報告失敗", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 確保上傳目錄存在
    private void ensureUploadDirectoryExists() throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            logger.info("創建上傳目錄：" + uploadPath.toString());
        }
    }
    
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws Exception {
        // 檔案存放路徑
        Path filePath = Paths.get(UPLOAD_DIR + "\\" + filename);
        Resource resource = new UrlResource(filePath.toUri());
        System.out.println("檔案路徑：" + filePath.toString());  // 打印出檔案路徑

        if (resource.exists()) {
            // 設定檔案名稱和下載標頭
            String contentDisposition = "attachment; filename=\"" + resource.getFilename() + "\"";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .body(resource);
        } else {
            // 如果檔案不存在
            return ResponseEntity.notFound().build();
        }
    }




}
