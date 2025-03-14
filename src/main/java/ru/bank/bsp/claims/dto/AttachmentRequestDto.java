package ru.bank.bsp.claims.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Информация о прикрепленном файлее к заявке
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentRequestDto {
    private String id; // Идентификатор
    private String documentType; // Тип документа в elib
    private String name; // Название файла
    private String contentType; // Расширение файла
    private Integer size; // Размер файла в байтах
    private Boolean elibPublished; // Признак необходимости публикации документа в Elib
}
