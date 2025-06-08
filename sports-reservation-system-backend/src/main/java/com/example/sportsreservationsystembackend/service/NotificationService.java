package com.example.sportsreservationsystembackend.service;

import com.example.sportsreservationsystembackend.model.AppUser;

public interface NotificationService {

    void sendRegistrationEmail(AppUser recipient, String link);

    void sendResetPasswordEmail(AppUser recipient, String token, String password);
}
