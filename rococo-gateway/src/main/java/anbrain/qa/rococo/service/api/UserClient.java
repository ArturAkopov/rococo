package anbrain.qa.rococo.service.api;

import anbrain.qa.rococo.model.UserJson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class UserClient {
    private static final Logger LOG = LoggerFactory.getLogger(UserClient.class);

    public UserJson getUser(@NonNull String username) {
        try (InputStream inputStream = new ClassPathResource("pageJson/get_user.json").getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<>() {
            });

        } catch (Exception e) {
            LOG.error("Error loading user", e);

        }
        return null;
    }
}
