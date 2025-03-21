package ru.bank.bsp.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCardLimitResponse {
    private String cardId;
    private List<CardLimitResponseDto> limit;
}
