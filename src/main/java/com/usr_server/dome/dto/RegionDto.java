package com.usr_server.dome.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class RegionDto {
    private Long id;
    private String name;
    private List<CasesDto> cases= new ArrayList<>(); 
    
    // Getters 和 Setters
    
    public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public List<CasesDto> getCases() {
        return cases;
    }

    public void setCases(List<CasesDto> cases) {
        this.cases = cases;
    }
}

