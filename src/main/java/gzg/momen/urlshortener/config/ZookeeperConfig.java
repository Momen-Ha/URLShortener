package gzg.momen.urlshortener.config;

import gzg.momen.urlshortener.constants.ZkConstants;
import gzg.momen.urlshortener.utils.StringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ZookeeperConfig {

    private final ZkConstants zkConstants;

    public ZookeeperConfig(ZkConstants zkConstants) {
        this.zkConstants = zkConstants;
    }

    @Bean
    public ZkClient zkClient() {
        log.info("Zookeeper hostPort: " + zkConstants.getHost());
        if (zkConstants.getHost() == null || zkConstants.getHost().isBlank()) {
            throw new IllegalStateException("Zookeeper hostPort is null!");
        }
        return new ZkClient(zkConstants.getHost(), zkConstants.getSessionTimeout(),
                zkConstants.getTimeout(), new StringSerializer());
    }
}
