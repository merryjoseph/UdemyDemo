package com.example.PasswordEmailSecurityDemo.Repository;

import com.example.PasswordEmailSecurityDemo.Entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
// Query methods. name appropriately . find is a keyword. find a record by which column
    // JPA Query. Method name is important

    UserEntity findByEmail(String email);

    UserEntity findByUserId(String id);

    UserEntity findUserByEmailVerificationToken(String token);



//    // Native Query
//    // Method name is not important
//    //SQL with details of the real database tables.
//    //deals with real table names and column names in the database
//    @Query(value = "select * from users u where u.userId = :userId",nativeQuery = true)
//    public UserEntity findDatabaseUserByPublicUserIdField(@Param("userId") String userId);
//
//
//    //countQuery to figure out the total amount of records that will be fetched
//    // countQuery as we have used Pageable
//    @Query(value = "select * from users u where u.emailVerificationStatus = 'true'",
//            countQuery = " select count(*) from users u where u.emailVerificationStatus = 'true'",
//            nativeQuery = true)
//    Page<UserEntity> findAllUsersWithVerifiedEmailAddress(Pageable pageableRequest);
//
//    // Native SQL query with positional parameters
//    // instead of hardcoding values inside queries
//    //1 points to the first parameter passed to the particular method
//    // 2 points to the second parameter passed to the particular method
//    @Query(value = "select * from users u where u.firstName= ?1 and u.lastName=?2",nativeQuery = true)
//    List<UserEntity> findUserByName(String firstname,String lastname);
//
//
//    // Named Parameters
//    // name given inside query should match the string inside param keyword
//    @Query(value = "select * from users u where u.firstName= :fName and u.lastName = :lName",nativeQuery = true)
//    List<UserEntity> findUserByNames(@Param("lName") String lastname,@Param("fName") String firstname);
//
//    // LIKE
//    @Query(value = "select * from users u where u.firstName LIKE %:keyword%",nativeQuery = true)
//    List<UserEntity> findUserByKeyword(@Param("keyword") String keyword);
//
//    @Query(value = "select u.email,u.firstName from users u where u.firstName LIKE %:keyword%",nativeQuery = true)
//    List<Object[]> findUserEmailAndNameByKeyword(@Param("keyword") String keyword);
//
//    //Modifying for delete and update as it changes the database table values
//    //Transactional helps us to do modifications  to db tables so that if some error happens, it is rollbacked
//    // usually it is the service class method that is annotated with @transactional
//    @Transactional
//    @Modifying
//    @Query(value = "update users u set u.emailVerificationStatus=:status where u.userId = :id",
//                nativeQuery = true)
//    void updateUserEmailVerificationStatus(@Param("status") boolean status,
//                                           @Param("id ") String id);
//
//
//    //JPQL database independent
//    //works with entity classes and columns instead of database tables
//    @Query("select user from UserEntity user where user.userId=:id")
//    UserEntity findUserEntityByUserId(@Param("id") String userid);
//
//    @Query("select u.email,u.firstName from UserEntity u where u.userId = :id")
//    List<Object[]> findUserEmailAndNameById(@Param("id") String userid);
//
//    @Transactional
//    @Modifying
//    @Query("update UserEntity u set u.emailVerificationStatus=:status where u.userId = :id")
//    void updateUserEntityEmailVerificationStatus(@Param("status") boolean status,
//                                           @Param("id ") String id);

}
