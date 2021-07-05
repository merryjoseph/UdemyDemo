package com.example.PasswordEmailSecurityDemo.Repository;

import com.example.PasswordEmailSecurityDemo.Entity.AddressEntity;
import com.example.PasswordEmailSecurityDemo.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository  extends JpaRepository<AddressEntity,Long> {


   // Iterable<AddressEntity> findAllByUserEntity(UserEntity userEntity);

    AddressEntity findByAddressId(String addressid);
}
