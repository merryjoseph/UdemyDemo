package com.example.PasswordEmailSecurityDemo.Repository;

import com.example.PasswordEmailSecurityDemo.Entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity,Long> {


}
