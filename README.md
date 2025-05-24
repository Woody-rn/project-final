## [REST API](http://localhost:8080/doc)

## Концепция:

<details>
<summary style="color: skyblue;text-decoration: underline;">Показать детали</summary>

- Spring Modulith
    - [Spring Modulith: достигли ли мы зрелости модульности](https://habr.com/ru/post/701984/)
    - [Introducing Spring Modulith](https://spring.io/blog/2022/10/21/introducing-spring-modulith)
    - [Spring Modulith - Reference documentation](https://docs.spring.io/spring-modulith/docs/current-SNAPSHOT/reference/html/)

```
  url: jdbc:postgresql://localhost:5432/jira
  username: jira
  password: JiraRush
```

- Есть 2 общие таблицы, на которых не fk
    - _Reference_ - справочник. Связь делаем по _code_ (по id нельзя, тк id привязано к окружению-конкретной базе)
    - _UserBelong_ - привязка юзеров с типом (owner, lead, ...) к объекту (таска, проект, спринт, ...). FK вручную будем
      проверять

</details>

## Аналоги

- https://java-source.net/open-source/issue-trackers

## Тестирование

- https://habr.com/ru/articles/259055/

## Список выполненных задач:
задачи в процессе выполнения
1. ✅**Разобраться со структурой проекта (onboarding)**
2. ✅**Удалить социальные сети: vk, yandex**
   * Удалены кнопки из шаблонов 
      - `resources/view/unauth/register.html`  
      - `resources/view/login.html`
   * Удалены классы  
      - `com.javarush.jira.login.internal.sociallogin.handler.YandexOAuth2UserDataHandler`   
      - `com.javarush.jira.login.internal.sociallogin.handler.VkOAuth2UserDataHandler`
   * Почищен `application.yaml`
3. ✅**Вынести чувствительную информацию в отдельный проперти файл**  
    
    - Создан файл `application-secrets.yaml`  в котором используются переменные окружения в виде `${VARIABLE_NAME:default_value}`  
    - путь к файлу `src/main/resources/application-secrets.yaml`  <details><summary style="color: skyblue;">Показать скрин файла</summary>![img.png](README_img/img.png)</details>

4. ✅**Переделать тесты так, чтоб во время тестов использовалась in memory БД (H2), а не PostgreSQL.**  
    - Создан файл с измененным тестовым скриптом под БД H2 [src/main/resources/db/changelog_H2.sql](https://github.com/Woody-rn/project-final/blob/nikitin/src/main/resources/db/changelog_H2.sql)  
    - Изменил [src/test/resources/application-test.yaml](https://github.com/Woody-rn/project-final/blob/master/src/test/resources/application-test.yaml) и не пришлось определять дополнительные бины  
    - В `AbstractControllerTest` поправил файл скрипта  
    ![img.png](README_img/img_AbstractControllerTest.png)
5. ✅**Написать тесты для всех публичных методов контроллера ProfileRestController**  
   - [открыть ProfileRestControllerTest](https://github.com/Woody-rn/project-final/blob/nikitin/src/test/java/com/javarush/jira/profile/internal/web/ProfileRestControllerTest.java)  
    ![img.png](README_img/img_ProfileRestControllerTest.png)
6. ✅**Сделать рефакторинг метода com.javarush.jira.bugtracking.attachment.FileUtil#upload чтоб он использовал современный
   подход для работы с файловой системмой**  
    - Код переписан [открыть FileUtil#upload](https://github.com/Woody-rn/project-final/blob/nikitin/src/main/java/com/javarush/jira/bugtracking/attachment/FileUtil.java)  
    ![img.png](README_img/img_FileUtil.png)
7. ❌**Добавить новый функционал: добавления тегов к задаче (REST API + реализация на сервисе)**
8. ❌**Добавить подсчет времени сколько задача находилась в работе и тестировании**
9. ✅**Написать Dockerfile для основного сервера**  

    - ![img.png](README_img/img_dockerfile.png)

10. ✅**Написать docker-compose файл для запуска контейнера сервера вместе с БД и nginx**  
    - Открыть [docker-compose](https://github.com/Woody-rn/project-final/blob/nikitin/docker-compose.yaml)
    - В `nginx.conf` заменены адреса с `localhost` на имя сервиса/контейнера приложения указанного в docker-compose `proxy_pass http://jira-app:8080;`
    - Добавлен liquibase `changelog-master.xml` соединяющий создание структуры и добавление данных. [Путь к файлу](https://github.com/Woody-rn/project-final/tree/nikitin/src/main/resources/db)
11. ❌**Добавить локализацию минимум на двух языках для шаблонов писем (mails) и стартовой страницы index.html**
12. ❌**Переделать механизм распознавания «свой-чужой» между фронтом и беком с JSESSIONID на JWT**

