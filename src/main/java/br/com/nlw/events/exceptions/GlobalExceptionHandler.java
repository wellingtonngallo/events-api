package br.com.nlw.events.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import br.com.nlw.events.dto.ErrorMessage;

@ControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(EventNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleEventNotFoundException(EventNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
  }

  @ExceptionHandler(SubscriptionConflictException.class)
  public ResponseEntity<ErrorMessage> handleSubscriptionConflictException(SubscriptionConflictException e) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage(e.getMessage()));
  }

  @ExceptionHandler(UserIndicadorNotFoundException.class)
  public ResponseEntity<ErrorMessage> handleUserIndicadorNotFoundException(UserIndicadorNotFoundException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleException(Exception e) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(e.getMessage()));
  }
}
