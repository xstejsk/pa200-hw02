package com.example.sportsreservationsystembackend.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {

        public InvalidRefreshTokenException(String message) {
            super(message);
        }
}
