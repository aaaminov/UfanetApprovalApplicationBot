package org.tgbot.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
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

    private String actionsButtonText = "Действия";
    private String actionsButtonCallbackData = "actions";

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
        System.out.println("update = " + update);

        // если приходят сообщения
        if (update.hasMessage()) {

            Message message = update.getMessage();
            User from = message.getFrom();

            if (!DbConnection.instance.getSubscribersId().contains(from.getId())) {
                DbConnection.instance.saveSubscriber(from);
            }

            // command
            if (message.isCommand()) {
                switch (message.getText()) {
                    case "/start": {
                        sendMenu(from, startMessageText);
                        break;
                    }
                    case "/menu":
                    case "/back": {
                        sendMenu(from, "Главное меню");
                        break;
                    }
                    case "/approvers": {
                        sendApprovers(from.getId());
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
                        sendApplicationForApproval(from, approverId, description);
                    }
                    return;
                }

                return;
            }

            // echo bot
            sendText(from.getId(), message.getText());
        }

        // если нажимаются кнопки
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            buttonTap(callbackQuery);
            return;
        }

    }

    private void executeMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // private void buttonTap(Long fromId, String id, String data, String messageId)
    // {

    // }

    private void buttonTap(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();

        User from = callbackQuery.getFrom();
        Integer messageId = callbackQuery.getMessage().getMessageId();
        String text = callbackQuery.getMessage().getText();
        String queryId = callbackQuery.getId();

        System.out.println("callbackData = " + data);

        EditMessageText newText = EditMessageText.builder()
                .chatId(from.getId().toString())
                .messageId(messageId)
                .parseMode("HTML")
                .text("")
                .build();
        EditMessageReplyMarkup newMarkup = EditMessageReplyMarkup.builder()
                .chatId(from.getId().toString())
                .messageId(messageId)
                .replyMarkup(null)
                .build();

        // if (data.equals(actionsButtonCallbackData)) {

        // KeyboardButton approve = KeyboardButton.builder()
        // .text(approveButtonText)
        // .build();
        // KeyboardButton reject = KeyboardButton.builder()
        // .text(rejectButtonText)
        // .webApp(new WebAppInfo(webAppUrlRejectApplication))
        // .build();
        // KeyboardButton back = KeyboardButton.builder()
        // .text("/back")
        // .build();

        // ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder()
        // .keyboardRow(new KeyboardRow(List.of(approve, reject)))
        // .keyboardRow(new KeyboardRow(List.of(back)))
        // .resizeKeyboard(true)
        // .selective(true)
        // .build();
        // SendMessage sm = SendMessage.builder()
        // .chatId(from.getId().toString())
        // .text("Выберите действия для заявки от @" + from.getUserName())
        // .replyMarkup(markup)
        // .build();

        // AnswerCallbackQuery close = AnswerCallbackQuery.builder()
        // .callbackQueryId(queryId)
        // .build();
        // try {
        // execute(sm);
        // execute(close);
        // } catch (TelegramApiException e) {
        // e.printStackTrace();
        // }

        // return;
        // }
        // else


        if (data.equals(approveButtonCallbackData)) {
            newText.setParseMode("HTML");
            newText.setText(text + "\n" + "<b>✅ Согласовано</b>");

            // действия при "Согласовано"

            AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                    .callbackQueryId(queryId)
                    .build();
            try {
                execute(newText);
                execute(newMarkup);
                execute(close);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return;

        } 
        // else if (data.equals(rejectButtonCallbackData)) {
        //     newText.setParseMode("HTML");
        //     newText.setText(text + "\n" + "<b>❌ Отказано</b>");

        //     // действия при "Отказано"

        //     KeyboardButton button = KeyboardButton.builder()
        //             .text(createApplicationToApproveButtonText)
        //             .webApp(new WebAppInfo(webAppUrlRejectApplication))
        //             .build();
        // }
        return;
    }

    private void sendMenu(User from, String text) {
        KeyboardButton button = KeyboardButton.builder()
                .text(createApplicationToApproveButtonText)
                .webApp(new WebAppInfo(webAppUrlApplicationToApprove))
                .build();
        ReplyKeyboardMarkup markup = ReplyKeyboardMarkup.builder()
                .keyboardRow(new KeyboardRow(List.of(button)))
                .resizeKeyboard(true)
                .selective(true)
                .build();
        SendMessage sm = SendMessage.builder()
                .chatId(from.getId().toString())
                .text(text)
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

    private void sendApplicationForApproval(User from, Long approverId, String description) {
        InlineKeyboardButton approve = InlineKeyboardButton.builder()
                .text(approveButtonText)
                .callbackData(approveButtonCallbackData)
                .build();
        InlineKeyboardButton reject = InlineKeyboardButton.builder()
                .text(rejectButtonText)
                .webApp(new WebAppInfo(webAppUrlRejectApplication))
                .build();

        // InlineKeyboardButton actions = InlineKeyboardButton.builder()
        // .text(actionsButtonText)
        // .webApp(new WebAppInfo(webAppUrlRejectApplication))
        // .build();
        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(approve, reject))
                .build();

        String text = "<b>Заявка от @" + from.getUserName() + "</b>\n" + description;
        SendMessage sm = SendMessage.builder()
                .chatId(approverId.toString())
                .parseMode("HTML")
                .text(text)
                .replyMarkup(markup)
                .build();

        executeMessage(sm);
    }

}
