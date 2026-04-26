package com.app.crowdfunding.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*; // Indispensable pour @NotBlank, @Min, etc.
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Le titre est obligatoire")
    @Size(min = 5, max = 100, message = "Le titre doit faire entre 5 et 100 caractères")
    private String title;
    
    @NotBlank(message = "La description est obligatoire")
    @Column(length = 2000)
    private String description;
    
    @NotNull(message = "L'objectif financier est obligatoire")
    @DecimalMin(value = "1.0", message = "L'objectif doit être d'au moins 1 $")
    private BigDecimal goalAmount;
    
    @NotNull
    private BigDecimal currentAmount = BigDecimal.ZERO;
    
    @NotBlank(message = "Veuillez choisir une catégorie")
    private String category;
    
    private String imageUrl;

    public Campaign() {
    }

    // --- GETTERS ET SETTERS ---
    // (Garde tes getters/setters actuels, ils sont parfaits)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getGoalAmount() { return goalAmount; }
    public void setGoalAmount(BigDecimal goalAmount) { this.goalAmount = goalAmount; }
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // --- LOGIQUE MÉTIER ---
    public int getPercentage() {
        if (goalAmount == null || goalAmount.compareTo(BigDecimal.ZERO) <= 0 || currentAmount == null) {
            return 0;
        }
        
        // Calcul sécurisé avec arrondi
        return currentAmount.multiply(new BigDecimal(100))
                            .divide(goalAmount, 0, RoundingMode.HALF_UP)
                            .intValue();
    }
}