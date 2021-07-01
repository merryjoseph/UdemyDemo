package com.example.PasswordEmailSecurityDemo.Controller;

import com.example.PasswordEmailSecurityDemo.DTO.AddressDto;
import com.example.PasswordEmailSecurityDemo.DTO.UserDto;
import com.example.PasswordEmailSecurityDemo.Exceptions.UserServiceExceptions;
import com.example.PasswordEmailSecurityDemo.ModelRequest.AddressRequestModel;
import com.example.PasswordEmailSecurityDemo.ModelRequest.PasswordResetRequestModel;
import com.example.PasswordEmailSecurityDemo.ModelRequest.UserSignUpRequestModel;
import com.example.PasswordEmailSecurityDemo.ModelResponse.*;
import com.example.PasswordEmailSecurityDemo.Security.SecurityConstants;
import com.example.PasswordEmailSecurityDemo.Service.AddressService;
import com.example.PasswordEmailSecurityDemo.Service.UserService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressService;

    // to respond back in xml/json by default
    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    // @GetMapping(path = "/{id}")
    public UserSignUpResponseModel getUser(@PathVariable String id) {
        UserDto userDto = userService.getUserByUserId(id);
        UserSignUpResponseModel returnValue = new UserSignUpResponseModel();

        BeanUtils.copyProperties(userDto, returnValue);
        return returnValue;
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserSignUpResponseModel createUser(
            @RequestBody UserSignUpRequestModel userSignUpRequestModel,
            HttpServletRequest request) throws Exception {

        UserSignUpResponseModel returnValue = new UserSignUpResponseModel();
        if (userSignUpRequestModel.getEmail().isEmpty())
            throw new NullPointerException("The object is null ");

        ModelMapper modelMapper = new ModelMapper();
        UserDto userDto = modelMapper.map(userSignUpRequestModel, UserDto.class);

        List<AddressDto> addressDtos = new ArrayList<AddressDto>();
        for (int i = 0; i < userSignUpRequestModel.getAddresses().size(); i++) {
            AddressDto addressDto = modelMapper.map(userSignUpRequestModel.getAddresses().get(i), AddressDto.class);
            addressDto.setUserDto(userDto);
            addressDtos.add(addressDto);
        }

        userDto.setAddressDtos(addressDtos);
        UserDto createdUser = userService.createUser(userDto,getSiteURL(request));

        List<AddressResponseModel> addressResponseModels = new ArrayList<AddressResponseModel>();
        for (int i = 0; i < createdUser.getAddressDtos().size(); i++) {
            AddressResponseModel addressResponseModel = modelMapper.map(createdUser.getAddressDtos().get(i), AddressResponseModel.class);
            addressResponseModels.add(addressResponseModel);
        }

        returnValue = modelMapper.map(createdUser, UserSignUpResponseModel.class);
        returnValue.setAddressResponseModels(addressResponseModels);
        return returnValue;
    }

    @PutMapping(path = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserSignUpResponseModel updateUser(@PathVariable String id, @RequestBody UserSignUpRequestModel userSignUpRequestModel) {
        UserSignUpResponseModel userSignUpResponseModel = new UserSignUpResponseModel();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userSignUpRequestModel, userDto);

        UserDto createdUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(createdUser, userSignUpResponseModel);

        return userSignUpResponseModel;

    }

    @DeleteMapping(path = "/{id}")
    public OperationModel deleteUser(@PathVariable String id) throws Exception {
        userService.deleteUser(id);
        OperationModel returnValue = new OperationModel();
        returnValue.setOperationName(RequestOperationName.DELETE.name());
        returnValue.setOperationStatus(RequestOperationStatus.SUCCESS.name());
        return returnValue;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<UserSignUpResponseModel> getUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "3") int limit) {
        List<UserSignUpResponseModel> returnValue = new ArrayList<UserSignUpResponseModel>();

        List<UserDto> users = userService.getUsers(page, limit);

        for (UserDto user : users) {
            UserSignUpResponseModel userSignUpResponseModel = new UserSignUpResponseModel();
            BeanUtils.copyProperties(user, userSignUpResponseModel);
            returnValue.add(userSignUpResponseModel);
        }

        return returnValue;
    }

    // protocol://domainname:port/applicationcontext/rootresource/id/endpoint
    //http://localhost:8080/passwordemailsecuritydemo/users/dfdrfesfdsv/addresses
    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public CollectionModel<AddressResponseModel> getUserAddresses(@PathVariable String id) {
        List<AddressDto> addressDtos = addressService.getAddresses(id);
        List<AddressResponseModel> returnValue = new ArrayList<AddressResponseModel>();


        if (addressDtos != null && !addressDtos.isEmpty()) {
            Type listType = new TypeToken<List<AddressResponseModel>>() {}.getType();
            returnValue = new ModelMapper().map(addressDtos, listType);

            for(AddressResponseModel addressResponseModel:returnValue){
                Link selfLink = WebMvcLinkBuilder.linkTo(
                                WebMvcLinkBuilder.methodOn(UserController.class)
                                .getUserAddress(id,addressResponseModel.getAddressId()))
                                .withSelfRel();
                addressResponseModel.add(selfLink);
            }
        }

        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(id).withRel("user");
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserAddresses(id))
                        .withSelfRel();
        return CollectionModel.of(returnValue,userLink,selfLink);
    }

    @GetMapping(path = "/{userid}/addresses/{addressid}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public EntityModel<AddressResponseModel> getUserAddress(@PathVariable String userid,
                                                           @PathVariable String addressid) {

        AddressDto addressDto = addressService.getAddress(addressid);
        AddressResponseModel returnValue  = new ModelMapper().map(addressDto,AddressResponseModel.class);

        // This will fetch the link - http//localhost:8080/passwordemailsecuritydemo/users/<userId>
        Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userid).withRel("user");
//        Link userAddresses = WebMvcLinkBuilder.linkTo(UserController.class)
//                .slash(userid)
//                .slash("addresses")
//                .withRel("addresses");
//        Link selfLink = WebMvcLinkBuilder.linkTo(UserController.class)
//                .slash(userid)
//                .slash("addresses")
//                .slash(addressid)
//                .withSelfRel();
        Link userAddresses = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserAddresses(userid))
                        .withRel("addresses");
        Link selfLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class)
                        .getUserAddress(userid,addressid))
                .withSelfRel();

//        returnValue.add(userLink);
//        returnValue.add(userAddresses);
//        returnValue.add(selfLink);

       return EntityModel.of(returnValue, Arrays.asList(userLink,userAddresses,selfLink));

        //return returnValue;
    }

    @PostMapping(path="/password-reset-request",
                consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
                produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationModel operationModel(
            @RequestBody PasswordResetRequestModel passwordResetRequestModel) throws Exception {

        OperationModel returnValue = new OperationModel();

        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());

        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationStatus(RequestOperationStatus.ERROR.name());

        if(operationResult)
        {
            returnValue.setOperationStatus(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }

    //    http://localhost:8080/passwordemailsecuritydemo/users/email-verification?token=asdsdfsd
    @GetMapping(path = "/verify",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationModel verifyEmailToken(@RequestParam(value = "code") String token) {
        System.out.println("Inside email verify");
        OperationModel returnValue = new OperationModel();
        returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());

        boolean isVerified=userService.verifyEmailToken(token);
        if(isVerified){
            returnValue.setOperationStatus(RequestOperationStatus.SUCCESS.name());
        }
        else{
            returnValue.setOperationStatus(RequestOperationStatus.ERROR.name());
        }

        return returnValue;
    }

    private String getSiteURL(HttpServletRequest request) {
        String siteURL = request.getRequestURL().toString();
        return siteURL.replace(request.getServletPath(), "");
    }


}