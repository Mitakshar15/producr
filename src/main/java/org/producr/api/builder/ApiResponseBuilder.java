package org.producr.api.builder;


import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import org.producr.api.dto.BaseApiResponse;
import org.producr.api.dto.Metadata;
import org.producr.api.dto.Status;
import org.producr.api.utils.constants.UserConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class ApiResponseBuilder {

  private final Tracer tracer;

  public BaseApiResponse buildSuccessApiResponse(String statusMessage) {
    return new BaseApiResponse()
        .metadata(new Metadata().timestamp(Instant.now())
            .traceId(null != tracer.currentSpan()
                ? Objects.requireNonNull(tracer.currentSpan()).context().traceId()
                : ""))
        .status(new Status().statusCode(HttpStatus.OK.value()).statusMessage(statusMessage)
            .statusMessageKey(UserConstants.RESPONSE_MESSAGE_KEY_SUCCESS));
  }

}
