package com.afb.pos_backend.common.email.service;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Base64;

@Service
@Log4j2
public class EmailService {
    private final SendGrid mailSender;
    private final TemplateEngine templateEngine;
    private final String fromEmail;

    public EmailService(@Value("${app.send-grid-key}") String apiKey, @Value("${app.send-grid-from}") String fromEmail, TemplateEngine templateEngine) {
        this.mailSender = new SendGrid(apiKey);
        this.fromEmail = fromEmail;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendHtmlEmail(String to, String subject, String templateName, Context context) throws IOException {

        String htmlContent = templateEngine.process(templateName, context);

        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);

        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = mailSender.api(request);
        log.info("Status Code: {}, response: {}, header: {}", response.getStatusCode(), response.getBody(), response.getHeaders());
    }

    @Async
    public void sendHtmlEmailWithLogo(String to, String subject, String templateName, Context context) throws IOException {

        // For add image logo of company -> Name of variable imageLogo for thymeleaf template.
        // The logo.png file must exist in the resources/static folder.
        ClassPathResource logoResource = new ClassPathResource("static/logo.png");
        boolean logoExists = logoResource.exists();
        context.setVariable("logoExists", logoExists);

        String htmlContent = templateEngine.process(templateName, context);

        Email from = new Email(fromEmail);
        Email toEmail = new Email(to);

        Content content = new Content("text/html", htmlContent);
        Mail mail = new Mail(from, subject, toEmail, content);


        if (logoExists) {
            byte[] logoBytes = logoResource.getInputStream().readAllBytes();
            String encodedLogo = Base64.getEncoder().encodeToString(logoBytes);

            Attachments logoAttachment = new Attachments();
            logoAttachment.setContent(encodedLogo);
            logoAttachment.setType("image/png");
            logoAttachment.setFilename("logo.png");
            logoAttachment.setDisposition("inline");
            logoAttachment.setContentId("logoImage");

            mail.addAttachments(logoAttachment);
        }


        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = mailSender.api(request);
        log.info("Status Code: {}, response: {}, header: {}", response.getStatusCode(), response.getBody(), response.getHeaders());
    }

}
