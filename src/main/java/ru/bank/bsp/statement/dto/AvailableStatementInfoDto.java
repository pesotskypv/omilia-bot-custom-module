package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.statement.model.StatementType;

import java.util.List;

/**
 * Список доступных справок в категории
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AvailableStatementInfoDto {
    private String name; // Название справки
    private String form; // Форма изготовления справки
    private String formName; // Название формы изготовления справки
    private String period; // Период изготовления справки
    private StatementType type; // Тип справки
    private List<LanguageDto> languages; // Список доступных языков для изготовления справки
    private List<ProductInfoDto> products; // Продукты, по которым можно сформировать справку
    private List<FullClientAddressDto> addresses; // Список адресов клиента (не используется)
    private List<FileFormatDto> fileFormats; // Форматы файлов, в которых можно заказать справку
}
