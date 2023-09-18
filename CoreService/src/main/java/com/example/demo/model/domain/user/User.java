package com.example.demo.model.domain.user;

import com.example.demo.model.constants.Role;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "user")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    @ManyToOne
    @JoinColumn(name="faculty")
    private Faculty faculty;
    @Enumerated(value = EnumType.STRING)
    private Role role;
}
