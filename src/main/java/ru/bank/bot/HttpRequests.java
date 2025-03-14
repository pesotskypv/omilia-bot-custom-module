package ru.bank.bot;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.omilia.diamant.dialog.components.fields.ApiField;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import ru.bank.bot.utils.OutputMap;
import ru.bank.bot.utils.Utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import static ru.bank.bot.utils.Utils.toDiamantLog;

public class HttpRequests {
    public static void main(String[] args) throws Exception {
        // Для тестирования HTTP-запросов
        // java -cp BotModule.jar ru.bank.bot.HttpRequests https://crm
        URL urlObj = new URL(args[0]);
//        URL urlObj = new URL("https://crmcert/siebel/app/eai_anon_rus/rus?SWEExtSource=AnonWebService&SWEExtCmd=Execute");
        HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection(Proxy.NO_PROXY);
        httpConnection.setConnectTimeout(5000);
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setDoOutput(true);
        System.out.println(Utils.Stream2String(httpConnection.getInputStream()));
    }

    public static JsonElement sendPostReturnJson(String url, String body, boolean useproxy) {
        try {
            URL urlObj = new URL(url);
            Proxy proxy;
            if (useproxy) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("nsproxy.bank.ru", 8080));
            } else {
                proxy = Proxy.NO_PROXY;
            }
            HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection(proxy);
            httpConnection.setConnectTimeout(25000);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setDoOutput(true);
            byte[] jsonReqBytes = body.getBytes(StandardCharsets.UTF_8); // Для передачи данных в стрим нужно обернуть их в байты
            OutputStream reqOutputStream = httpConnection.getOutputStream(); // Создание исходящего стрима
            reqOutputStream.write(jsonReqBytes); // Отправка тела запроса в стрим
            reqOutputStream.close();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) { // Если HTTP 200 - Возвращаем тело
                return JsonParser.parseReader(new InputStreamReader(httpConnection.getInputStream(),
                        StandardCharsets.UTF_8));
            } else {
                String response = "";
                if (httpConnection.getErrorStream() == null) { // Если код не 200, а тела ошибки нет, возвращаем только код HTTP
                    return JsonParser.parseString("{ \"error\": \"HTTP error. Response code "
                            + httpConnection.getResponseCode() + ". Full message in Diamant log.\"}");
                } else {      // Если тело ошибки есть, сохраняем его в стринг для последующей обработки, и в лог.
                    response = Utils.Stream2String(httpConnection.getErrorStream());
                    if (CustomConfig.glogger != null) CustomConfig.glogger.logInfo(response);
                }


                // Проверки на наличие известных фраз в респонсах с ошибками
                if (response.contains("телефонный номер уже используется")) { // Для отправки лидов Авто: если номер уже есть в базе
                    return JsonParser.parseString("{ \"error\": \"sentBefore\"}");
                } else {      // Если ни одной известной ошибки не нашлось
                    return JsonParser.parseString("{ \"error\": \"HTTP error. Response code "
                            + httpConnection.getResponseCode() + ". Full message in Diamant log.\"}");
                }
            }
        } catch (Exception e) {
            toDiamantLog("Ошибка HTTP:", e);
            return JsonParser.parseString("{ \"error\": \"HTTP error\"}");
        }
    }

    public static JsonElement sendPostReturnJsonE(String url, String body, boolean useproxy) throws IOException {
        URL urlObj = new URL(url);
        Proxy proxy;
        if (useproxy) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("nsproxy.bank", 8080));
        } else {
            proxy = Proxy.NO_PROXY;
        }
        HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection(proxy);
        httpConnection.setConnectTimeout(25000);
        httpConnection.setRequestMethod("POST");
        httpConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setDoOutput(true);
        byte[] jsonReqBytes = body.getBytes(StandardCharsets.UTF_8); // Для передачи данных в стрим нужно обернуть их в байты
        OutputStream reqOutputStream = httpConnection.getOutputStream(); // Создание исходящего стрима
        reqOutputStream.write(jsonReqBytes); // Отправка тела запроса в стрим
        reqOutputStream.close();
        return JsonParser.parseReader(new InputStreamReader(httpConnection.getInputStream(), StandardCharsets.UTF_8));
    }

    public static JsonElement sendGetReturnJson(String url, boolean useproxy) {
        try {
            URL urlObj = new URL(url);
            Proxy proxy;
            if (useproxy) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("nsproxy.bank.ru", 8080));
            } else {
                proxy = Proxy.NO_PROXY;
            }
            HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection(proxy);
            httpConnection.setConnectTimeout(5000);
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setDoOutput(true);
            return JsonParser.parseReader(new InputStreamReader(httpConnection.getInputStream(),
                    StandardCharsets.UTF_8));
        } catch (Exception e) {
            toDiamantLog("Ошибка HTTP:", e);
            return JsonParser.parseString("{ \"error\": \"HTTP error\" }");
        }
    }

    public static JsonElement sendGetReturnJsonE(String url, boolean useproxy) throws IOException {
        URL urlObj = new URL(url);
        Proxy proxy;
        if (useproxy) {
            proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("nsproxy.bank.ru", 8080));
        } else {
            proxy = Proxy.NO_PROXY;
        }
        HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection(proxy);
        httpConnection.setConnectTimeout(5000);
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setDoOutput(true);
        return JsonParser.parseReader(new InputStreamReader(httpConnection.getInputStream(), StandardCharsets.UTF_8));
    }

    public static JsonElement get2bspApi(String pathToResource, String xAuthUser) throws IOException {
        String BspAddr = (String) CustomConfig.properties.get("BspApiAddr");
        BspAddr = BspAddr.replaceAll("/$", "");       // Если у адреса в конце слеш - отрезаем
        if (!pathToResource.startsWith("/")) {
            pathToResource = "/" + pathToResource;
        }   // Если path начинается не со слеша - добавляем
        URL urlObj = new URL(BspAddr + pathToResource);
        HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection();
        httpConnection.setConnectTimeout(5000);
        httpConnection.setRequestMethod("GET");
        httpConnection.setRequestProperty("Content-Type", "application/json; utf-8");
        if (xAuthUser != null) {
            httpConnection.setRequestProperty("X-auth-user", xAuthUser);
        }
        httpConnection.setRequestProperty("Accept", "application/json");
        httpConnection.setRequestProperty("Authorization", "");
        toDiamantLog("Request to BSP API: GET " + urlObj);
        httpConnection.setDoOutput(true);
        return JsonParser.parseReader(new InputStreamReader(httpConnection.getInputStream(), StandardCharsets.UTF_8));
    }

    public static JsonElement bspApi(String method, String pathToResource, String body, String xAuthUser)
            throws IOException {
        String BspAddr = (String) CustomConfig.properties.get("BspApiAddr");
        BspAddr = BspAddr.replaceAll("/$", "");
        if (!pathToResource.startsWith("/"))
            pathToResource = "/" + pathToResource;
        String url = BspAddr + pathToResource;
        JsonElement JEreturn = null;

        toDiamantLog("Request to BSP API: " + method + " " + url + "\n"
                + body.replaceAll("\\d{4} ?\\d{4} ?\\d{4} ?\\d{4}", "****card_probably_was_here****"));

        if (method.equals("PATCH")) {
            CloseableHttpClient closeableHttpClient = HttpClients.custom().build();
            StringEntity entity = new StringEntity(body, StandardCharsets.UTF_8);
            HttpUriRequest request = RequestBuilder.patch()
                    .setUri(url)
                    .setHeader("Content-Type", "application/json; utf-8")
                    .setHeader("Accept", "application/json")
                    .setHeader("X-auth-user", xAuthUser)
                    .setEntity(entity).build();
            HttpResponse response = closeableHttpClient.execute(request);
            JEreturn = JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent(),
                    StandardCharsets.UTF_8));
        } else {
            URL urlObj = new URL(url);
            HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection();
            httpConnection.setConnectTimeout(5000);
            httpConnection.setRequestMethod(method);
            httpConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            if (xAuthUser != null) {
                httpConnection.setRequestProperty("X-auth-user", xAuthUser);
            } else {
                httpConnection.setRequestProperty("Authorization", "");
            } // Костыль для servicepoint-api
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setDoOutput(true);
            byte[] jsonReqBytes = body.getBytes(StandardCharsets.UTF_8);
            OutputStream reqOutputStream = httpConnection.getOutputStream();
            reqOutputStream.write(jsonReqBytes);
            reqOutputStream.close();
            JEreturn = JsonParser.parseReader(new InputStreamReader(httpConnection.getInputStream(),
                    StandardCharsets.UTF_8));
        }
        return JEreturn;
    }

    public static void sendPost(String url, boolean useproxy, String user, String pass) {
        try {
            URL urlObj = new URL(url);
            Proxy proxy;
            if (useproxy) {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("nsproxy.bank.ru", 8080));
            } else {
                proxy = Proxy.NO_PROXY;
            }
            HttpURLConnection httpConnection = (HttpURLConnection) urlObj.openConnection(proxy);
            httpConnection.setConnectTimeout(5000);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json; utf-8");
            httpConnection.setRequestProperty("Accept", "application/json");
            httpConnection.setRequestProperty("Authorization", "Basic " + Base64.getEncoder().encodeToString((user + ":"
                    + pass).getBytes()));
            httpConnection.setDoOutput(true);
            httpConnection.setFixedLengthStreamingMode(0);
            httpConnection.getInputStream();
        } catch (Exception ignored) {
        }
    }

    public static Map<String, ApiField> sendAnswerToHr(String answer, DialogData dialogData) {
        OutputMap output = new OutputMap();
        try {
            String HrAbpm = (String) CustomConfig.properties.get("HrAbpm");
            String url = HrAbpm + "/v1/applications/api/candidate-answer";
            JsonObject reqBody = new JsonObject();
            reqBody.addProperty("phone", dialogData.getFieldValue("Ani"));
            reqBody.addProperty("answer", answer);
            toDiamantLog(dialogData, "Тело запроса в HR aBPM: " + reqBody.toString());
            sendPostReturnJsonE(url, reqBody.toString(), false);
            output.add("BEsendAnswerToHrStatus", "ok");
        } catch (Exception e) {
            toDiamantLog(dialogData, "Не удалось отправить реакцию в HR ABPM:", e);
            output.add("BEsendAnswerToHrStatus", "error");
        }
        return output.get();
    }
}
