package com.mrzolution.integridad.app.services;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mrzolution.integridad.app.domain.UserIntegridad;

import lombok.extern.slf4j.Slf4j;

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

    public Boolean sendEmailREgister(UserIntegridad userIntegridad, String passPreEncoded) {
        String to = userIntegridad.getEmail();
        String subject = "Cuenta para tu aplicacion Integridad";
        
        String link ="https://mrzolutions.github.io/Integridad/integridad-ui/dist/#!/activate/" + userIntegridad.getId()+ "/" + userIntegridad.getValidation();
        
        String body = "Gracias por tu registro. "
                + "<br> Tu email registrado es: " + userIntegridad.getEmail()
                + "<br> Tu password es: " + passPreEncoded
        		+ "<br> Usa este link para activar tu cuenta: "
        		+ "<br><br><a href=\""+link+"\">"
        		+ "<button>ACTIVAR</button>"
        		+ "</a>";
    
        
        sendEmail(subject, body, to);
        return true;
    }
    
    public Boolean sendEmailRecoveryPass(UserIntegridad userIntegridad, String pass){
    	String to = userIntegridad.getEmail();
        String subject = "Clave Recuperada para Sistema Integridad";
        
        String link ="https://mrzolutions.github.io/Integridad/integridad-ui/dist/#!/";
        
        String body = "GRACIAS POR CONFIAR EN LOS SERVICIOS DE MR. ZOLUTIONS ECUADOR. "
        		+ "<br><br> Estimado usuario, hemos recibido su solicitud de recuperación de clave para el sistema INTEGRIDAD. "
        		+ "<br> Su clave de acceso temporal es: " + pass
        		+ "<br> Ingrese al sistema y personalice su clave dando clic en el siguiente botón"
        		+ "<br><br><a href=\""+link+"\">"
        		+ "<button>Intedgridad</button>"
        		+ "</a>";
    
        
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
            message.setContent(body, "text/html; charset=utf-8");
            message.setSubject(subject);
//            message.setText(body);

            Transport.send(message);
            
            log.info("Email sent to: {}", to);


        } catch (MessagingException e) {
        	log.error("Error email sent to: {}", to);
            throw new RuntimeException(e);
        }
    }
}
