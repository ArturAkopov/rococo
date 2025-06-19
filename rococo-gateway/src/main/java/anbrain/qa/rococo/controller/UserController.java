package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.UserJson;
import anbrain.qa.rococo.service.grpc.UserGrpcClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Tag(name = "User API", description = "Операции с пользователями")
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserGrpcClient userGrpcClient;

    @Autowired
    public UserController(UserGrpcClient userGrpcClient) {
        this.userGrpcClient = userGrpcClient;
    }

    @Operation(
            summary = "Получить информацию о пользователе",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Информация о пользователе",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserJson.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера"
                    )
            }
    )
    @GetMapping
    public UserJson getUser(
            @Parameter(
                    description = "Токен аутентификации (автоматически подставляется из заголовка Authorization)",
                    required = true,
                    hidden = true
            )
            @Nonnull
            @AuthenticationPrincipal Jwt principal
    ) {
        String username = principal.getClaim("sub");
        return userGrpcClient.getUser(username);
    }

    @Operation(
            summary = "Обновить данные пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Обновленные данные пользователя",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserJson.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Невалидные данные пользователя"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Пользователь не найден"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Внутренняя ошибка сервера"
                    )
            }
    )
    @PatchMapping
    public UserJson updateUser(
            @Parameter(
                    description = "Токен аутентификации (автоматически подставляется из заголовка Authorization)",
                    required = true,
                    hidden = true
            )
            @Nonnull
            @AuthenticationPrincipal Jwt principal,

            @Valid @RequestBody UserJson updateRequest) {
        String username = principal.getClaim("sub");
        return userGrpcClient.updateUser(username, updateRequest);
    }
}