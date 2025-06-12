package anbrain.qa.rococo.controller;

import anbrain.qa.rococo.model.UserJson;
import anbrain.qa.rococo.service.api.UserClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "Операции с пользователем")
@RestController
@RequestMapping("api/user")
public class UserController {

    private final UserClient userClient;

    @Autowired
    public UserController(UserClient userClient) {
        this.userClient = userClient;
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
        return userClient.getUser(username);
    }
}
