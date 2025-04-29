package org.producr.api.config.security.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.producr.api.builder.ApiResponseBuilder;
import org.producr.api.config.security.jwt.JwtTokenUtil;
import org.producr.api.config.security.jwt.UserPrincipal;
import org.producr.api.dtos.AuthResponse;
import org.producr.api.dtos.AuthResponseDto;
import org.producr.api.mapper.UserMgmtMapper;
import org.producr.api.service.interfaces.UserService;
import org.producr.api.utils.constants.UserConstants;
import org.producr.api.utils.enums.AuthProvider;
import org.producr.api.utils.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final JwtTokenUtil jwtTokenUtil;
  private final UserMgmtMapper mapper;
  private final ApiResponseBuilder builder;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException, ServletException {
    clearAuthenticationAttributes(request);
    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

    String token = jwtTokenUtil.generateToken(userPrincipal);
    AuthResponse authResponse =
        mapper.toAuthResponse(builder.buildSuccessApiResponse(UserConstants.AUTH_SUCCESS_MESSAGE));
    AuthResponseDto authResponseDto = new AuthResponseDto();
    authResponseDto.setToken(token);
    authResponseDto.setUserName(userPrincipal.getUsername());
    authResponseDto.setProvider(AuthProvider.GOOGLE.toString());
    authResponse.setData(authResponseDto);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Op
    String jsonResponse = objectMapper.writeValueAsString(authResponse);
    // Set response headers and write response
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(jsonResponse);
    response.getWriter().flush();
  }
}
