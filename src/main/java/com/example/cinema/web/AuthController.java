package com.example.cinema.web;

import com.example.cinema.model.dto.auth.AuthenticationRequestDto;
import com.example.cinema.model.dto.auth.AuthenticationResponseDto;
import com.example.cinema.model.dto.user.UserDto;
import com.example.cinema.model.entities.user.User;
import com.example.cinema.model.entities.user.token.RegistrationToken;
import com.example.cinema.model.errors.ApiError;
import com.example.cinema.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;

@Tag(name = "Authentication API")
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private final ModelMapper modelMapper;
    private final AuthService authService;

    @Operation(summary = "Sign up", description = "Returns email confirmation token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully saved"),
            @ApiResponse(responseCode = "400", description = "Isn't valid or already exists", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User data")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public RegistrationToken signup(@Valid @RequestBody UserDto userDto) {
        String host = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String tokenConfirmationUrl = host + "/api/auth/confirm";

        User user = modelMapper.map(userDto, User.class);

        return authService.registerClient(user, tokenConfirmationUrl);
    }

    @Operation(summary = "Log in", description = "Returns JWT token and user data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Email or password are not valid", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "User wasn't found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "403", description = "Bad credentials", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            )),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User credentials")
    @PostMapping("/login")
    public AuthenticationResponseDto login(@Valid @RequestBody AuthenticationRequestDto request) {
        return authService.authenticateUser(request);
    }


    @Operation(summary = "Confirm email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Email already confirmed or token expired. See response body", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            )),
            @ApiResponse(responseCode = "404", description = "Token wasn't found", content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ApiError.class)
            ))
    })
    @GetMapping("/confirm")
    public void confirmToken(
            @Parameter(name = "Confirmation token")
            @RequestParam("token") String token
    ) {
        authService.confirmRegistration(token);
    }
}
