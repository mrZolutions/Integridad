package com.mrzolution.integridad.app.services;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.UserIntegridad;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by daniel.
 */
@Slf4j
@Component
public class MailingService {

    @Value("${email.username}")
    String usermail;

    @Value("${email.pass}")
    String pass;

    @Value("${email.emailFrom}")
    String emailFrom;

    public Boolean sendEmailREgister(UserIntegridad userIntegridad) {
        String to = userIntegridad.getEmail();
        String subject = "Cuenta para tu aplicacion Integridad";
        String body = "Gracias por tu registro. "
                + "\n\n Tu email registrado es: " + userIntegridad.getEmail()
        		+ "\n\n Usa este link para activar tu cuenta: "
        		+ "\n\nhttps://mrzolutions.github.io/Integridad/integridad-ui/dist/#!/activate/" + userIntegridad.getId()+ "/" + userIntegridad.getValidation();
        
        sendEmail(subject, body, to);
        return true;
    }

    public void sendEmail(String subject, String body, String to) {
    	log.info("SendEmail subject: {}, to: {}", subject, to);
        Properties props = new Properties();
        
        props.put("mail.transport.protocol","smtp" );
        props.put("mail.smtp.starttls.enable","true" );
        props.put("mail.smtp.ssl.enable", "false");
        props.put("mail.smtp.host","smtpout.secureserver.net");

        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.port","465");
        props.put("mail.debug","true");
        props.put("mail.smtp.socketFactory.port","465");
        props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback","false");
        
        Session session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(usermail, pass);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            
            log.info("Email sent to: {}", to);


        } catch (MessagingException e) {
        	log.error("Error email sent to: {}", to);
            throw new RuntimeException(e);
        }
    }
}
