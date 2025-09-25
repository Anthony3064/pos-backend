package com.afb.pos_backend.common.email.service.implementation;

import com.afb.pos_backend.common.constant.MessageConstant;
import com.afb.pos_backend.common.email.service.EmailSender;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

@Component
@Log4j2
public class EmailSenderAWS implements EmailSender {

    private final SesClient sesClient;
    private final TemplateEngine templateEngine;
    private final String fromEmail;

    public EmailSenderAWS(@Value("${app.aws.region}") String region, @Value("${app.aws.ses.from}") String fromEmail, TemplateEngine templateEngine) {
        this.sesClient = SesClient.builder().region(Region.of(region)).build();
        this.fromEmail = fromEmail;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            String htmlContent = templateEngine.process(templateName, context);

            SendEmailRequest request = SendEmailRequest.builder().source(fromEmail).destination(Destination.builder().toAddresses(to).build()).message(Message.builder().subject(Content.builder().data(subject).charset("UTF-8").build()).body(Body.builder().html(Content.builder().data(htmlContent).charset("UTF-8").build()).build()).build()).build();

            sesClient.sendEmail(request);
            logSuccessSendMessage(to);
        } catch (Exception ex) {
            logErrorSendingMessage(to, ex.getMessage());
        }
    }

    @Async
    public void sendHtmlEmailWithLogo(String to, String subject, String templateName, Context context) {
        try {
            ClassPathResource logoResource = new ClassPathResource("static/logo.png");
            boolean logoExists = logoResource.exists();
            context.setVariable("logoExists", logoExists);

            String htmlContent = templateEngine.process(templateName, context);

            MimeMessage message = createMimeMessage();
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject, "UTF-8");

            if (logoExists) {
                MimeMultipart multipart = new MimeMultipart("related");

                MimeBodyPart htmlPart = new MimeBodyPart();
                htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
                multipart.addBodyPart(htmlPart);

                MimeBodyPart logoPart = new MimeBodyPart();
                byte[] logoBytes = logoResource.getInputStream().readAllBytes();

                DataSource logoDataSource = new ByteArrayDataSource(logoBytes, "image/png");
                logoPart.setDataHandler(new DataHandler(logoDataSource));
                logoPart.setHeader("Content-ID", "<logoImage>");
                logoPart.setHeader("Content-Type", "image/png");
                logoPart.setHeader("Content-Disposition", "inline");

                multipart.addBodyPart(logoPart);
                message.setContent(multipart);
            } else {
                message.setContent(htmlContent, "text/html; charset=UTF-8");
            }

            sendRawEmail(message);
            logSuccessSendMessageWithLogo(to);
        } catch (Exception ex) {
            logErrorSendingMessageWithLogo(to, ex.getMessage());
        }
    }

    private MimeMessage createMimeMessage() {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props);
        return new MimeMessage(session);
    }

    private void sendRawEmail(MimeMessage message) throws MessagingException, IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        message.writeTo(outputStream);

        SendRawEmailRequest request = SendRawEmailRequest.builder().source(fromEmail).destinations(message.getAllRecipients()[0].toString()).rawMessage(RawMessage.builder().data(SdkBytes.fromByteArray(outputStream.toByteArray())).build()).build();

        sesClient.sendRawEmail(request);
    }

    public void logSuccessSendMessage(Object... params) {
        log.info(MessageConstant.SUCCESS_AWS_SES_MESSAGE_SEND_HTML_EMAIL, params);
    }

    public void logSuccessSendMessageWithLogo(Object... params) {
        log.info(MessageConstant.SUCCESS_AWS_SES_MESSAGE_SEND_HTML_EMAIL_WITH_LOGO, params);
    }

    public void logErrorSendingMessage(String to, String exceptionMessage) {
        log.error(MessageConstant.ERROR_MESSAGE_SEND_HTML_EMAIL, to, exceptionMessage);
    }

    public void logErrorSendingMessageWithLogo(String to, String exceptionMessage) {
        log.error(MessageConstant.ERROR_MESSAGE_SEND_HTML_EMAIL_WITH_LOGO, to, exceptionMessage);
    }

}
