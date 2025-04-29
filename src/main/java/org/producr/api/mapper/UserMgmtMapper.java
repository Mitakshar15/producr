package org.producr.api.mapper;


import org.mapstruct.Mapper;
import org.producr.api.data.domain.user.User;
import org.producr.api.dto.BaseApiResponse;
import org.producr.api.dtos.*;

@Mapper(componentModel = "spring")
public interface UserMgmtMapper {

  User toUserEntity(SignUpRequest signUpRequest);

  AuthResponse toAuthResponse(BaseApiResponse apiResponse);


  UserMgmtBaseApiResponse toUserMgmtBaseApiResponse(BaseApiResponse apiResponse);

  UserProfileResponse toUserProfileResponse(BaseApiResponse baseApiResponse);

  UserProfileDto toUserProfileDto(User user);
}
