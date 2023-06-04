package inc.evil.cnsorderservice.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

@ConfigurationProperties(prefix = "api")
public record ClientProperties(
        @NotNull
        URI catalogServiceUri
) {
}
