package org.tgbot.model;

import java.util.ArrayList;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppData;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.tgbot.db.DbConnection;


public class Bot extends TelegramLongPollingBot {

    // private String botUsername = "UfanetApprovalApplicationBot";
    // private String botToken = "6230219510:AAEhCAZN7YRDLLBU2cPIKo2v18lg3NL83aw";
    private String botUsername = "NeverKetBot";
    private String botToken = "5848728893:AAENlDxAuca7sJZtKc5rvUy-b00T0ecVOjg";
    private String webAppUrl = "https://aaaminov.github.io/UfanetApprovalApplicationBot/web/index.html";

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
        if (!DbConnection.instance.getSubscribersId().contains(from.getId())) {
            DbConnection.instance.saveSubscriber(from);
        }

        Message message = update.getMessage();
        if (message.isCommand()) {
            switch (message.getText()){
                case "/start": {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText("hello!");
                    sendMessage.setChatId(message.getChatId().toString());

                    ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

                    WebAppInfo webAppInfo = new WebAppInfo(webAppUrl);
                    List<KeyboardRow> keyboardRows = new ArrayList<>();
                    KeyboardRow keyboardRow = new KeyboardRow();

                    KeyboardButton button = new KeyboardButton("WEB app", null, null, null, webAppInfo);
                    keyboardRow.add(button);
                    keyboardRows.add( keyboardRow);
                    replyKeyboardMarkup.setKeyboard(keyboardRows);
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    return;
                }
                case "/approvers": {
                    sendApprovers(message.getFrom().getId());
                    return;
                }
            }
        } else {
            WebAppData webAppData = message.getWebAppData();
            if (webAppData != null) {
                System.out.println(webAppData);
            }
    
            sendText(message.getFrom().getId(), message.getText());
    
        }
    }

    public void sendText(Long userId, String msg) {
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
        sendText(userId, DbConnection.instance.getSubscribersId().toString());
    }

}
