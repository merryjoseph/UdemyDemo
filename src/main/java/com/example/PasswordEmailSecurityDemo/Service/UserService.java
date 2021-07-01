package com.example.PasswordEmailSecurityDemo.Service;

import com.example.PasswordEmailSecurityDemo.DTO.AddressDto;
import com.example.PasswordEmailSecurityDemo.DTO.UserDto;
import com.example.PasswordEmailSecurityDemo.Entity.AddressEntity;
import com.example.PasswordEmailSecurityDemo.Entity.UserEntity;
import com.example.PasswordEmailSecurityDemo.Repository.AddressRepository;
import com.example.PasswordEmailSecurityDemo.Repository.UserRepository;
import com.example.PasswordEmailSecurityDemo.Utilities.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    UserEntity userEntity = new UserEntity();

    @Autowired
    EmailService emailService;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    Utils utils;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserDto createUser(UserDto userDto, String url) throws MessagingException, UnsupportedEncodingException {
        if (userRepository.findByEmail(userDto.getEmail()) != null)
            throw new RuntimeException("Email id already present");

        for (int i = 0; i < userDto.getAddressDtos().size(); i++) {
            AddressDto addressDto = userDto.getAddressDtos().get(i);
            addressDto.setUserDto(userDto);
            addressDto.setAddressId(utils.generateAddressId(10));
            userDto.getAddressDtos().set(i, addressDto);
        }
        ModelMapper modelMapper = new ModelMapper();
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);

        String publicUserId = utils.generateUserId(10);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(publicUserId);
        userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
        userEntity.setEmailVerificationStatus(true);

        List<AddressEntity> addressEntities = new ArrayList<AddressEntity>();
        for(int i=0;i<userDto.getAddressDtos().size();i++)
        {
            AddressDto addressDto = userDto.getAddressDtos().get(i);
            AddressEntity addressEntity = modelMapper.map(addressDto,AddressEntity.class);
            addressEntities.add(addressEntity);
        }
        userEntity.setAddressEntities(addressEntities);
        UserEntity storedUserEntity = userRepository.save(userEntity);

        // sending email to user once registered
        this.triggerMail(storedUserEntity,url);

        List<AddressDto> addressDtos = new ArrayList<AddressDto>();
        for(int i=0;i<storedUserEntity.getAddressEntities().size();i++)
        {
            AddressEntity addressEntity = storedUserEntity.getAddressEntities().get(i);
            addressEntity.setUserEntity(storedUserEntity);
            AddressDto addressDto = modelMapper.map(addressEntity,AddressDto.class);
            addressRepository.save(addressEntity);
            addressDtos.add(addressDto);
        }
        UserDto returnUser = modelMapper.map(storedUserEntity, UserDto.class);
        returnUser.setAddressDtos(addressDtos);
        return returnUser;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null)
            throw new UsernameNotFoundException(email);
        //return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
        return new User(userEntity.getEmail(),
                userEntity.getEncryptedPassword(),
                userEntity.getEmailVerificationStatus(),
                true,true,true, new ArrayList<>());
    }

    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    public UserDto getUserByUserId(String id) {
        UserDto returnValue = new UserDto();
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null)
            throw new UsernameNotFoundException(id + " does not exist");
        BeanUtils.copyProperties(userEntity, returnValue);
        return returnValue;
    }

    public UserDto updateUser(String id, UserDto userDto) {

        UserEntity storedValue = userRepository.findByUserId(id);
        if (storedValue == null)
            throw new NullPointerException("User Id not Present");

        storedValue.setFirstName(userDto.getFirstName());
        storedValue.setLastName(userDto.getLastName());
        userRepository.save(storedValue);

        UserDto returnValue = new UserDto();
        BeanUtils.copyProperties(storedValue, returnValue);

        return returnValue;

    }

    public void deleteUser(String id) throws Exception {
        UserEntity userEntity = userRepository.findByUserId(id);
        if (userEntity == null)
            throw new Exception("User does not Exist");
        userRepository.delete(userEntity);
    }

    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue = new ArrayList<UserDto>();

        if (page > 0)
            page -= 1;

        Pageable pageable = PageRequest.of(page, limit);
        Page<UserEntity> userspage = userRepository.findAll(pageable);

        List<UserEntity> users = userspage.getContent();

        for (UserEntity user : users) {
            UserDto userDto = new UserDto();
            BeanUtils.copyProperties(user, userDto);
            returnValue.add(userDto);
        }

        return returnValue;
    }

    public boolean requestPasswordReset(String email) {

        boolean returnValue = false;

        UserEntity userEntity=userRepository.findByEmail(email);
        if(userEntity == null){
            return returnValue;
        }

//        String token = Utils.generatePasswordResetToken(userEntity.getUserId());
//        PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenentity();
//        passwordResetTokenEntity.setToken(token);
//        passwordResetTokenEntity.setUserDetails(userEntity);
//        passwordResetTokenRepository.save(passwordResetTokenEntity);

        return  returnValue;
    }

    public boolean verifyEmailToken(String token) {
        boolean returnValue = false;

        //verify user by token
        UserEntity userEntity = userRepository.findUserByEmailVerificationToken(token);

        if(userEntity != null){
            boolean hastokenExpired = Utils.hasTokenExpired(token);
            if(! hastokenExpired){
                userEntity.setEmailVerificationToken(null);
                userEntity.setEmailVerificationStatus(Boolean.TRUE);
                userRepository.save(userEntity);
                returnValue = true ;
            }
        }
        return returnValue;
    }

    public void triggerMail(UserEntity userEntity,String url) throws MessagingException, UnsupportedEncodingException {
        emailService.sendSimpleEmail(userEntity,url);

    }




}
