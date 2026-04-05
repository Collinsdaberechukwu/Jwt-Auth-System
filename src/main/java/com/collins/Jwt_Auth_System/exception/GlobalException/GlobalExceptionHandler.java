package com.collins.Jwt_Auth_System.exception.GlobalException;

import com.collins.Jwt_Auth_System.dtos.ErrorResponseDto;
import com.collins.Jwt_Auth_System.exception.AdminAlreadyExistException;
import com.collins.Jwt_Auth_System.exception.InvalidLoginException;
import com.collins.Jwt_Auth_System.exception.ResourceNotFoundException;
import com.collins.Jwt_Auth_System.exception.UserAlreadyExistException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected  ResponseEntity<Object> handleMethodArgumentNotValid(
                                                                        MethodArgumentNotValidException ex,
                                                                        HttpHeaders headers,
                                                                        HttpStatusCode status,
                                                                        WebRequest request){
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validErrorsList = ex.getBindingResult().getAllErrors();

        validErrorsList.forEach((error) -> {
            String fieldName = ((FieldError)error).getField();
            String validationMsg = error.getDefaultMessage();
            validationErrors.put(fieldName,validationMsg);
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGlobalException(Exception exception,WebRequest webRequest){

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleUserAlreadyExistException(UserAlreadyExistException ex,
                                                                             WebRequest webRequest){
        ErrorResponseDto responseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(responseDto,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleResourceNotFoundException(ResourceNotFoundException e,
                                                                            WebRequest webRequest){

        ErrorResponseDto responseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                e.getMessage(),
                LocalDateTime.now()
        );

        return new ResponseEntity<>(responseDto,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AdminAlreadyExistException.class)
    public ResponseEntity<ErrorResponseDto> handleAdminAlreadyExistException(AdminAlreadyExistException ex,
                                                                             WebRequest webRequest){
        ErrorResponseDto responseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(responseDto,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidLoginException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidLoginException(InvalidLoginException exception,
                                                                        WebRequest webRequest){

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                webRequest.getDescription(false),
                HttpStatus.TOO_MANY_REQUESTS,
                exception.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(errorResponseDto,HttpStatus.TOO_MANY_REQUESTS);
    }
}
