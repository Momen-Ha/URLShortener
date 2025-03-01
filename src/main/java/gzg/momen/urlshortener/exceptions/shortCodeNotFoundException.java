package gzg.momen.urlshortener.exceptions;

public class shortCodeNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public shortCodeNotFoundException(String message) {
        super(message);
    }
}
