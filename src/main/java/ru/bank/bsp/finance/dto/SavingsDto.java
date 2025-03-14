package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.Currency;
import ru.bank.bsp.finance.model.SavingsProductType;
import ru.bank.bsp.finance.model.State;

/**
 * Массив текущих, сберегательных и депозитных счетов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavingsDto {
    private String productUid; // Уникальный идентификатор счета/продукта
    private String productId; // Уникальный идентификатор продукта по dboId. Оставлен для обратной совместимости
    private String accountCode; // 13-значный номер счета
    private String branch; // Филиал счета
    private Double amount; // Остаток на счету
    private Double reserveAmount; // Зарезервированные средства
    private Double creditAmount; // Лимит овердрафта / Кредитный лимит
    private String openDate; // Дата открытия счета/продукта
    private String closeDate; // Дата закрытия счета/продукта
    private String name; // Пользовательское название счета либо при отсутствии название по умолчанию
    private Currency currency; // Валюта счета
    private State state; // Статус счета продукта
    private SavingsProductType productType; // Тип продукта
    private Double maxAmount; // Максимальная допустимая сумма на счете
    private Boolean kidsAccount; // Признак детского счета
}
