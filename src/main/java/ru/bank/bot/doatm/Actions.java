package ru.bank.bot.doatm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.omilia.diamant.dialog.components.fields.ApiField;
import ru.bank.bot.CustomConfig;
import ru.bank.bot.DialogData;
import ru.bank.bot.HttpRequests;
import ru.bank.bot.utils.OutputMap;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import static ru.bank.bot.HttpRequests.get2bspApi;
import static ru.bank.bot.HttpRequests.bspApi;
import static ru.bank.bot.utils.Utils.toDiamantLog;

public class Actions {
    public static Map<String, ApiField> getNearestAtms(DialogData dialogData, String BEatmCity, String BEatmAddress,
                                                       String BEatmBankOrAny, String BEatmService) {
        OutputMap output = new OutputMap();

        // Получение координат клиента по адресу
        HashMap<String, String> custCoords = null;
        try {
            custCoords = getCoordsByAddr(dialogData, BEatmCity + ", " + BEatmAddress);
        } catch (Exception e) {
            toDiamantLog(dialogData, "Не удалось получить координаты от Яндекс:", e);
            output.add("BEgetNearestAtmsStatus", "error");
            return output.get();
        }
        double customerLatitude = Double.parseDouble(custCoords.get("lat"));
        double customerLongitude = Double.parseDouble(custCoords.get("lon"));

        // Критерии отбора банкоматов
        List<String> requestedServices = new LinkedList<>();
        requestedServices.add(BEatmService);
        if (BEatmBankOrAny.equals("bank")) {
            requestedServices.add("excludePartners");
        } // Если не нужны партнёры

        // Получение банкоматов из BSP
        Set<Servicepoint> atms;
        try {
            atms = getServicepoints(customerLatitude, customerLongitude, SPType.ATM, requestedServices, 10);
        } catch (Exception e) {
            toDiamantLog(dialogData, "Не удалось получить банкоматы от BSP:", e);
            output.add("BEgetNearestAtmsStatus", "error");
            return output.get();
        }

        int atmCounter = 1;
        for (Servicepoint sp : atms) {
            output.add("BEatmBank" + atmCounter, ((Atm) sp).bank);
            output.add("BEatmDistance" + atmCounter, sp.getRoundedDistance());
            output.add("BEatmAddress" + atmCounter, sp.getClearAddress());
            if (sp.getSchedule() != null) {
                output.add("BEatmSchedule" + atmCounter, sp.getSchedule());
            }
            atmCounter++;
        }

        if (output.get().size() > 0) {
            output.add("BEgetNearestAtmsStatus", "ok");
        } else {
            output.add("BEgetNearestAtmsStatus", "not found");
        }

        return output.get();
    }


    public static HashMap<String, String> getCoordsByAddr(DialogData dialogData, String address) throws Exception {
        HashMap<String, String> coords = new HashMap<>();
        String yandexapikey = (String) CustomConfig.properties.get("yandexapikey");
        String url = "https://geocode-maps.yandex.ru/1.x/?format=json&apikey=" + yandexapikey + "&geocode="
                + URLEncoder.encode(address, "UTF-8");
        JsonElement response = HttpRequests.sendGetReturnJsonE(url, true);
        String posAttr = response.getAsJsonObject().get("response").getAsJsonObject().get("GeoObjectCollection")
                .getAsJsonObject().get("featureMember").getAsJsonArray().get(0).getAsJsonObject().get("GeoObject")
                .getAsJsonObject().get("Point").getAsJsonObject().get("pos").getAsString();
        String lat = posAttr.substring(posAttr.indexOf(" ") + 1);
        String lon = posAttr.substring(0, posAttr.indexOf(" ") - 1);
        coords.put("lat", lat);
        coords.put("lon", lon);

        String mapUrl = "https://yandex.ru/maps/?pt=" + lon + "," + lat + "&z=15";
        toDiamantLog(dialogData, "Определены координаты клиента: <a href =\""
                        + mapUrl + "\"> Latitude = " + lat
                        + ", Longitude = " + lon + "</a>"
                , "green");
        return coords;
    }

    public static Map<String, ApiField> getNearestOffices(DialogData dialogData, String BEofficeCity,
                                                          String BEofficeAddress, String BEofficeService) {
        OutputMap output = new OutputMap();

        // Получение координат клиента по адресу
        HashMap<String, String> custCoords = null;
        try {
            custCoords = getCoordsByAddr(dialogData, BEofficeCity + ", " + BEofficeAddress);
        } catch (Exception e) {
            toDiamantLog(dialogData, "Не удалось получить координаты от Яндекс:", e);
            output.add("BEgetNearestOfficesStatus", "error");
            return output.get();
        }
        double customerLatitude = Double.parseDouble(custCoords.get("lat"));
        double customerLongitude = Double.parseDouble(custCoords.get("lon"));

        // Критерии отбора банкоматов
        List<String> requestedServices = new LinkedList<>();
        if (BEofficeService.equals("cashOperations")) {
            requestedServices.add("operations"); // для обратной совместимости
        } else {
            requestedServices.add(BEofficeService);
        }

        // Получение офисов из BSP
        Set<Servicepoint> offices;
        try {
            offices = getServicepoints(customerLatitude, customerLongitude, SPType.OFFICE, requestedServices, 2);
        } catch (Exception e) {
            toDiamantLog(dialogData, "Не удалось получить банкоматы от BSP:", e);
            output.add("BEgetNearestOfficesStatus", "error");
            return output.get();
        }

        int atmCounter = 1;
        for (Servicepoint sp : offices) {
            output.add("BEofficeDistance" + atmCounter, sp.getRoundedDistance());
            output.add("BEofficeAddress" + atmCounter, sp.getClearAddress());
            output.add("BEofficeSchedule" + atmCounter, ((Office) sp).getSchedule());
            atmCounter++;
        }

        if (output.get().size() > 0) {
            output.add("BEgetNearestOfficesStatus", "ok");
        } else {
            output.add("BEgetNearestOfficesStatus", "not found");
        }

        return output.get();
    }

    public static Office getOfficeById(String officeid) throws IOException {
        JsonElement JEofficeDetails = get2bspApi("servicepoint/v0/office/" + officeid + "/details", null);
        String offceDboId = JEofficeDetails.getAsJsonObject().get("data").getAsJsonObject().get("officeId").getAsString();
        String address = JEofficeDetails.getAsJsonObject().get("data").getAsJsonObject().get("address").getAsString();

        JsonElement JEofficeTimeTable = get2bspApi("/servicepoint/v0/officeTimeTable/" + offceDboId, null);
        JsonArray schedule = JEofficeTimeTable.getAsJsonObject().get("data").getAsJsonObject().get("fl").getAsJsonArray();
        return new Office(offceDboId, 0, 0, 0, address, schedule);
    }

    /**
     * Метод получения ближайших точек обслуживания (ДО/АТМ)
     *
     * @param latitude          Широта
     * @param longitude         Долгота
     * @param spType            тип точки обслуживания: ATM/OFFICE
     * @param requestedServices Требуемые критерии отбора точек.
     *                          ATM : dayNight, excludePartners, nfc, voiceAssistant, excludePartners, cashIn
     *                          OFFICE: mortgage, operations, premium, weekend, wheelchair, cardOrderAvailable, depositBox
     *                          , flservicing, ulservicing
     *                          Другие критерии игнорируются. При отсутствии критериев в выборку попадают все точки.
     * @param requiredCount     Требуемое количество точек обслуживания
     * @return
     * @throws Exception
     */
    public static Set<Servicepoint> getServicepoints(Double latitude, Double longitude, SPType spType
            , List<String> requestedServices, int requiredCount) throws Exception {
        Set<Servicepoint> output = new LinkedHashSet<>();

        JsonObject JOroot = new JsonObject();
        JOroot.addProperty("requestId", UUID.randomUUID().toString());

        JsonObject JOpage = new JsonObject();
        JOroot.add("page", JOpage);
        JOpage.addProperty("count", 10);

        JsonObject JOclientLocation = new JsonObject();
        JOroot.add("clientLocation", JOclientLocation);
        JOclientLocation.addProperty("latitude", latitude);
        JOclientLocation.addProperty("longitude", longitude);

        JsonObject JOcriteria = new JsonObject();
        JOroot.add("criteria", JOcriteria);


        JsonArray JAtypes = new JsonArray();
        JOcriteria.add("types", JAtypes);
        JAtypes.add(spType.toString());


        JsonObject JOcriteria2L = new JsonObject();
        if (spType == SPType.ATM) {
            JOcriteria.add("atmCriteria", JOcriteria2L);
            // АТМ с ограниченным проходом не показываем
            JOcriteria2L.addProperty("includeRestriction", false);
        } else if (spType == SPType.OFFICE) {
            JOcriteria.add("officeCriteria", JOcriteria2L);
            // вип-офисы не показываем
            JOcriteria2L.addProperty("vip", false);
        }

        List<String> allowedBinaryCriteria = Arrays.asList("cardOrderAvailable", "depositBox", "mortgage", "operations"
                , "premium", "weekend", "wheelchair", "dayNight", "excludePartners", "nfc", "voiceAssistant"
                , "excludePartners");
        for (String requestedService : requestedServices) {
            if (allowedBinaryCriteria.contains(requestedService)) {
                JOcriteria2L.addProperty(requestedService, true);
            } else if (requestedService.equals("cashIn")) {
                JsonArray currencies = new JsonArray();
                currencies.add("RUB");
                JOcriteria2L.add("pullCurrencies", currencies);
            }
        }


        for (int offset = 0; output.size() < requiredCount; offset += requiredCount) {
            JOpage.addProperty("offset", offset);
            JsonElement JEresp = bspApi("POST", "/servicepoint/v0/list", JOroot.toString(), null);
            String result = JEresp.getAsJsonObject().get("result").getAsJsonObject().get("code").getAsString();
            if (!result.equals("SRVP.00000")) {
                throw new Exception("servicepoint-api вернул не успешный код: " + JEresp);
            }

            JsonArray servicePoints = new JsonArray();
            if (JEresp.getAsJsonObject().has("data")
                    && JEresp.getAsJsonObject().get("data").getAsJsonObject().has("servicePoints")) {
                servicePoints = JEresp.getAsJsonObject().get("data").getAsJsonObject().get("servicePoints")
                        .getAsJsonArray();
            }

            for (JsonElement i : servicePoints) {
                JsonObject svcPoint = i.getAsJsonObject();
                String spId = svcPoint.get("id").getAsString();
                double spLatitude = svcPoint.get("latitude").getAsDouble();
                double spLongitude = svcPoint.get("longitude").getAsDouble();
                int spDistance = svcPoint.get("distance").getAsInt();

                // Получение адреса. Для ДО и АТМ добавляем город и регион.
                String spAddress = svcPoint.get("address").getAsString();
                if (spType == SPType.OFFICE) {
                    spAddress = svcPoint.get("region").getAsString() + ", "
                            + svcPoint.get("city").getAsString() + ", " + spAddress;
                } else { // АТМ
                    String spPartnerName = svcPoint.get("partnerName").getAsString();
                    if (spPartnerName.equals("bank") && svcPoint.get("city").getAsString() != null) {
                        spAddress = svcPoint.get("city").getAsString() + ", " + spAddress;
                    }
                    if (spPartnerName.equals("bank") && svcPoint.get("region").getAsString() != null) {
                        spAddress = svcPoint.get("region").getAsString() + ", " + spAddress;
                    }
                }

                // Получение расписания и инициализация объекта ДО или АТМ
                if (spType == SPType.ATM) {
                    String spPartnerName = svcPoint.get("partnerName").getAsString();
                    String spSchedule = svcPoint.get("atmSchedule").getAsJsonObject().get("scheduleString").isJsonNull() ?
                            null : svcPoint.get("atmSchedule").getAsJsonObject().get("scheduleString").getAsString();
                    output.add(new Atm(spId, spLatitude, spLongitude, spDistance, spAddress, spPartnerName, spSchedule));
                } else if /* Офисы + расписание ЮЛ */ (spType == SPType.OFFICE
                        && requestedServices.contains("ulservicing")
                        && svcPoint.get("officeSchedule").getAsJsonObject().get("ul").getAsJsonArray().size() > 0) {
                    JsonArray spSchedule = svcPoint.get("officeSchedule").getAsJsonObject().get("ul").getAsJsonArray();
                    output.add(new Office(spId, spLatitude, spLongitude, spDistance, spAddress, spSchedule));
                } else if /* Офисы + расписание кассы */ (spType == SPType.OFFICE
                        && requestedServices.contains("operations")
                        && svcPoint.get("officeSchedule").getAsJsonObject().get("cash").getAsJsonArray().size() > 0) {
                    JsonArray spSchedule = svcPoint.get("officeSchedule").getAsJsonObject().get("cash").getAsJsonArray();
                    output.add(new Office(spId, spLatitude, spLongitude, spDistance, spAddress, spSchedule));
                } else if /* Офисы + расписание ФЛ */ (spType == SPType.OFFICE
                        && !requestedServices.contains("ulservicing") && !requestedServices.contains("operations")) {
                    JsonArray spSchedule = svcPoint.get("officeSchedule").getAsJsonObject().get("fl").getAsJsonArray();
                    output.add(new Office(spId, spLatitude, spLongitude, spDistance, spAddress, spSchedule));
                }
                if (output.size() >= requiredCount) {
                    break;
                }
            }
        }
        return output;
    }

    /**
     * Тип точки обслуживания
     */
    public enum SPType {OFFICE, ATM}

}

