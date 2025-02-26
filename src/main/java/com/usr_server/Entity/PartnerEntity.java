package com.usr_server.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "USRpartner")
public class PartnerEntity {
//  主鍵
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Long id;
	
//	組織名稱
	@Column(name = "organize_name")
    private String organizeName;
	
//	職位
	@Column(name = "position_name")
    private String positionName;
	
//	夥伴名稱
	@Column(name = "partner_name")
    private String partnerName;
	
//	夥伴照片
	@Column(name = "partner_img")
    private String image;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getOrganizeName() {
		return organizeName;
	}
	public void setOrganizeName(String organizeName) {
		this.organizeName = organizeName;
	}
	
	public String getPositionName() {
		return positionName;
	}
	public void setPositionName(String positionName) {
		this.positionName = positionName;
	}
	
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	
	public String getPartnerImage() {
		return image;
	}
	public void setPartnerImage(String image) {
		this.image = image;
	}


}
