# Проект сервиса для локального запуска симуляторов iOS/Android
## Автор
- [giustiziere](https://github.com/giustiziere)

**Описание**

Сервис используется для менеджмента симуляторов/эмуляторов перед стартом тестов.

**Сборка**

`gradle clean bootJar`

**Запуск**

`java -jar /<path_to_jar>/EmulatorManager.jar > /<path_to_log>/EmulatorManager.log &`

**Использование**

Сервис запускается локально на порту 8081. Для каждого запроса есть обязательный параметр `platform`, принимающий значения `ios` или `android`.

*Основные методы:*
* Получить список текущих симуляторов: `GET /list`. Пример: `curl -X GET 'http://localhost:8081/list?platform=android'`.
* Завершить и удалить запущенные симуляторы: `DELETE /delete/all`. Пример: `curl -X DELETE 'http://localhost:8081/delete/all?platform=ios'`.
* Добавить и запустить симуляторы: `PUT /create`. Обязательные параметры: `devices` - список имен устройств через запятую; `runtimes` - список версий ОС через запятую (либо указать одну версию, тогда у всех устройств будет одна и та же версия). Необязательный параметр: `isHeadless` - (только для Android) включение headless-режима. Пример: `curl -X PUT $(echo "http://localhost:8081/create?platform=android&devices=Pixel_1,Pixel_2&runtimes=10,11&isHeadless=false" | sed -r 's/ +/%20/g')` (где реплейсер sed нужен для форматирования пробелов в URL).
