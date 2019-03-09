package example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Configuration
public class ApplicationConfiguration {

    @Value("${port:8080}")
    private String port;

    @Value(value = "${kafka.bootstrap-servers}")
    private String bootstrapServers;

    public String port() {
        return this.port;
    }

    public String bootstrapServers() {
        return this.bootstrapServers;
    }
}
