package com.usr_server.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.usr_server.Entity.ReportEntity;
import com.usr_server.Repository.ReportRepository;

@Service
public class ReportService {
	@Autowired
	private ReportRepository ReportRepository;
	
	//取得全部資料
	public List<ReportEntity> getAllReport(){
		return ReportRepository.findAll();
	}
	
	//根據ID查詢資料
	public ReportEntity getReportById(Long id) throws Exception{
			return ReportRepository.findById(id)
					.orElseThrow(()-> new Exception("找不到此年度報告"));
	}

	//更新資料
	public ReportEntity saveReports(ReportEntity report) {
		Logger logger = LoggerFactory.getLogger(ReportService.class);
		logger.info("save保存年度報告：" + report.toString());
		return ReportRepository.save(report);
	}
	
	//刪除
	public void deleteReportS(Long id) {
		//幾查是否存在該連結
		if(ReportRepository.existsById(id)) {
			ReportRepository.deleteById(id);
		}else {
			throw new RuntimeException("年度報告不存在");
		}
	}
	
}
