package anbrain.qa.rococo.service.api;

import anbrain.qa.rococo.model.PaintingJson;
import anbrain.qa.rococo.model.page.RestPage;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Component
public class PaintingClient {
    private static final Logger LOG = LoggerFactory.getLogger(PaintingClient.class);

    public RestPage<PaintingJson> getAll(@Nullable String name, @NonNull Pageable pageable) {
        try (InputStream inputStream = new ClassPathResource("pageJson/get_all_painting.json").getInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            List<PaintingJson> paintings = mapper.readValue(inputStream, new TypeReference<>() {});

            // Рассчитываем, какие элементы должны быть на текущей странице
            int start = (int) pageable.getOffset();
            int end = Math.min((start + pageable.getPageSize()), paintings.size());
            List<PaintingJson> pageContent = paintings.subList(start, end);

            return new RestPage<>(
                    pageContent,
                    pageable,
                    paintings.size()
            );
        } catch (Exception e) {
            LOG.error("Error loading paintings", e);
            return new RestPage<>(Collections.emptyList(), pageable, 0);
        }
    }
}
