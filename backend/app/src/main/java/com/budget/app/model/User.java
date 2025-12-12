package com.budget.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
// import lombok.NoArgsConstructor;

import java.util.ArrayList;

import jakarta.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Entity
@Table(name = "user_table")
public class User {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "user_id", updatable = false, nullable = false)
   private Long userId;

   @Column(name = "user_name")
   private String userName;

   @Column(name = "password")
   private String password;

   @Column(name = "email")
   private String email;

   @OneToMany(mappedBy = "user")
   private List<UserLogin> logins = new ArrayList<>();

   public User(){
   }

   public User(String userName, String password, String email) {
       this.userName = userName;
       this.password = password;
       this.email = email;
   }

}
