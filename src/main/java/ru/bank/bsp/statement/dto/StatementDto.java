package ru.bank.bsp.statement.dto;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.bank.bsp.statement.model.StatementType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatementDto {
    private String name; // Название справки
    private String fileFormat; // Формат файла
    private StatementType type; // Тип справки
    private String expireDate; // Дата, до которой доступна справка
    private String createDate; // Дата создания справкии
    private String status; // Статус готовности справки
    private String description; // Дополнительная информация по созданной справке
    private String message; // Сообщение об ошибке при формировании справки

    @SerializedName(value = "URL", alternate = {"url", "Url"})
    private String url; // Ссылка для скачивания справки
}
