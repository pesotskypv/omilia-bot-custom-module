package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Параметры заявки
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClaimsIssueExecDataDto {
    private String description; // Содержание заявки
    private String typeResult; // Способ получения ответа на обращение
    private String addressResult; // Адрес
    private String claimsTypeCode; // Код типа заявления
    private Boolean notToCall; // Признак "Не звонить мне"
    private Boolean signature_sign; // Признак подписания на стороне РМБ
    private List<AttachmentRequestDto> attachments; // Список прикрепленных файлов
}
