package gzg.momen.urlshortener.utils;

public class URLEncoder {
    // base62
    private static final char[] charSet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    public static String encode(long counter) {

        long size = counter;

        StringBuilder tinyUrl = new StringBuilder();

        while (size > 0) {
            tinyUrl.append(charSet[(int) (size % 62)]);
            size = size / 62;
        }

        return tinyUrl.toString();

    }
}
