package ru.bank.bsp.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.finance.model.TariffInfoLink;

import java.util.List;

/**
 * Результат запроса получения деталей продукта
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDetailsObjectIdDto {
    private String productId;
    private List<ProductDetailsItemDto> details;
    private List<ProductDetailsItemDto> tariffDetails;
    private TariffInfoLink tariffInfoLink;
    private List<ExistingInstallmentDTO> installments;
    private ExistingInstallmentInfoDto existinginstallmentInfo;
    private BrokerageDto brokerageAgreement;
    private OpifDetailsDto opif;
    private IisDuDetailsDto iisDu;
    private DuDetailsDto du;
    private SduDetailsDto sdu;
    private InvestRequisitesDto requisites;
}
