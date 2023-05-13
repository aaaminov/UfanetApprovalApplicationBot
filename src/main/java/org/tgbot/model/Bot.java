package org.tgbot.model;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppData;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.tgbot.db.DbConnection;

public class Bot extends TelegramLongPollingBot {

    // private String botUsername = "UfanetApprovalApplicationBot";
    // private String botToken = "6230219510:AAEhCAZN7YRDLLBU2cPIKo2v18lg3NL83aw";
    private String botUsername = "NeverKetBot";
    private String botToken = "5848728893:AAENlDxAuca7sJZtKc5rvUy-b00T0ecVOjg";

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        User from = update.getMessage().getFrom();
        if (!DbConnection.instance.getSubscribersId().contains(from)) {
            DbConnection.instance.saveSubscriber(from);
        }

        Message message = update.getMessage();
        if (message.isCommand()) {
            if (message.getText().equals("/approvers")) { // If the command was /scream, we switch gears
                sendApprovers(message.getFrom().getId());
            }
        } else {
            sendMessage(message.getFrom().getId(), message.getText());
        }

        WebAppData webAppData = message.getWebAppData();
        if (webAppData != null) {
            System.out.println(webAppData);
        }

    }

    public void sendMessage(Long userId, String msg) {
        SendMessage sm = SendMessage.builder()
                .chatId(userId.toString())
                .text(msg + "").build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendApprovers(Long userId) {
        SendMessage sm = SendMessage.builder()
                .chatId(userId.toString())
                .text(DbConnection.instance.getSubscribersId().toString()).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
