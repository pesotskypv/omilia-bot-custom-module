package ru.bank.bsp.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.loyalty.model.CardType;
import ru.bank.bsp.loyalty.model.LoyaltyStatus;

/**
 * Массив карт, участвующих в программе лояльности
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyProgramCardDto {
    private CardType cardType; // Тип карты
    private String bonusType; // Тип бонуса
    private String calculationDate; // Месяц расчёта бонусов
    private String cardId; // Идентификатор карты
    private String loyaltyId; // Уникальный идентификатор ПЛ
    private LoyaltyStatus loyaltyStatus; // Статус подключения карты к ПЛ
    private Integer monthBonus; // Количество бонусов за месяц
    private String paymentDate; // Дата выплаты бонусов. Заполняется только для кешбэка
    private String previousCalculationDate; // Предыдущий месяц расчёта бонусов
    private Integer previousMonthBonus; // Бонусы за предыдущий месяц
    private String previousPaymentDate; // Дата выплаты бонусов в предыдущем месяце. Заполняется только для кешбэка
    private Double turnover;
    private Double yearBonus; // Сумма накопленных бонусов за текущий год
}
