package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.CardProductType;
import ru.bank.bsp.finance.model.CardState;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDto {
    private String productUid; // Идентификатор карты
    private String productId; // Идентификатор карты (оставлен для обратной совместимости)
    private String cardNumber; // Маскированный номер карты
    private String name; // Название карты (пользовательское или по умолчанию)
    private String expirationDate; // Срок окончания действия (пластик)
    private String paymentSystem; // Платежная система
    private String primaryAccountUid; // Идентификатор основного счета карты
    private String primaryAccountCode; // 13-значный номер основного счета карты
    private CardState state; // Статус карты
    private CardProductType productType; // Тип карты
    private Boolean digital; // Признак цифровой карты
    private String cardLocation; // Код отделения карты
    private String corporate; // Карта корпоративная?
    private Boolean tokenizationFlag; // Признак возможности подключения Apple Pay
}
