package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Форматы файлов, в которых можно заказать справку
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileFormatDto {
    private String fileFormat;
    private Integer sortOrder;
}
