package ru.bank.bsp.card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.card.model.Status;
import ru.bank.bsp.card.model.Type;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetCardsResponse {
    private String id;
    private String cardNumber;
    private String name;
    private String expirationDate;
    private String paymentSystem;
    private String primaryAccountNumber;
    private Status status;
    private Type type;
    private Boolean digital;
    private Boolean isVisible;
    private Integer sortOrder;
    private Boolean showOnMainScreen;
    private Integer sortOrderMainScreen;
    private Boolean corporate;
    private String backgroundUrl;
    private String cardLocation; // Код отделения карты
    private Boolean tokenizationFlag;
    private String deliveryId; // Идентификатор доставки (для карт в статусе курьерской доставки)
    private Boolean kidsCard; // Признак детской карты
}
