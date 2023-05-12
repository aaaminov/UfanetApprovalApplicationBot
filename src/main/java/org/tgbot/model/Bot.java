package org.tgbot.model;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    private String botUsername = "UfanetApprovalApplicationBot";
    private String botToken = "6230219510:AAEhCAZN7YRDLLBU2cPIKo2v18lg3NL83aw";

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
        Message message = update.getMessage();
        sendMessage(message.getFrom().getId(), message.getText());
    }

    public void sendMessage(Long userId, String msg) {
        SendMessage sm = SendMessage.builder()
                .chatId(userId.toString())
                .text(msg).build();
        try {
            execute(sm);
        }catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
}
