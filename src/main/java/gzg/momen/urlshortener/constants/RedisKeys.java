package gzg.momen.urlshortener.constants;

public enum RedisKeys {
    URL("urls::"),
    HYPERLOGLOG(":clicks"),
    BLOOMFILTER("urls:bloomfilter");

    private final String key;

    RedisKeys(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
