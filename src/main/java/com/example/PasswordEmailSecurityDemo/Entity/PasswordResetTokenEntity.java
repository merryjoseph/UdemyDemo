package com.example.PasswordEmailSecurityDemo.Entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity(name="password_reset_tokens")
public class PasswordResetTokenEntity implements Serializable {

    private static final long serialVersionUID = -1579251285552692353L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserEntity getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserEntity userDetails) {
        this.userDetails = userDetails;
    }

    @OneToOne
    @JoinColumn(name = "user_id")
    private UserEntity userDetails;

}
