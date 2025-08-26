package com.smartbay.progettofinale.Models;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;
    
    // Collegamento con le richieste di carriera
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    private List<CareerRequest> careerRequests = new ArrayList<>();
}
