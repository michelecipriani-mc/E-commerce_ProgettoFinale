package com.smartbay.progettofinale.Services;

public interface EmailService {
    void sendSimpleEmail(String to, String subject, String text);
}
