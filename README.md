# omilia-bot-custom-module
* Интеграционный модуль для текстовых и голосовых ботов Omilia DiaManT
* В проекте отсутствует коммерческий пакет com.omilia.diamant.*

## Список интеграций и реализованных сервисов в боте:

### БД

#### noSQL Redis
ru.bank.aid.service.AidService Получение часового пояса клиента по мобильному номеру

#### SQL 
* ru.bank.aid.service.CcReportService.getEmployeeLogin Получение логина сотрудника по SiebelId
* ru.bank.aid.service.CcReportService.getCcReport Получение отчётов для чат-бота отчётности ЮЛ reportBot
* ru.bank.bot.SqlRequest в частности:
- Получение ответов бота
- Чтение/запись данных для обучающего бота
- Получение динамических ICR'ов
- Работа с данными ownNLU

### Сервис NLG Генерации естественного языка (Natural Language Generation)
ru.bank.bot.nlg Преобразование данных в читаемый текст

### Сервис отправки на email кодов подтверждений и технических уведомляющий об исключениях
ru.bank.bot.Email

### Сервис отправки ответов в отдел кадров
ru.bank.bot.HttpRequests.sendAnswerToHr

### Сервис подбора ДО и ATM
ru.bank.bot.doatm Поиск ближайших банкоматов и офисов по различным критериям

### Продуктовые сервисы
* CustomerInfo-API Сервис для получения информации по клиенту
* Finance-API Сервис для получения информации о списке финансовых продуктов клиента
* Card-API Сервис для работы с картами клиента
* Tariff-API Сервис для получения деталей тарифа
* Mortgage-API Сервис для работы с ипотечными кредитами
* Loyalty-API Сервис для операций с программой лояльности
* Claims-API Сервис для работы с заявлениями в свободной форме
* Statement-API Сервис заказа справок и выписок

### MFM Сервис отправки SMS
ru.bank.bot.Mfm

### CTI Omni омниканальная платформа контакт центра
ru.bank.bot.Omni Получение:
* Длины очереди операторов
* Идентификатора клиента по ИД диалога
* Количество недавних диалогов
* Статуса доступности персонального менеджера

### CTI Outbound Сервис автоматизации исходящих звонков
* ru.bank.bot.Outbound Сохранение результата диалога обзвона Outbound
* ru.bank.bot.SoapRequest.outboundImport импорт клиента в Outbound

### Siebel CRM
* ru.bank.bot.Auto Отправка лида по авто-кредитованию (POST)
* ru.bank.bot.Siebel Поиск карт в отделениях (SOAP)

### NLU Понимание естественного языка (Natural Language Understanding)
ru.bank.bot.nlu Реализована собственная обработка естественного языка для понимания смысла текста ownNLU

### Получение ICR'ов о наличии типов продуктов у клиента
ru.bank.bot.service.getIcr
* Клиентский сегмент
* БИС ИД
* Сервисный пакет
* Признак наличия карты
* Признак наличия дебетовой карты
* Признак наличия кредитной карты
* Признак наличия кредита
* Признак перевыпуска карты
* Признак наличия текущего счёта
* Признак наличия сберегательного счёта
* Признак наличия овердрафта
* Признак наличия несанкционированного овердрафта
* Признак наличия потребительского кредита
* Признак наличия просроченной задолженности
* Признак наличия ипотеки
* Признак наличия автокредита
* Признаки наличия различных Программ Лояльности

### Получение баланса по продукту
ru.bank.bot.service.getBalance Получение балансов по счетам, картам, кредитам

### Получение баланса по продукту
* Генерация случайных вопросов для обучающего бота

### Сервис работы с картами клиента
ru.bank.bsp
* card.service.CardService.getCards Получение списка карт
* card.service.CardService.blockOrUnblockCard Блокировка или разблокировка карты
* card.service.CardService.findCardLimits Получение списка лимитов карты 
* finance.service.FinanceService.getCardFinance Получение финансовой информации по карте
* finance.service.FinanceService.getCardOptions Получение списка опций по карте
* finance.service.FinanceService.findCreditCardTariff Получение тарифов по кредитной карте
* finance.service.FinanceService.findCardsCreditFinance Получение списка кредитных карт

+ ru.bank.bsp.tariff.service.TariffService.findTariff Получение групп комиссий из деталей тарифа

### TaskTracker
ru.bank.bsp.claims.service.ClaimsService
* claimsIssueCloseAccount Создание заявки по закрытию счёта
* claimsIssueCivilServant Создание заявки по заказу справки для госслужащих
* claimsIssueCardCreditClose Создание заявки по закрытию кредитной карты с аннулированным лимитом
* claimsIssueCardOverdraftClose Создание заявки по закрытию овердрафтов

### Сервис заказа справок и выписок
ru.bank.bsp.statement.service.StatementService
* findPersonalAvailableStatements Получение списка доступных для формирования справок
* findListAllStatements Получение информации по заказанным справкам
* findListStatementsWithTimeDelay Получение информации по заказанным справкам с задержкой времени
* validationStartOrEndDateStatementStatus, validationStartOrEndDateStatementStatus, validationDatesStatementStatus, validationCivilServantDateStatus Валидация дат
* requestAccountPersonalStatement Выписка по счёту
* requestCivilServantsPersonalStatement Выписка для государственных служащих