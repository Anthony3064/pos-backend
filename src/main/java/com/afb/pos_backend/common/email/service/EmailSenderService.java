package com.afb.pos_backend.common.email.service;

import com.afb.pos_backend.common.dto.EmailSenderProvider;
import com.afb.pos_backend.common.email.service.factory.EmailSenderFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
public class EmailSenderService {

    private final EmailSenderFactory factory;
    private final EmailSenderProvider provider;

    public EmailSenderService(EmailSenderFactory factory) {
        this.factory = factory;
        // Provider -> SENDGRID or AWS_SES. In this case I use AWS_SES.
        this.provider = EmailSenderProvider.SENDGRID;
    }

    public void sendEmail(String to, String subject, String templateName, Context context) {
        EmailSender emailSender = this.factory.getEmailSender(provider);
        emailSender.sendHtmlEmail(to, subject, templateName, context);
    }

    public void sendEmailWithLogo(String to, String subject, String templateName, Context context) {
        EmailSender emailSender = this.factory.getEmailSender(provider);
        emailSender.sendHtmlEmailWithLogo(to, subject, templateName, context);
    }

}
