package com.usr_server.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.usr_server.Entity.PartnerEntity;
import com.usr_server.Repository.PartnerRepository;

@Service
public class PartnerService {

	@Autowired
    private PartnerRepository PartnerRepository;
	
	
	// 取得全部資料
    public List<PartnerEntity> getAllPartners(){
        return PartnerRepository.findAll();
    }
    
    // 根據ID查詢資料
    public PartnerEntity getPartnerById(Long id) throws Exception{
        return PartnerRepository.findById(id)
            .orElseThrow(() -> new Exception("找不到此輪播圖"));
    }
    
 // 創建一筆新資料
    public PartnerEntity createPartner(PartnerEntity request) {
        PartnerEntity Partner = new PartnerEntity();
        Partner.setOrganizeName(request.getOrganizeName());
        Partner.setPositionName(request.getPositionName());
        Partner.setPartnerName(request.getPartnerName());
        Partner.setPartnerImage(request.getPartnerImage());
        return PartnerRepository.save(Partner);
    }
    
 // 更新資料
    public PartnerEntity savePartner(PartnerEntity Partner){
    	Logger logger = LoggerFactory.getLogger(PartnerService.class);
        logger.info("save保存輪播圖: " + Partner.toString());
        return PartnerRepository.save(Partner);
    	
    }

    
   
//    刪除
    public void deletePartner(Long id) {
        // 檢查是否存在該輪播圖
        if (PartnerRepository.existsById(id)) {
            PartnerRepository.deleteById(id);
        } else {
            throw new RuntimeException("輪播圖不存在");
        }
    }
}
