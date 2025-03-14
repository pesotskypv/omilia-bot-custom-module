package ru.bank.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPatch;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.ssl.TrustStrategy;
import ru.bank.bot.DialogData;
import ru.bank.client.dto.BotHttpResponseDto;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;
import static ru.bank.util.Utils.genericLogInfo;

public class BotHttpClient {

    public static BotHttpResponseDto invokeGet(HttpGet request, DialogData dialogData) {
        genericLogInfo("Выполняется GET-запрос: " + request);

        BotHttpResponseDto responseDto = null;
        final TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        final SSLContext sslContext;
        try {
            sslContext = SSLContexts.custom()
                    .loadTrustMaterial(null, acceptingTrustStrategy).build();
        } catch (GeneralSecurityException e) {
            dialogLogWarn(dialogData, "Ошибка при выполнении GET-запроса: " + e);
            return responseDto;
        }
        final SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();
        final BasicHttpClientConnectionManager connectionManager =
                new BasicHttpClientConnectionManager(socketFactoryRegistry);

        try (CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build()) {
            responseDto = httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                JsonElement parsed = JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8));
                return BotHttpResponseDto.builder().statusCode(statusCode).responseJson(parsed).build();
            });
        } catch (IOException e) {
            dialogLogWarn(dialogData, "Ошибка при выполнении GET-запроса: " + e);
        }

        genericLog("Ответ на GET-запрос: " + responseDto);
        return responseDto;
    }

    public static BotHttpResponseDto invokePost(HttpPost request, DialogData dialogData) {
        genericLogInfo("Выполняется POST-запрос: " + request);

        BotHttpResponseDto responseDto = null;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            responseDto = httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                JsonElement parsed = JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8));
                return BotHttpResponseDto.builder().statusCode(statusCode).responseJson(parsed).build();
            });
        } catch (IOException e) {
            dialogLogWarn(dialogData, "Ошибка при выполнении POST-запроса: " + e);
        }

        genericLog("Ответ на POST-запрос: " + responseDto);
        return responseDto;
    }

    public static BotHttpResponseDto invokePatch(HttpPatch request, DialogData dialogData) {
        genericLogInfo("Выполняется PATCH-запрос: " + request);

        BotHttpResponseDto responseDto = null;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            responseDto = httpClient.execute(request, response -> {
                int statusCode = response.getCode();
                JsonElement parsed = JsonParser.parseReader(new InputStreamReader(response.getEntity().getContent(),
                        StandardCharsets.UTF_8));
                return BotHttpResponseDto.builder().statusCode(statusCode).responseJson(parsed).build();
            });
        } catch (IOException e) {
            dialogLogWarn(dialogData, "Ошибка при выполнении PATCH-запроса: " + e);
        }

        genericLog("Ответ на PATCH-запрос: " + responseDto);
        return responseDto;
    }
}
