## Проект (Project)
```
v1/api/projects/
```

Методы для работы с проектами пользователей

### Описание методов:

### 1. Получение списка проектов текущего пользователя
```
GET /projects?{params}
```
Параметры метода:

| Параметр    | Обязательный | По умолчанию | Значение |Способ передачи|Ключ словаря|
| --- | --- | --- | --- | --- | --- |
| limit | Нет | 25 | Ограничение на количество возвращаемых проектов |Параметр Get-запроса| -|
| offset | Нет| 0 | Смещение относительно начала списка |Параметр Get-запроса| -|
Пример запроса:
```
v1/api/projects?limit=10&offset=20
```
#### Результат:
Список проектов:
```json
{
    "response": {
        "count": 3,
        "items": [
            {
                "id": 1,
                "name": "test",
                "file_name": "Admin-test"
            },
            {
                "id": 2,
                "name": "MyBestProject",
                "file_name": "Admin-MyBestProject"
            },
            {
                "id": 3,
                "name": "PrivateProject",
                "file_name": "Admin-PrivateProject"
            }
        ]
    }
}
```

### 2. Создание нового проекта
```
POST /projects/create
```
Параметры метода:

| Параметр    | Обязательный | По умолчанию | Значение |Способ передачи|Ключ словаря|
| --- | --- | --- | --- | --- | --- |
| project_name | Да | - | Название проекта | JSON | name|
Пример запроса:
```
v1/api/projects/create
```
JSON:
```json
{
	"name" : "MyProjectName"
}
```
#### Результат:
Проект удачно создан:
```json
{
    "response": "Project created"
}
```
Проект уже существует:
```json
{
    "error": [
        {
            "status": "400",
            "message": "Object already exist",
            "param": "project_name"
        }
    ]
}
```
Некорректный JSON:
```json
{
    "error": [
        {
            "status": "400",
            "message": "Incorrect JSON"
        }
    ]
}
```
