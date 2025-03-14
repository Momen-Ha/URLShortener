package gzg.momen.urlshortener.exceptions;

public class ShortCodeNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ShortCodeNotFoundException(String message) {
        super(message);
    }
}
