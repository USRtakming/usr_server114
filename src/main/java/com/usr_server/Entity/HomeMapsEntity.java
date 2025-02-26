package com.usr_server.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "HomeMaps")
public class HomeMapsEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "regions_id")
	private Long id;
	
	@Column(name = "regions_name")
	private String aname;
	
	@Column(name = "content_text")
	private String content_text;
	
	@Column(name = "HomeMaps_image")
    private String image;
	
	
	//Getters and Setters
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAname() {
		return aname;
	}
	
	public void setAname(String aname) {
		this.aname = aname;
	}
	
	public String getContent_text() {
		return content_text;
	}
	
	public void setContent_text(String content_text) {
		this.content_text = content_text;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
}