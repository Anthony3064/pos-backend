package com.afb.pos_backend.common.email.service.factory;

import com.afb.pos_backend.common.dto.EmailSenderProvider;
import com.afb.pos_backend.common.email.service.EmailSender;
import com.afb.pos_backend.common.email.service.implementation.EmailSenderAWS;
import com.afb.pos_backend.common.email.service.implementation.EmailSenderSendGrid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@Log4j2
public class EmailSenderFactory {

    private final String awsRegion;

    private final String awsFromEmail;

    private final String sendGridApiKey;

    private final String sendGridFromEmail;

    private final TemplateEngine templateEngine;

    @Autowired
    public EmailSenderFactory(@Value("${app.aws.region}") String awsRegion,
                              @Value("${app.aws.ses.from}") String awsFromEmail,
                              @Value("${app.send-grid-key}") String sendGridApiKey,
                              @Value("${app.send-grid-from}") String sendGridFromEmail,
                              TemplateEngine templateEngine) {
        this.awsRegion = awsRegion;
        this.awsFromEmail = awsFromEmail;
        this.sendGridApiKey = sendGridApiKey;
        this.sendGridFromEmail = sendGridFromEmail;
        this.templateEngine = templateEngine;
    }

    public EmailSender getEmailSender(EmailSenderProvider provider) {
        return switch (provider) {
            case SENDGRID -> new EmailSenderSendGrid(sendGridApiKey, sendGridFromEmail, templateEngine);
            case AWS_SES -> new EmailSenderAWS(awsRegion, awsFromEmail, templateEngine);
            default -> {
                log.error("Proveedor de correo no soportado: {}", provider.name());
                throw new IllegalArgumentException("Proveedor de correo no soportado: " + provider.name());
            }
        };
    }

}
