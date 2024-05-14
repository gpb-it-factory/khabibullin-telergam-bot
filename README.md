## Практический проект
### Студент Хабибуллин И.Ф.

<details>
    <summary><b>Краткое описание проекта Мини-банк</b></summary>  

В рамках практики мы разработаем "Мини-банк", который будет состоять из трёх компонентов:
1. `frontend` (telegram-bot на java/kotlin);
2. `middle-слой` (java/kotlin-сервис);
3. `backend` (java/kotlin-сервис). 

- ниже представлена схема

    <details>
        <summary><b>Схема</b></summary>  

                 ┌──────────────────────────────────┐         ┌──────────────────────────────────┐         ┌──────────────────────────────────┐                  
                 │                                  │         │                                  │         │                                  │                  
                 │             FRONTEND             │         │             MIDDLE               │         │             BACKEND              │                  
                 │                                  │         │                                  │         │                                  │                  
                 │  Telegram-bot                    │         │  Java-сревис                     │         │  "Глубинная" система, выступает в│                  
                 │  Выступает как клиентское        │  HTTP   │  Принимает запросы от tg-бота,   │  HTTP   │  качестве АБС (автоатизированная │                  
                 │  приложение, инициирует запросы  ├────────►│  выполняет валидацию и           ├────────►│  банковская система, обрабатывает│                  
                 │  пользователя                    │         │  бизнес-логику, маршрутизирует   │         │  транзакции, хранит клиентские   │                  
                 │                                  │         │  запросы в "банк"                │         │  данные и т.д.                   │                   
                 │                                  │         │                                  │         │                                  │                  
                 └──────────────────────────────────┘         └──────────────────────────────────┘         └──────────────────────────────────┘
    </details>
</details>


<details>
    <summary><b>Как запустить проект?</b></summary>

1. Раздел в разработке
</details>

<details>
    <summary><b>Технологический стэк</b></summary>  

1. Языки: [Java 17](https://www.java.com/ru/)+ в виде реализации [Axiom JDK](https://axiomjdk.ru/pages/downloads/#/java-17-lts)
2. Система сборки: [Gradle 8.7](https://gradle.org/)
3. Базовый фреймворк: [Spring Boot](https://spring.io/projects/spring-boot)
4. Дополнительные библиотекам: [JUnit 5](https://junit.org/junit5/), [Testcontainers](https://testcontainers.com/), [AssertJ](https://assertj.github.io/doc/), [Logback](https://logback.qos.ch/), [Micrometer](https://micrometer.io/), [MapStruct](https://mapstruct.org/)
</details>
