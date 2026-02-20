package com.api.account.mapper;

import com.api.account.model.User;
import com.api.account.model.dto.request.RegisterRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    // domain modal(target) and source
    User fromRegisterRequest(RegisterRequest registerRequest);
}