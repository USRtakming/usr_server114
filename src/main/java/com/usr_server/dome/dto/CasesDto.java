package com.usr_server.dome.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;



public class CasesDto {

	    private Long id;
	    private String title;
	    private String image;
	    private String pdfLink;
	    private String content_date;
	    private LocalDate date;
	    
	    public Long getId() {
	        return id;
	    }

	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getTitle() {
	        return title;
	    }

	    public void setTitle(String title) {
	        this.title = title;
	    }

	    public String getImage() {
	        return image;
	    }

	    public void setImage(String image) {
	        this.image = image;
	    }
	    
	    public String getContentDate() {
	        return image;
	    }

	    public void setContentDate(String content_date) {
	        this.content_date = content_date;
	    }

	    public String getPdfLink() {
	        return pdfLink;
	    }

	    public void setPdfLink(String pdfLink) {
	        this.pdfLink = pdfLink;
	    }

	    public LocalDate getDate() {
	        return date;
	    }

	    public void setDate(LocalDate date) {
	        this.date = date;
	    }
		
		
}
