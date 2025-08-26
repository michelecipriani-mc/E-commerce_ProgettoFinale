package com.smartbay.progettofinale.Models;

import java.math.BigDecimal;
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
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private BigDecimal balance;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_roles",
        joinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")},
        inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", referencedColumnName = "ID")}
    )

    private List<Role> roles = new ArrayList<>();

    // Collegamento con le richieste di carriera
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<CareerRequest> careerRequests = new ArrayList<>();
    
}
