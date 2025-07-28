package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String title;

    @Column(nullable = false, length = 100)
    @NotNull
    @Size(max = 100)
    private String subtitle;

    @Column(nullable = false, length = 1000)
    @NotNull
    @Size(max = 1000)
    private String body;

    @Column(nullable = false)
    @NotNull
    @Min(value = 1)
    @Max(value = 1000)
    private BigDecimal price;

    @Column(nullable = true, length = 8)
    @NotNull
    private LocalDate publishDate;

    @Column(nullable = true)
    private Boolean isAccepted;

    public Boolean getIsAccepted() {
        return isAccepted;
    }
    
    public void setIsAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"articles"})
    private User user;

    @ManyToOne
    @JsonIgnoreProperties({"articles"})
    private Category category;

    @OneToOne(mappedBy = "article")
    @JsonIgnoreProperties({"article"})
    private Image image;

    @Override
    public boolean equals(Object obj) {
        Article article = (Article) obj;
        if (title.equals(article.getTitle()) && subtitle.equals(article.getSubtitle()) &&
            body.equals(article.getBody()) && publishDate.equals(article.getPublishDate()) &&
            category.getName().equals(article.getCategory().getName()) && image.getPath().equals(article.getImage().getPath())) {
            return true; 
        }
        return false;
    }
    
}