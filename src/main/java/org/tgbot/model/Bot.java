package org.tgbot.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
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

    private String startMessageText = "Привет";
    private String keyboardButtonText = "Создать заявку";

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

        // command
        if (message.isCommand()) {
            switch (message.getText()) {
                case "/start": {
                    sendStart(message);
                    break;
                }
                case "/approvers": {
                    sendApprovers(message.getFrom().getId());
                    break;
                }
            }
            return;
        }

        // web app
        WebAppData webAppData = message.getWebAppData();
        if (webAppData != null) {
            System.out.println("webAppData = " + webAppData);

            if (webAppData.getButtonText().equals(keyboardButtonText)) {
                // String dataStr = webAppData.getData();
                // System.out.println("dataStr = " + dataStr);

                // String[] data = webAppData.getData().split(".", 2);
                // System.out.println("data = " + data.toString());

                // String[] approvers = data[0].split(",");
                // System.out.println("approvers = " + approvers.toString());
                
                // String description = data[1];
                // System.out.println("description = " + description);

                // for (int i = 0; i < approvers.length; i++) {
                //     Long approverId = Long.valueOf(approvers[i]);
                //     System.out.println("approverId = " + approverId);
                    
                //     sendText(approverId, description);
                // }

                JSONObject data = new JSONObject(webAppData.getData());
                
            }
            return;
        }

        // echo bot
        sendText(message.getFrom().getId(), message.getText());
    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendStart(Message message) {
        SendMessage sm = new SendMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow = new KeyboardRow();

        WebAppInfo webAppInfo = new WebAppInfo(webAppUrl);
        KeyboardButton button = new KeyboardButton(keyboardButtonText, null, null, null, webAppInfo);

        keyboardRow.add(button);
        keyboardRows.add(keyboardRow);

        replyKeyboardMarkup.setKeyboard(keyboardRows);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setSelective(true);

        sm.setReplyMarkup(replyKeyboardMarkup);
        sm.setText(startMessageText);
        sm.setChatId(message.getChatId().toString());

        executeMessage(sm);
    }

    public void sendText(Long userId, String msg) {
        SendMessage sm = SendMessage.builder()
                .chatId(userId.toString())
                .text(msg + "").build();
        executeMessage(sm);
    }

    private void sendApprovers(Long userId) {
        sendText(userId, DbConnection.instance.getSubscribersId().toString());
    }

}
