package ru.bank.bot;

import com.omilia.diamant.application.Application;
import com.omilia.diamant.dialog.Dialog;
import com.omilia.diamant.dialog.DialogEvent;
import com.omilia.diamant.dialog.components.fields.ApiField;
import com.omilia.diamant.dialog.components.fields.FieldStatus;
import com.omilia.diamant.dialog.components.fields.FieldsContainer;
import com.omilia.diamant.loggers.DialogLogger;
import com.omilia.diamant.managers.DialogManager;
import ru.bank.bot.model.Product;
import ru.bank.bsp.card.dto.GetCardsResult;
import ru.bank.bsp.customerinfo.dto.ResponseUserInfoResponseDto;
import ru.bank.bsp.finance.dto.FinanceExtendedResponse;
import ru.bank.bsp.finance.dto.ResponseNewFinanceDto;
import ru.bank.bsp.loyalty.dto.ResponseListLoyaltyProgramDto;
import ru.bank.bsp.statement.dto.ResponseListStatementDto;
import ru.bank.bsp.statement.dto.ResponsePersonalAvailableStatementsDto;
import ru.bank.bsp.tariff.dto.TariffResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import static ru.bank.bot.utils.Utils.outputMap2String;
import static ru.bank.util.Utils.dialogLog;
import static ru.bank.util.Utils.dialogLogGreen;
import static ru.bank.util.Utils.dialogLogWarn;
import static ru.bank.util.Utils.genericLog;

// Класс для хранения данных, специфичных для конкретного диалога
public class DialogData {
    public DialogLogger dlogger = null; // Лог диалога
    public String siebelId = null;
    public String xAuthUser = null; // Заголовок аутентификации ДБО
    public FieldsContainer fieldsContainer = null; // Филды
    public Map<String, String> testFieldsContainer = null; // Замена FieldsContainer для тестов
    public Set<Product> productsWithBalance = null; // Все продукты с балансом: карты и счета
    public List<String> intentHistory = new LinkedList<>();
    public TreeMap<Integer, String> stepsIntentsHistory = new TreeMap<>();
    public ResponseUserInfoResponseDto userInfo = null; // Информация по клиенту
    public ResponsePersonalAvailableStatementsDto personalAvailableStatements = null; // Список доступных для формирования справок
    public ResponseListStatementDto listStatement = null; // Информация по заказанным справкам
    public ResponseNewFinanceDto finance = null; // Список финансовых продуктов клиента
    public FinanceExtendedResponse financeExtended = null; // Список финансовых продуктов клиента
    public GetCardsResult cards = null; // Список карт клиента
    public ResponseListLoyaltyProgramDto loyaltyPrograms = null; // Подключенные ПЛ клиента
    public TariffResult tariff = null; // Детали тарифов клиента

    public void setFieldValue(String field, String value) {
        if (fieldsContainer == null && testFieldsContainer != null) {
            testFieldsContainer.put(field, value);
            genericLog("Поле " + field + " обновлено: " + value);
        } else if (fieldsContainer != null) {
            if (fieldsContainer.doesNotContainField(field)) {
                dialogLogWarn(this, "Поле " + field + " не создано. Значение не обновлено: " + value);
            } else if (value.equals(fieldsContainer.getField(field).getFieldInstanceValue())) {
                genericLog("Обновление поля " + field + " не требуется. Значение: " + value);
            } else {
                fieldsContainer.getField(field).updateInstanceValue(value);
                dialogLogGreen(this, "Поле " + field + " обновлено: " + value);
            }
        } else {
            dialogLogWarn(this, "FieldsContainer пуст.");
        }
    }

    /**
     * Получение значения Field.
     * В метод заложены все проверки Field, и при невозможности получить значение, в лог будет записана причина.
     * В случае тестового запуска, значение будет взято из тестового контейнера.
     */
    public String getFieldValue(String field) {
        if (fieldsContainer == null && testFieldsContainer == null) {
            return null;
        } else if (fieldsContainer != null && fieldsContainer.doesNotContainField(field)) {
            dialogLogWarn(this, "Поле " + field + " не объявлено во Flow");
            return null;
        } else if (fieldsContainer != null && fieldsContainer.getField(field).isDefined()) {
            return fieldsContainer.getField(field).getFieldInstanceValue();
        } else if (fieldsContainer != null && !fieldsContainer.getField(field).isDefined()) {
            dialogLogWarn(this, "Поле " + field + " не определено (Undefined)");
            return null;
        } else if (fieldsContainer == null && testFieldsContainer != null && testFieldsContainer.containsKey(field)) {
            return testFieldsContainer.get(field); // Для тестов
        } else {
            return null;
        }
    }

    /**
     * Метод возвращает текущий интент, если он является финальным (не Abmbiguous и др.).
     * Если интент является клоном (на конце индекс), то возвращается оригинальное название.
     */
    public String getOriginalIntent() {
        String currentIntent = this.fieldsContainer.getIntent().getIntentEntityName();
        if (this.fieldsContainer.getIntent().getStatus() != FieldStatus.DEFINED) {
            return null;
        }
        if (currentIntent.matches(".*\\d+$")) {
            String intentToCheck = currentIntent.replaceAll("\\d+$", "");
            String appName = this.fieldsContainer.getDialogIDField().getApp().getAppName();
            for (Map.Entry<String, Application> entry :
                    DialogManager.getInstance().getApplicationManager().getApplications().entrySet()) {
                if ((entry.getKey()).equals(appName)) {
                    if ((entry.getValue()).getNluApplicationHandler().getIntentNames().contains(intentToCheck))
                        return intentToCheck;
                    return currentIntent;
                }
            }
        } else {
            return currentIntent;
        }
        return currentIntent;
    }

    /**
     * Проверка, является ли интент повторным в диалоге (с учётом интентов-клонов с индексами).
     * При каждом запросе интент добавляется в историю.
     */
    public void checkIntentInHistory(Map<String, ApiField> outputMap) {
        String intent = getOriginalIntent();
        if (intent != null) {
            dialogLog(this, "История финальных интентов в диалоге: " + this.intentHistory);
            outputMap.put("BEoriginalIntent", ApiField.builder()
                    .name("BEoriginalIntent")
                    .value(intent)
                    .status(FieldStatus.DEFINED).build());
            if (this.intentHistory.contains(intent)) {
                outputMap.put("BEintentRepeatedInDialog", ApiField.builder()
                        .name("BEintentRepeatedInDialog")
                        .value("true")
                        .status(FieldStatus.DEFINED).build());
            } else {
                outputMap.put("BEintentRepeatedInDialog", ApiField.builder()
                        .name("BEintentRepeatedInDialog")
                        .status(FieldStatus.UNDEFINED).build());
            }
            this.intentHistory.add(intent);
        } else {
            outputMap.put("BEoriginalIntent", ApiField.builder()
                    .name("BEoriginalIntent")
                    .status(FieldStatus.UNDEFINED).build());
        }
        dialogLogGreen(this, "Выходные переменные: " + outputMap2String(outputMap));
    }

    public String getDialogID() {
        return fieldsContainer.getDialogIDField().getFieldInstanceValue();
    }

    public Optional<Dialog> getDialog() {
        if (dlogger == null) {
            return Optional.empty(); // Тестирование, а не реальный диалог
        }

        Map<String, Dialog> activeDialogs = DialogManager.getInstance().getDialogController().getActiveDialogs();
        return activeDialogs.values().stream()
                .filter(x -> x.getDialogSession().getDialogId().equals(getDialogID()))
                .findFirst();
    }

    /**
     * Последнее сообщение пользователя
     */
    public Optional<String> getUserUtterance() {
        if (getDialog().isPresent()) {
            Optional<String> userUtterance = Optional.ofNullable(getDialog().get().getDialogState().getUserUtterance());
            if (userUtterance.isPresent() && userUtterance.get().length() > 0) {
                return userUtterance;
            }
        }
        return Optional.empty();
    }

    /**
     * Получение текущего FieldToElicit (при наличии)
     */
    public Optional<String> getFieldToElicit() {
        try {
            return Optional.of(Objects.requireNonNull(getDialog().orElse(null)).getDialogState().getLastAction()
                    .getFieldsToElicit());
        } catch (Exception ignored) {
        }
        return Optional.empty();
    }

    /**
     * Получение имени таргета
     */
    public String getTargetName() {
        if (getDialog().isPresent()) {
            return getDialog().get().getDialogState().getTarget().getName();
        }
        return null;
    }

    /**
     * Получение имени Action
     */
    public String getLastActionName() {
        if (getDialog().isPresent()) {
            return getDialog().get().getDialogState().getLastAction().getName();
        }
        return null;
    }

    /**
     * Текущий эвент
     */
    public Optional<DialogEvent> getEvent() {
        if (getDialog().isPresent()) {
            if (getDialog().get().getDialogState().getReactionEffect() != null) {
                return Optional.ofNullable(getDialog().get().getDialogState().getReactionEffect().getEventType());
            }
        }
        return Optional.empty();
    }

    /**
     * Тот же ли самый сейчас интент, что и на предыдущем шаге?
     */
    public boolean isSameIntentReturned(String intent) {
        boolean output = stepsIntentsHistory.size() > 0 // Если в истории что-то есть
                && getDialog().isPresent() // ... диалог сушествует => не тест ...
                && !stepsIntentsHistory.lastKey().equals(getDialog().get().getStep()) // ... шаг сменился ...
                && stepsIntentsHistory.lastEntry().getValue().equals(intent); // ... интент тот же

        // Добавляем текущий интент в историю
        if (getDialog().isPresent()) {
            stepsIntentsHistory.put(getDialog().get().getStep(), intent);
        }

        return output;
    }

    /**
     * Сброс реакции на DIALOG_NO_MATCH_EVENT если она стоит в очереди.
     * Используется для случаев когда Inhouse NLU нашёл интент или поле.
     */
    public void resetNoMatchReaction() {
        // Отмена исполнения реакции NO_MATCH если NLU определил интент или поле
        if (getEvent().isPresent()
                && getEvent().get().equals(DialogEvent.DIALOG_NO_MATCH_EVENT)) {
            dialogLog(this, "Выполняется Reset Dialog State для сброса эвента: "
                    + this.getEvent().get());
            Objects.requireNonNull(getDialog().orElse(null)).getDialogState().resetState();
        }
    }
}
