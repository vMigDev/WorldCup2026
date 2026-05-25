package mig.project.advice;

import mig.project.exceptions.WedstrijdNietGevondenException;
import mig.project.rest.dto.RestFoutResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice(annotations = RestController.class)
public class RestFoutAfhandeling {

    @ExceptionHandler(WedstrijdNietGevondenException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public RestFoutResponse handleNietGevonden(Exception ex) {
        return new RestFoutResponse(404, ex.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestFoutResponse handleFouteInvoer(IllegalArgumentException ex) {
        return new RestFoutResponse(400, ex.getMessage(), LocalDateTime.now());
    }
}