package ru.bank.bsp.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.loyalty.model.Availability;
import ru.bank.bsp.loyalty.model.Program;

import java.util.List;

/**
 * ПЛ доступные пользователю и карты, которые подключены к ПЛ
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoyaltyProgramDto {
    private Availability availability; // Атрибут, определяющий, доступно ли подключение к ПЛ
    private String calculationDateForTotalPreviousMonthBonus; // Дата, за которую рассчитывается общий кешбэк предыдущего месяца
    private List<ConditionDto> conditions; // Массив условий ПЛ
    private String link; // Ссылка на правила программы
    private List<LoyaltyProgramCardDto> loyalties; // Массив карт, участвующих в программе лояльности
    private String paymentDateForTotalPreviousMonthBonus; // Дата выплаты общего кешбэка предыдущего месяца
    private Program program; // Тип ПЛ
    private Integer programSortOrder; // Порядок ПЛ для отображения в МП
    private String programDescription; // Описание программы для отображения на странице деталей
    private String programDetailIcon; // Ссылка на картинку для программы, которая используется для отображения на экране с деталями программы лояльности
    private String programListIcon; // Ссылка на иконку программы для отображения в списке программ лояльности
    private String programName; // Название программы
    private String programShortDescription; // Краткое описание программы для отображения в списке программ лояльности
    private Double totalBonus; // Общий размер бонусов. За весь период для travel. За месяц по всем картам для cashback. За месяц по карте для AFFINITY
    private Double totalPreviousMonthBonus; // Общая сумма накопленного кешбэка за предыдущий месяц
}
