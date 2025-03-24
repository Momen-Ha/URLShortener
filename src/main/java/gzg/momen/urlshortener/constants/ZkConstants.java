package gzg.momen.urlshortener.constants;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "zookeeper")
public class ZkConstants {
    private String host;
    private int timeout;
    private String nodePath;
    private long rangeLength;
    private int maxRetry;
    private int sessionTimeout;

}
