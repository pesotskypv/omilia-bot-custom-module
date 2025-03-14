package ru.bank.bot;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import static ru.bank.bot.utils.Utils.toDiamantLog;

public class Email {
    public static void main(String[] args) {
        System.out.println(sendConfirmationCode(null,
                "pesotskypv@ya.ru"));
    }

    public static Map<String, ApiField> sendConfirmationCode(DialogData dialogData, String email) {
        Map<String, ApiField> output = new HashMap<>();

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.bank.ru");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", false);
        properties.put("mail.smtp.starttls.enable", "false");

        Session session = Session.getInstance(properties);

//        session.setDebugOut(new PrintStream(new Utils.OutputStream2log()));      // Test
//        session.setDebug(true);     // Test

        String code = String.valueOf(new Random().nextInt(10))
                + String.valueOf(new Random().nextInt(10))
                + String.valueOf(new Random().nextInt(10))
                + String.valueOf(new Random().nextInt(10));
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("omilia@bank.ru"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("BankEduBot - Код подтверждения");
            String msg = "Код подтверждения: " + code;

            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception e) {
            toDiamantLog("Ошибка при отправке email:", e);
            output.put("BEsendConfirmationCodeStatus", ApiField.builder().name("BEsendConfirmationCodeStatus")
                    .value(e.toString()).status(FieldStatus.DEFINED).build());
            return output;
        }

        output.put("BEemailCode", ApiField.builder().name("BEemailCode").value(code).status(FieldStatus.DEFINED)
                .build());
        output.put("BEsendConfirmationCodeStatus", ApiField.builder().name("BEsendConfirmationCodeStatus").value("ok")
                .status(FieldStatus.DEFINED).build());
        return output;
    }

    public static void notifyAboutException(DialogData dialogData, String text) {
        Map<String, ApiField> output = new HashMap<>();

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.host", "smtp.bank.ru");
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", false);
        properties.put("mail.smtp.starttls.enable", "false");

        Session session = Session.getInstance(properties);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("omilia@bank.ru"));
            String emailTo = (String) CustomConfig.properties.get("emailForExceptions");
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailTo));
            message.setSubject("Omilia - Exception");
            String msg = "<a href=\"https://bank.gts.ru:8443/DRTViewer/d?sid="
                    + dialogData.fieldsContainer.getDialogIDField().getFieldInstanceValue()
                    + "\">"
                    + dialogData.fieldsContainer.getDialogIDField().getFieldInstanceValue()
                    + "</a><br>" + text;
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);
            Transport.send(message);
        } catch (Exception ignored) {
        }

    }
}
