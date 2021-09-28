package com.example.demo.service;

import com.example.demo.controllers.models.CreateUserRequest;
import com.example.demo.controllers.models.UserResponse;


import javax.validation.constraints.NotNull;
import java.util.List;

public interface UserService {

    @NotNull
    List<UserResponse> findAll();

    @NotNull
    UserResponse findById(@NotNull Integer userId);

    @NotNull
    UserResponse createUser(@NotNull CreateUserRequest request);

    @NotNull
    UserResponse update(@NotNull Integer userId, @NotNull CreateUserRequest request);

    void delete(@NotNull Integer userId);
}
