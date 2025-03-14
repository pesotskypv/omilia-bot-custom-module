package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.CardProductType;
import ru.bank.bsp.finance.model.CardState;
import ru.bank.bsp.finance.model.MainCard;
import ru.bank.bsp.finance.model.PinGenerationMethod;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardExtendedDto {
    private String productUid; // Идентификатор карты
    private String branch; // Филиал карты
    private String cardNumber; // Маскированный номер карты
    private String name; // Наименование карты по умолчанию
    private String expirationDate; // Срок окончания действия (пластик)
    private String expirationBusinessDate; // Срок окончания действия (продленный)
    private String paymentSystem; // Платежная система
    private String primaryAccountUid; // Идентификатор основного счета карты
    private CardState state; // Статус карты
    private CardProductType productType; // Тип карты
    private Boolean digital; // Признак цифровой карты
    private Boolean corporate; // Признак корпоративной карты
    private String blockCode; // Технический код статуса блокировки карты в ITM
    private String blockCodeDetail; // Детализация блокировки
    private String blockDate; // Дата блокировки карты
    private MainCard mainCard; // Тип карты (основная / доп / на 3 лицо)
    private String holder; // Держатель карты
    private String embossedName; // Эмбоссированное имя
    private PinGenerationMethod pinGenerationMethod; // Способ генерации PIN
    private Boolean reissuingAvailable; // Признак возможности перевыпуска
    private Boolean nameless; // Карта не именная?
    private Boolean tokenizationFlag; // Признак возможности подключения Apple Pay
}
