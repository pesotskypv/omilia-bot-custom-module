package ru.bank.client.dto;

import com.google.gson.JsonElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BotHttpResponseDto {
    int statusCode;
    JsonElement responseJson;
}
