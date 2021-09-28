package com.example.demo.service;

import com.example.demo.UserRepository;
import com.example.demo.controllers.models.AddressResponse;
import com.example.demo.controllers.models.CreateAddressRequest;
import com.example.demo.controllers.models.CreateUserRequest;
import com.example.demo.controllers.models.UserResponse;
import com.example.demo.data.Address;
import com.example.demo.data.User;
import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Stream.ofNullable;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static UserRepository userRepository;

    @NotNull
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::buildUserResponse)
                .collect(Collectors.toList());
    }

    @NotNull
    @Transactional(readOnly = true)
    private UserResponse buildUserResponse(@NotNull User user) {
        return new UserResponse()
                .setId(user.getId())
                .setLogin(user.getLogin())
                .setAge(user.getAge())
                .setFirstName(user.getFirstName())
                .setMiddleName(user.getMiddleName())
                .setLastName(user.getLastName())
                .setAddress(new AddressResponse()
                        .setCity(user.getAddress().getCity())
                        .setBuilding(user.getAddress().getBuilding())
                        .setStreet(user.getAddress().getStreet()));
    }

    @NotNull
    @Override
    @Transactional
    public UserResponse findById(@NotNull Integer userId) {
        return userRepository.findById(userId)
                .map(this::buildUserResponse)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " is not found"));
    }

    public UserResponse createUser(@NotNull CreateUserRequest request) {
        User user = buildUserRequest(request);
        return buildUserResponse(userRepository.save(user));
    }

    @NotNull
    private User buildUserRequest(@NotNull CreateUserRequest request) {
        return new User()
                .setLogin(request.getLogin())
                .setAge(request.getAge())
                .setFirstName(request.getFirstName())
                .setMiddleName(request.getMiddleName())
                .setLastName(request.getLastName())
                .setAddress(new Address()
                        .setCity(request.getAddress().getCity())
                        .setBuilding(request.getAddress().getBuilding())
                        .setStreet(request.getAddress().getStreet()));
    }

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", foreignKey = @ForeignKey(name = "fk_users_address_id"))
    private Address address;

    @NotNull
    @Override
    @Transactional
    public UserResponse update(@NotNull Integer userId, @NotNull CreateUserRequest request) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " is not found"));
        userUpdate(user, request);
        return buildUserResponse(userRepository.save(user));
    }

    private void userUpdate(@NotNull User user, @NotNull CreateUserRequest request) {
        ofNullable(request.getLogin()).map(user::setLogin);
        ofNullable(request.getFirstName()).map(user::setFirstName);
        ofNullable(request.getMiddleName()).map(user::setMiddleName);
        ofNullable(request.getLastName()).map(user::setLastName);
        ofNullable(request.getAge()).map(user::setAge);

        CreateAddressRequest addressRequest = request.getAddress();
        if (addressRequest != null) {
            ofNullable(addressRequest.getBuilding()).map(user.getAddress()::setBuilding);
            ofNullable(addressRequest.getStreet()).map(user.getAddress()::setStreet);
            ofNullable(addressRequest.getCity()).map(user.getAddress()::setCity);
        }
    }

    @Override
    @Transactional
    public void delete(@NotNull Integer userId) {
        userRepository.deleteById(userId);
    }
}