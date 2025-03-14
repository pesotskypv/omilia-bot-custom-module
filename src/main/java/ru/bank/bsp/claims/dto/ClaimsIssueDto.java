package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Данные заявки с информацией о клиенте
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsIssueDto {
    private String issueId; // Идентификатор заявки
    private String state; // Статус заявки
    private String firstName; // Имя клиента
    private String middleName; // Отчество клиента
    private String lastName; // Фамилия клиента
    private String documentType; // Тип документа в Elib
    private String descriptionHint; // Подсказка к тексту заявления
    private String attachHint; // Подсказка к прикрепляемому файлу
    private String phone; // Телефон клиента
    private String clientBirthday; // День рождения клиента
    private List<DeliveryTypeDto> deliveryType; // Способы получения ответа на обращение
    private String descriptionPlaceholder; // Шаблон для предзаполненного заявления
}
