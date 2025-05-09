package org.producr.api.builder;


import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.producr.api.data.domain.user.User;
import org.producr.api.dto.BaseApiResponse;
import org.producr.api.dto.Metadata;
import org.producr.api.dto.Status;
import org.producr.api.dtos.UserProfileDto;
import org.producr.api.mapper.UserMgmtMapper;
import org.producr.api.utils.constants.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApiResponseBuilder {

  private final Tracer tracer;
  private final UserMgmtMapper mapper;

  public BaseApiResponse buildSuccessApiResponse(String statusMessage) {
    return new BaseApiResponse()
        .metadata(new Metadata().timestamp(Instant.now())
            .traceId(null != tracer.currentSpan()
                ? Objects.requireNonNull(tracer.currentSpan()).context().traceId()
                : ""))
        .status(new Status().statusCode(HttpStatus.OK.value()).statusMessage(statusMessage)
            .statusMessageKey(Constants.RESPONSE_MESSAGE_KEY_SUCCESS));
  }

  public UserProfileDto buildUserProfileData(User user) {
    UserProfileDto dto = mapper.toUserProfileDto(user);
    Hibernate.initialize(user.getTracks());
    dto.setNoOfTracks(user.getTracks().size());
    return dto;
  }
}
