package replication.test.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceProperties {

    private final Map<String, ReplicationSource> replicationSources = new HashMap<>();

    private String username;
    private String password;
    private String jdbcUrl;
    private String driverClassName;

    @Data
    public static class ReplicationSource {

        private String username;
        private String password;
        private String jdbcUrl;
        private String name;
        private String driverClassName;
    }
}
