package org.producr.api.mapper;


import org.mapstruct.Mapper;
import org.producr.api.data.domain.user.User;
import org.producr.api.dto.BaseApiResponse;
import org.producr.api.dtos.AuthResponse;
import org.producr.api.dtos.SignUpRequest;
import org.producr.api.dtos.UserMgmtBaseApiResponse;

@Mapper(componentModel = "spring")
public interface UserMgmtMapper {

  User toUserEntity(SignUpRequest signUpRequest);

  AuthResponse toAuthResponse(BaseApiResponse apiResponse);


  UserMgmtBaseApiResponse toUserMgmtBaseApiResponse(BaseApiResponse apiResponse);
}
