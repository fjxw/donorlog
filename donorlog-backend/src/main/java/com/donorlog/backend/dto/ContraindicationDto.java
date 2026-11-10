package com.donorlog.backend.dto;

public class ContraindicationDto {
    private Long id;
    private String title;
    private String category;
    private int deferralDays;
    private String description;
    private String recommendation;

    public ContraindicationDto() {
    }

    public ContraindicationDto(Long id, String title, String category, int deferralDays, String description, String recommendation) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.deferralDays = deferralDays;
        this.description = description;
        this.recommendation = recommendation;
    }

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDeferralDays() {
        return deferralDays;
    }

    public void setDeferralDays(int deferralDays) {
        this.deferralDays = deferralDays;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
