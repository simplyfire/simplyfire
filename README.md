# SimplyFire

SimplyFire - репозиторий управления файлами, поддерживающий древовидную структуру папок и версионирование. Краткое описание для пользователя-непрограммиста см. в [приветственном сообщении программы](/docs/HomePageInfo.md).

## Технические подробности
SimplyFire - веб-сайт, предоставляющий GUI для регистрации/аутентификации пользователей, а также для управления файлами аутентифицированного пользователя. Кроме GUI, все команды можно выполнять из сторонних приложений при помощи запросов к [HTTP REST API](/docs/).

Файлы пользователей хранятся в виде файлов "как есть". Структура файлов в файловой системе (ФС) почти соответствует структуре файлов в репозитории. Единственным исключением служит имя файла, поскольку в ФС к нему приписана временная метка загрузки.

Приложение написано на Java 8 с использованием [Play Framework](https://www.playframework.com/). В качестве подсистемы хранения данных, отличных от файлов пользователей, используется встраиваемая СУБД [H2](http://www.h2database.com/).

### Сборка проекта
Для сборки проекта используется [SBT](https://www.scala-sbt.org/) (требуется [Java SE Development Kit 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)).

Проект собирается командой `sbt dist`. Результатом сборки является ZIP-архив. Альтернативные способы сборки описаны в [документации](https://www.playframework.com/documentation/2.6.x/Deploying).

### Запуск проекта
Проект запускается из папки распакованного ZIP-архива командой `bin/simplyfire` или `bin\simplyfire.bat` в зависимости от используемой операционной системы. Для работы приложения требуется только [Java Runtime Environment 8](https://java.com/ru/download/).

#### Проблемы при запуске приложения
Часть описанных ниже проблем решена в [сборках, доступных для скачивания](https://github.com/simplyfire/simplyfire/releases).
_____
> Database 'default' needs evolution!

Приложение необходимо запускать с дополнительным параметром
```
bin/simplyfire -Dplay.evolutions.db.default.autoApply=true
```
_____
> The application secret has not been set, and we are in prod mode. Your application is not secure.

Необходима настройка [секретного ключа](https://www.playframework.com/documentation/2.6.x/ApplicationSecret). 
Приложение необходимо запускать с дополнительным параметром
```
bin/simplyfire -Dplay.http.secret.key='{произвольный секретный ключ приложения}'
```
_____
> Слишком длинная входная строка. Ошибка в синтаксисе команды.

Эта ошибка проявляется только при запуске в Windows. В файле `bin\simplyfire.bat` длинную строку с 
```
set "APP_CLASSPATH=%APP_LIB_DIR%\..\conf\;%APP_LIB_DIR%\ru....
...
....jar"
```
необходимо заменить на 
```
set "APP_CLASSPATH=%APP_LIB_DIR%\..\conf\;%APP_LIB_DIR%\*"
```
