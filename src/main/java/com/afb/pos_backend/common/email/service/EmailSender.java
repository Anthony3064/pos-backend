package com.afb.pos_backend.common.email.service;

import org.thymeleaf.context.Context;

public interface EmailSender {
    void sendHtmlEmail(String to, String subject, String templateName, Context context);

    void sendHtmlEmailWithLogo(String to, String subject, String templateName, Context context);

    void logSuccessSendMessage(Object... params);

    void logSuccessSendMessageWithLogo(Object... params);

    void logErrorSendingMessage(String to, String exceptionMessage);

    void logErrorSendingMessageWithLogo(String to, String exceptionMessage);
}
