package gzg.momen.urlshortener.utils;

public class Base62Encoder {
    // base62
    private static final char[] BASE62_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public static String encode(long counter) {
        if(counter < 0) {
            throw new IllegalArgumentException("counter cannot be negative");
        }
        long size = counter;

        StringBuilder tinyUrl = new StringBuilder();

        while (size > 0) {
            tinyUrl.append(BASE62_CHARS[(int) (size % 62)]);
            size = size / 62;
        }

        return tinyUrl.toString();

    }
}
