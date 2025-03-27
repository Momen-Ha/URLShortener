package gzg.momen.urlshortener.exceptions;


import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.KeeperException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShortCodeNotFoundException.class)
    public ResponseEntity<ErrorObject> handleShortCodeNotFoundException(ShortCodeNotFoundException e
            , WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(KeeperException.NoNodeException.class)
    public ResponseEntity<ErrorObject> handleZNodeNotFoundException(KeeperException.NoNodeException e
            , WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ZkNodeExistsException.class)
    public ResponseEntity<ErrorObject> handleZkNodeExistsException(ZkNodeExistsException e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.CONFLICT.value());
        errorObject.setMessage("Zookeeper Error: Node already exists - " + e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(ZkInterruptedException.class)
    public ResponseEntity<ErrorObject> handleZkInterruptedException(ZkInterruptedException e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage("Zookeeper operation was interrupted: " + e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ZkException.class)
    public ResponseEntity<ErrorObject> handleZkException(ZkException e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage("Zookeeper Client Error: " + e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", HttpStatus.BAD_REQUEST.value());
        response.put("timestamp", new Date());

        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        String field = ex.getBindingResult().getFieldErrors().get(0).getField();

        Object rejectedValue = ex.getBindingResult().getFieldErrors().get(0).getRejectedValue();

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("field", field);
        errorDetails.put("rejectedValue", rejectedValue);
        errorDetails.put("message", errorMessage);

        response.put("error", errorDetails);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


}
