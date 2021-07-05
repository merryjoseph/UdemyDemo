package com.example.PasswordEmailSecurityDemo.Service;

import com.example.PasswordEmailSecurityDemo.Entity.PasswordResetTokenEntity;
import com.example.PasswordEmailSecurityDemo.Entity.UserEntity;
import com.example.PasswordEmailSecurityDemo.Security.AppProperties;
import com.example.PasswordEmailSecurityDemo.SpringApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleEmailForAccountActivation(UserEntity userEntity, String url) throws MessagingException, UnsupportedEncodingException {
        String toAddress = userEntity.getEmail();
        String fromAddress = "merryjoseph00@gmail.com";
        String senderName = "W3 Schools";
        String subject = "Please verify your email";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", userEntity.getFirstName());
        String verifyURL = url + "/users/verify?code=" + userEntity.getEmailVerificationToken();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);

       System.out.println("Mail Sent.............");
    }



    public void sendSimpleEmailForPasswordReset(
            PasswordResetTokenEntity passwordResetTokenEntity, UserEntity userEntity,String url)
            throws MessagingException, UnsupportedEncodingException {

        String toAddress = passwordResetTokenEntity.getUserDetails().getEmail();
        String fromAddress = "merryjoseph00@gmail.com";
        String senderName = "W3 Schools";
        String subject = "Please reset your password here";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to reset your password:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">RESET</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";


        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", passwordResetTokenEntity.getUserDetails().getFirstName());
        String resetURL = url + "/users/"+userEntity.getUserId()+"/resetpassword?token=" + passwordResetTokenEntity.getToken();

        content = content.replace("[[URL]]", resetURL);

        helper.setText(content, true);

        mailSender.send(message);

        System.out.println("Password Reset Mail Sent.............");
    }
}
