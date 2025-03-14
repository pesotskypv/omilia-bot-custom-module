package ru.bank.bot;

import com.omilia.diamant.dialog.components.fields.Field;

public class Outbound {
    public static void setContactStatus(Field OutboundSessionId, Field OutboundContactId, Field OutboundPhoneResult,
                                        Field OutboundBusinessResult, Field OutboundRescheduleMinutes) {
        String sessionsUrl = "http://retoutbounddialer.bank.ru:4001/DialerApi/xml/sessions/";
        String contactMgtUrl = "http://retoutbound.bank.ru:4003/OutadminApi/ContactManagement.svc/contact/";
        String url;
        // Если поля не созданы во flow, либо созданы но без значения - будут undefined.
        String SessionId = "undefined";
        String ContactId = "undefined";
        String PhoneResult = "undefined";
        String BusinessResult = "undefined";
        String RescheduleMinutes = "undefined";

        try {
            SessionId = OutboundSessionId.getFieldInstanceValue();
        } catch (Exception ignored) {
        }
        try {
            ContactId = OutboundContactId.getFieldInstanceValue();
        } catch (Exception ignored) {
        }
        try {
            PhoneResult = OutboundPhoneResult.getFieldInstanceValue();
        } catch (Exception ignored) {
        }
        try {
            BusinessResult = OutboundBusinessResult.getFieldInstanceValue();
        } catch (Exception ignored) {
        }
        try {
            RescheduleMinutes = OutboundRescheduleMinutes.getFieldInstanceValue();
        } catch (Exception ignored) {
        }

        if (SessionId.equals("undefined")) {
        } // Если нет SessionId завершаем метод
        else if (!RescheduleMinutes.equals("undefined") && !RescheduleMinutes.equals("0")
                && !ContactId.equals("undefined")) { // Если указано перепланирование и есть ContactId
            url = sessionsUrl + SessionId + "/setCallResult?resultCode=0";
            HttpRequests.sendGetReturnJson(url, false);
            url = contactMgtUrl + ContactId + "/rescheduleByInterval?resetAttempt=0&interval=" + RescheduleMinutes;
            HttpRequests.sendPost(url, false, (String) CustomConfig.properties.get("outboundUser"),
                    (String) CustomConfig.properties.get("outboundPass"));
        } else if (!PhoneResult.equals("undefined") && !BusinessResult.equals("undefined")) {
            url = sessionsUrl + SessionId + "/setCallResult?resultCode=" + PhoneResult + "&result=" + BusinessResult;
            HttpRequests.sendGetReturnJson(url, false);
        } else if (!PhoneResult.equals("undefined")) {
            url = sessionsUrl + SessionId + "/setCallResult?resultCode=" + PhoneResult;
            HttpRequests.sendGetReturnJson(url, false);
        } else {
            url = sessionsUrl + SessionId + "/setCallResult?resultCode=0&result=BusinessResult_did_not_set";
            HttpRequests.sendGetReturnJson(url, false);
        }

    }
}
