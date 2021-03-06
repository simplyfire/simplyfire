
## Файл (File)
```
v1/api/:project_id/files
```
Методы для работы с файлами проекта

### Описание методов:

### 1. Получение списка файлов выбранного проекта из указанной папки
```
GET /:project_id/files?{params}
```
Параметры метода:

| Параметр    | Обязательный | По умолчанию | Значение |Способ передачи|Ключ словаря|
| --- | --- | --- | --- | --- | --- |
|project_id|Да|-|Идентификатор проекта|Элемент строки запроса| -|
|path|Нет|""|Путь до папки внутри проекта|Параметр Get-запроса| -|
| limit | Нет | 25 | Ограничение на количество возвращаемых проектов |Параметр Get-запроса| -|
| offset | Нет| 0 | Смещение относительно начала списка |Параметр Get-запроса| -|

Пример запроса:
```
v1/api/2/files
```
Результат:
Список файлов в корне проекта:
```json
{
    "response": {
        "count": 3,
        "items": [
            {
                "id": 11,
                "name": "Билеты.docx",
                "path": "Билеты.docx/",
                "type_name": "File"
            },
            {
                "id": 10,
                "name": "93-4.owl",
                "path": "93-4.owl/",
                "type_name": "File"
            },
            {
                "id": 9,
                "name": "testFolder",
                "path": "testFolder/",
                "type_name": "Directory"
            }
        ]
    }
}
```

Пример запроса:
```
v1/api/2/files?path=testFolder/
```
Результат:
Список файлов в папке testFolder:
```json
{
    "response": {
        "count": 1,
        "items": [
            {
                "id": 12,
                "name": "20.12.docx",
                "path": "testFolder/20.12.docx/",
                "type_name": "File"
            }
        ]
    }
}
```
Пример запроса:
```
v1/api/150/files?path=testFolder/
```
Результат:
Проект не найден
```json
{
    "error": [
        {
            "status": "404",
            "message": "Object not found",
            "not found": "Project"
        }
    ]
}
```

### 2. Загрузка файла в проект с указанием пути к папке в этом прокете
```
POST /:project_id/files/upload
```
Параметры метода:

| Параметр    | Обязательный | По умолчанию | Значение |Способ передачи|Ключ словаря|
| --- | --- | --- | --- | --- | --- |
|project_id|Да|-|Идентификатор проекта|Элемент строки запроса|-|
|path|Нет|""|Путь до папки внутри проекта|Параметр запроса|-|
|File|Да|-|Загружаемый файл| Form-data file|File|
Пример запроса:
```
v1/api/2/files/upload
```
Body запроса: form-data: File -> "Загружаемый файл"

Результат:
Файл успешно загружен:
```json
{
    "response": "File was uploaded!"
}
```
Пример запроса:
```
v1/api/2/files/upload
```
Body запроса: form-data: SomeFileName -> "Загружаемый файл"
Результат:
```json
{
    "error": [
        {
            "status": "400",
            "message": "Error during upload file! Check file key, it should be 'File'"
        }
    ]
}
```
Обратите внимание, что ключ в словаре должен быть 'File', а не 'SomeFileName', к примеру.

Пример запроса:
```
v1/api/2/files/upload?path=testFolder/
```
Body запроса: form-data: File -> "Загружаемый файл"

Результат:
Файл успешно загружен:
```json
{
    "response": "File was uploaded!"
}
```
### 3. Загрузка новой версии для существующего файла в проекте
```
POST /:project_id/files/:file_id/upload
```
Параметры метода:

| Параметр    | Обязательный | По умолчанию | Значение |Способ передачи|Ключ словаря|
| --- | --- | --- | --- | --- | --- |
|project_id|Да|-|Идентификатор проекта|Элемент строки запроса|-|
|file_id|Да|-|Идентификатор файла|Элемент строки запроса|-|
|File|Да|-|Загружаемый файл| Form-data file|File|

Пример запроса:
```
v1/api/1/files/1/upload
```
Body запроса: form-data: File -> "Загружаемый файл"
Результат:
Файл успешно загружен:
```json
{
    "response": "File was uploaded!"
}
```
### 4. Получение файла из проекта (Выгрузка файла). Выполняется скачивание последней версии файла
```
GET /:project_id/files/:file_id/download
```
Параметры метода:

| Параметр    | Обязательный | По умолчанию | Значение |Способ передачи|Ключ словаря|
| --- | --- | --- | --- | --- | --- |
|project_id|Да|-|Идентификатор проекта|Элемент строки запроса|-|
|file_id|Да|-|Идентификатор файла|Элемент строки запроса|-|

Пример запроса:
```
v1/api/1/files/7/download
```
Результат: Поток выходного файла


Пример запроса
```
v1/api/1/files/150/download
```
Результат:
```json
{
    "error": [
        {
            "status": "404",
            "message": "File not found"
        }
    ]
}
```

