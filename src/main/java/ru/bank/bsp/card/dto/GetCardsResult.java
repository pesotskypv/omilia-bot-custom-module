package ru.bank.bsp.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Список карт клиента
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCardsResult {
    private Result result;
    private List<GetCardsResponse> data;
}
