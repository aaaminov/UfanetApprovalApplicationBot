package org.tgbot.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
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

    private String webAppUrlApplicationToApprove = "https://aaaminov.github.io/UfanetApprovalApplicationBot/web/application_to_approve.html";
    private String webAppUrlRejectApplication = "https://aaaminov.github.io/UfanetApprovalApplicationBot/web/reject_application.html";

    private String startMessageText = "Привет";

    private String createApplicationToApproveButtonText = "Создать заявку";

    private String approveButtonText = "Согласовать";
    private String approveButtonCallbackData = "approve";

    private String rejectButtonText = "Отказать";
    private String rejectButtonCallbackData = "reject";

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

            if (webAppData.getButtonText().equals(createApplicationToApproveButtonText)) {
                JSONObject data = new JSONObject(webAppData.getData());
                JSONArray apr = data.getJSONArray("apr");
                String description = data.getString("des");

                for (int i = 0; i < apr.length(); i++) {
                    Long approverId = Long.valueOf(apr.getString(i));
                    sendApplicationForApproval(message.getFrom().getId(), approverId, description);
                }
                return;
            }

            return;
        }

        if (update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData()
            
            System.out.println("callbackData = " + callbackData);
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
        KeyboardButton button = KeyboardButton.builder()
                .text(createApplicationToApproveButtonText)
                .webApp(new WebAppInfo(webAppUrlApplicationToApprove))
                .build();

        ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder()
                .keyboardRow(new KeyboardRow(List.of(button)))
                .build();

        SendMessage sm = SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(startMessageText)
                .replyMarkup(markup)
                .build();

        executeMessage(sm);
    }

    private void sendText(Long userId, String msg) {
        SendMessage sm = SendMessage.builder()
                .chatId(userId.toString())
                .text(msg + "").build();
        executeMessage(sm);
    }

    private void sendApprovers(Long userId) {
        sendText(userId, DbConnection.instance.getSubscribersId().toString());
    }

    private void sendApplicationForApproval(Long userId, Long approverId, String description) {
        InlineKeyboardButton approve = InlineKeyboardButton.builder()
                .text(approveButtonText).callbackData(approveButtonCallbackData)
                .build();

        InlineKeyboardButton reject = InlineKeyboardButton.builder()
                .text(rejectButtonText).callbackData(rejectButtonCallbackData)
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(approve, reject))
                .build();

        SendMessage sm = SendMessage.builder()
                .chatId(approverId.toString())
                .text(description)
                .replyMarkup(markup)
                .build();

        executeMessage(sm);
    }

}
