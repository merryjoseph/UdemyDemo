package com.example.PasswordEmailSecurityDemo.Security;

import com.example.PasswordEmailSecurityDemo.DTO.UserDto;
import com.example.PasswordEmailSecurityDemo.ModelRequest.UserSignInRequestModel;
import com.example.PasswordEmailSecurityDemo.Service.UserService;
import com.example.PasswordEmailSecurityDemo.SpringApplicationContext;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public AuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException{
        try{
            UserSignInRequestModel creds= new ObjectMapper().readValue(req.getInputStream(),UserSignInRequestModel.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getEmail(),
                            creds.getPassword(),
                            new ArrayList<>()
                    )
            );
        }
        catch (IOException e){
            throw new RuntimeException();
        }
    }

    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth ) throws IOException, ServletException{
        String userName = ((User)auth.getPrincipal()).getUsername();

        String token = Jwts.builder()
                .setSubject(userName)
                .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512,SecurityConstants.getTokenSecret())
                .compact();
        UserService userService = (UserService) SpringApplicationContext.getBean("userService");
        UserDto userDto= null;
        try {
            userDto = userService.getUser(userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        res.addHeader(SecurityConstants.HEADER_STRING,SecurityConstants.TOKEN_PREFIX+token);
        res.addHeader("UserID",userDto.getUserId());

    }
}
