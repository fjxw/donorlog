package com.donorlog.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contraindications")
public class ContraindicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category; // TEMPORARY or ABSOLUTE

    @Column(nullable = false)
    private int deferralDays; // 0 for absolute

    @Column(length = 1000)
    private String description;

    @Column(length = 1000)
    private String recommendation;

    public ContraindicationEntity() {
    }

    public ContraindicationEntity(String title, String category, int deferralDays, String description, String recommendation) {
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
