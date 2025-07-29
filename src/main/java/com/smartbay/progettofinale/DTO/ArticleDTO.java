package com.smartbay.progettofinale.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.smartbay.progettofinale.Models.Category;
import com.smartbay.progettofinale.Models.Image;
import com.smartbay.progettofinale.Models.User;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ArticleDTO {

    private Long id;
    private BigDecimal price;
    private String title;
    private String subtitle;
    private String body;
    private LocalDate publishDate;
    private Boolean isAccepted;
    private User user;
    private Category category;
    private Image image;

}