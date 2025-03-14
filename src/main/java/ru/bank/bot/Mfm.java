package ru.bank.bot;

import javax.xml.soap.*;
import java.util.Date;

import static ru.bank.bot.utils.Utils.clearPhone;
import static ru.bank.bot.utils.Utils.toDiamantLog;

public class Mfm {
    public static void sendCustomMessage(String phone, String text, DialogData dialogData) {
        Date currDt = new Date();
        String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(currDt);
        String messageId = "OmiliaSms" + new java.text.SimpleDateFormat("yyyyMMddHHmmssSSS").format(currDt);

        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
            SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("ns", "http://bank.ru/definitions/SendSMS/iib2cvp/");
            soapMessage.getMimeHeaders().setHeader("SOAPAction",
                    "\"http://bank.ru/definitions/SendSMS/iib2cvp/SendSMS\"");

            // Конструируем тело - BEGIN
            SOAPElement SendSMSRequest = envelope.getBody().addChildElement("SendSMSRequest", "ns");

            // MessageInfo
            SOAPElement MessageInfo = SendSMSRequest.addChildElement("MessageInfo", "ns");
            MessageInfo.addChildElement("MessageID", "ns").addTextNode(messageId);
            MessageInfo.addChildElement("MessageDate", "ns").addTextNode(timestamp);
            MessageInfo.addChildElement("SourceSystem", "ns").addChildElement("InstanceID", "ns")
                    .addTextNode("Omilia");
            MessageInfo.addChildElement("TargetSystemList", "ns").addChildElement("TargetSystem", "ns")
                    .addChildElement("InstanceID", "ns").addTextNode("SMSG");
            MessageInfo.addChildElement("MessageType", "ns").addTextNode("SendSMS");
            MessageInfo.addChildElement("InteractionType", "ns").addTextNode("synch");
            MessageInfo.addChildElement("MessageCode", "ns").addTextNode("0");
            MessageInfo.addChildElement("InternalSystemNumber", "ns").addTextNode(messageId);

            // MessageBody
            SOAPElement MessageBody = SendSMSRequest.addChildElement("MessageBody", "ns");
            SOAPElement SMSMessage = MessageBody.addChildElement("SMSMessageList", "ns")
                    .addChildElement("SMSMessage");
            SMSMessage.addChildElement("TaskId", "ns").addTextNode(messageId);
            SMSMessage.addChildElement("CreateDate", "ns").addTextNode(timestamp);
            String clearedPhone = "7" + clearPhone(phone);
            SMSMessage.addChildElement("PhoneNumber", "ns").addTextNode(clearedPhone);
            SMSMessage.addChildElement("Priority", "ns").addTextNode("1");
            SMSMessage.addChildElement("Text", "ns").addTextNode(text);
            SMSMessage.addChildElement("Type", "ns").addTextNode("BOT_OTP");
            SMSMessage.addChildElement("From", "ns").addTextNode("Bank");
            // Конструируем тело - END

            String addr = "http://rsb-clpmosil0.bank.ru:7080/CVPInputServiceWeb/SendSMS";
            toDiamantLog(dialogData, "Отправляем SMS (" + messageId + ") на номер " + clearedPhone + " с текстом: "
                    + text);
            SOAPConnectionFactory.newInstance().createConnection().call(soapMessage, addr);
        } catch (Exception e) { toDiamantLog("Ошибка при отправке SMS:", e); }
    }
}
