package com.example.PasswordEmailSecurityDemo.Repository;

import com.example.PasswordEmailSecurityDemo.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
// Query methods. name appropriately . find is a keyword. find a record by which column
    UserEntity findByEmail(String email);

    UserEntity findByUserId(String id);

    UserEntity findUserByEmailVerificationToken(String token);

    @Query("SELECT u FROM users u WHERE u.emailVerificationToken = ?1")
    public UserEntity findByVerificationCode(String code);
}
