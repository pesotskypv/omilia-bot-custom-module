package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.AgreementType;
import ru.bank.bsp.finance.model.State;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BrokerageExtendedDto {
    private String productUid; // Уникальный идентификатор продукта
    private String agreementId; // Идентификатор брокерского договора
    private String createDate; // Дата создания счета
    private AgreementType agreementType; // Тип договора
    private State state; // Статус счета
    private BrokerageAccountDto accounts;
    private BrokerageAssetDto assets; // Активы счета
    private AssetsTotalDto assetsTotal; // Активы счета
}
