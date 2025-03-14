package ru.bank.bsp.statement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Список категорий каталогов
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalAvailableStatementCategoryDto {
    private String categoryId; // Идентификатор категории
    private String name; // Название категории
    private String imageUrl; // Ссылка на изображение категории
    private Integer categorySortOrder; // Порядковый номер отображения категории в каталоге
    private List<AvailableStatementInfoDto> statements; // Список доступных справок в категории
}
