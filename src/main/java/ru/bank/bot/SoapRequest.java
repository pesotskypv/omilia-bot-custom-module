package ru.bank.bot;

import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import org.w3c.dom.NodeList;
import ru.bank.bot.utils.Utils;

import javax.xml.soap.*;
import java.util.*;

public class SoapRequest {

    public static Set<String> getCardLocationsOfficeCode(String SiebelId) throws SOAPException {
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        envelope.addNamespaceDeclaration("ns", "http://siebel.com/CustomUI");

        // Задаём SOAPAction
        // Без этого передаётся SOAPAction="", что означает что действие нужно брать из URI.
        // Обязательно экранированные кавычки.
        soapMessage.getMimeHeaders().setHeader("SOAPAction",
                "\"document/http://siebel.com/CustomUI:CVPGetCardLocation\"");

        // Конструируем тело - BEGIN
        SOAPElement BlockUnblockCard_Input = envelope.getBody().addChildElement("CVPGetCardLocation_Input", "ns");
        BlockUnblockCard_Input.addChildElement("sClientId").addTextNode(SiebelId);
        // Конструируем тело - END

        String siebelAddr = (String) CustomConfig.properties.get("siebeladdr");
        SOAPMessage soapResponse = SOAPConnectionFactory.newInstance().createConnection().call(soapMessage, siebelAddr);

        Set<String> result = new HashSet<>();

        NodeList nodeList = soapResponse.getSOAPBody().getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (((SOAPElement) nodeList.item(i)).getElementName().getLocalName().contains("LocatingDOCode") &&
                    ((SOAPElement) nodeList.item(i)).getTextContent().length() > 0) { // Отбрасываем пустые контейнеры
                result.add(nodeList.item(i).getTextContent());
                System.out.println();
            }
        }
        return result;
    }

    public static Map<String, ApiField> outboundImport(String FirstName, String LastName, String MiddleName,
                                                       String Phone, String CampaignId, String TimeZone,
                                                       String attr1Name, String attr1Value, String attr2Name,
                                                       String attr2Value, String attr3Name, String attr3Value,
                                                       String attr4Name, String attr4Value, String attr5Name,
                                                       String attr5Value) {
        Map<String, ApiField> output = new HashMap<>();

        try {
            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();

            SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
            envelope.addNamespaceDeclaration("tem", "http://tempuri.org/");
            envelope.addNamespaceDeclaration("out",
                    "http://schemas.datacontract.org/2004/07/OutboundManagement.BL.Wcf");

            // Задаём SOAPAction
            // Без этого передаётся SOAPAction="", что означает что действие нужно брать из URI.
            // Обязательно экранированные кавычки.
            soapMessage.getMimeHeaders().setHeader("SOAPAction",
                    "\"http://tempuri.org/IManagementContract/ImportContacts\"");

            // Авторизация HTTP Basic
            String Authorization = "Basic " + Base64.getEncoder().encodeToString((CustomConfig.properties
                    .get("outboundUser") + ":" + CustomConfig.properties.get("outboundPass")).getBytes());
            soapMessage.getMimeHeaders().setHeader("Authorization", Authorization);

            // Конструируем тело - BEGIN
            SOAPElement ImportContacts = envelope.getBody().addChildElement("ImportContacts", "tem");
            ImportContacts.addChildElement("campaignId", "tem").addTextNode(CampaignId);

            SOAPElement ContactInfo = ImportContacts.addChildElement("model", "tem")
                    .addChildElement("Contacts", "out").addChildElement("ContactInfo", "out");
            ContactInfo.addChildElement("FirstName", "out").addTextNode(FirstName);
            ContactInfo.addChildElement("LastName", "out").addTextNode(LastName);
            ContactInfo.addChildElement("MiddleName", "out").addTextNode(MiddleName);
            ContactInfo.addChildElement("MiddleName", "out").addTextNode(MiddleName);

            SOAPElement elemPhone = ContactInfo.addChildElement("Phones", "out")
                    .addChildElement("Phone", "out");
            elemPhone.addChildElement("Number", "out").addTextNode(Utils.clearPhone(Phone));
            elemPhone.addChildElement("PhoneKindID", "out").addTextNode("Mobile");
            // пример: Russian Standard Time
            ContactInfo.addChildElement("TimeZoneInfoName", "out").addTextNode(TimeZone);

            SOAPElement UserAttributes = ContactInfo.addChildElement("UserAttributes", "out");
            if (attr1Name != null && attr1Value != null && !attr1Name.equals("undefined")
                    && !attr1Value.equals("undefined")) {
                SOAPElement UserAttribute = UserAttributes.addChildElement("UserAttribute", "out");
                UserAttribute.addChildElement("GID", "out").addTextNode(attr1Name);
                UserAttribute.addChildElement("Value", "out").addTextNode(attr1Value);
            }
            if (attr2Name != null && attr2Value != null && !attr2Name.equals("undefined")
                    && !attr2Value.equals("undefined")) {
                SOAPElement UserAttribute = UserAttributes.addChildElement("UserAttribute", "out");
                UserAttribute.addChildElement("GID", "out").addTextNode(attr2Name);
                UserAttribute.addChildElement("Value", "out").addTextNode(attr2Value);
            }
            if (attr3Name != null && attr3Value != null && !attr3Name.equals("undefined")
                    && !attr3Value.equals("undefined")) {
                SOAPElement UserAttribute = UserAttributes.addChildElement("UserAttribute", "out");
                UserAttribute.addChildElement("GID", "out").addTextNode(attr3Name);
                UserAttribute.addChildElement("Value", "out").addTextNode(attr3Value);
            }
            if (attr4Name != null && attr4Value != null && !attr4Name.equals("undefined")
                    && !attr4Value.equals("undefined")) {
                SOAPElement UserAttribute = UserAttributes.addChildElement("UserAttribute", "out");
                UserAttribute.addChildElement("GID", "out").addTextNode(attr4Name);
                UserAttribute.addChildElement("Value", "out").addTextNode(attr4Value);
            }
            if (attr5Name != null && attr5Value != null && !attr5Name.equals("undefined")
                    && !attr5Value.equals("undefined")) {
                SOAPElement UserAttribute = UserAttributes.addChildElement("UserAttribute", "out");
                UserAttribute.addChildElement("GID", "out").addTextNode(attr5Name);
                UserAttribute.addChildElement("Value", "out").addTextNode(attr5Value);
            }
            // Конструируем тело - END

            String addr = (String) CustomConfig.properties.get("outboundManagementApi");
            SOAPMessage soapResponse = SOAPConnectionFactory.newInstance().createConnection().call(soapMessage, addr);

            NodeList nodeList = soapResponse.getSOAPBody().getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (((SOAPElement) nodeList.item(i)).getElementName().getLocalName().contains("ErrorCode")) {
                    if (nodeList.item(i).getTextContent().contains("0")) {
                        output.put("BEoutboundImportStatus", ApiField.builder().name("BEoutboundImportStatus")
                                .value("ok").status(FieldStatus.DEFINED).build());
                        return output;
                    }
                }
                if (((SOAPElement) nodeList.item(i)).getElementName().getLocalName().contains("ErrorMessage")
                        && nodeList.item(i).getTextContent().length() > 0) {
                    output.put("BEoutboundImportStatus", ApiField.builder().name("BEoutboundImportStatus")
                            .value(nodeList.item(i).getTextContent()).status(FieldStatus.DEFINED).build());
                }
            }

        } catch (Exception e) {
            output.put("BEoutboundImportStatus", ApiField.builder().name("BEoutboundImportStatus")
                    .value(e.toString()).status(FieldStatus.DEFINED).build());
        }

        if (output.isEmpty()) {
            output.put("BEoutboundImportStatus", ApiField.builder().name("BEoutboundImportStatus").value("error")
                    .status(FieldStatus.DEFINED).build());
        }
        return output;

    }
}