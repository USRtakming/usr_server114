package com.usr_server.dome.dto;

import java.util.List;


public class CountryDto {
	private Long id;
    private String name;
    private List<RegionDto> regions; 

    // Getters and Setters
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
    
    public List<RegionDto> getRegions() {
        return regions;
    }

    public void setRegions(List<RegionDto> region) {
        this.regions = regions;
    }
}
