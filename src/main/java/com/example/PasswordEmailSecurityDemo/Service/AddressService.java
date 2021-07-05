package com.example.PasswordEmailSecurityDemo.Service;

import com.example.PasswordEmailSecurityDemo.DTO.AddressDto;
import com.example.PasswordEmailSecurityDemo.Entity.AddressEntity;
import com.example.PasswordEmailSecurityDemo.Entity.UserEntity;
import com.example.PasswordEmailSecurityDemo.Repository.AddressRepository;
import com.example.PasswordEmailSecurityDemo.Repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    public List<AddressDto> getAddresses(String id) {

        List<AddressDto> returnValue = new ArrayList<AddressDto>();

        UserEntity userEntity = userRepository.findByUserId(id);
        if(userEntity == null) throw new UsernameNotFoundException("User Does not Exixt");

//        Iterable<AddressEntity> addressEntities = addressRepository.findAllByUserEntity(userEntity);
        for(AddressEntity addressEntity:userEntity.getAddressEntities()){
            returnValue.add( new ModelMapper().map(addressEntity,AddressDto.class));
       }

        return returnValue;

    }

    public AddressDto getAddress(String addressid) {
        AddressDto returnValue = new AddressDto();

        AddressEntity addressEntity = addressRepository.findByAddressId(addressid);
        if(addressEntity != null){
            returnValue = new ModelMapper().map(addressEntity,AddressDto.class);
        }
        return returnValue;
    }
}
