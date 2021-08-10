package com.godzillajim.user_management.mails;

public interface EmailSender {
    void send(String to, String email);
}
