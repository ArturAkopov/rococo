package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.UserJson;
import anbrain.qa.rococo.service.grpc.UserGrpcClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(name = "User API", description = "Операции с пользователем")
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserGrpcClient userGrpcClient;

    @Autowired
    public UserController(UserGrpcClient userGrpcClient) {
        this.userGrpcClient = userGrpcClient;
    }

    @Operation(
            summary = "Получить информацию по пользователю",
            description = "Возвращает пользователя"
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

    @Operation(summary = "Обновить данные пользователя")
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
