# Трекер привычек

Учебное приложение, сделанное на [Android-курсе](https://www.youtube.com/playlist?list=PLQ09TvuOLytS_vYHtFHQzZJFcnbYCYF6x) от Doubletapp.  

Это простое приложение для отслеживания хороших и плохих привычек. Хранение привычек осущестляется как локально, так и на сервере с помощью [API](https://doublet.app/droid/8/api).  

## Screenshots
*\* Примечание: тёмная тема отключена из-за её неидеальности \**

| <img src="content\home.png">| <img src="content\editor.png"> |
| ---------------------------------------------- | -------------------------------------------- |

## Библиотеки и инструменты

- [Kotlin](https://kotlinlang.org/)
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) и [Flow](https://kotlinlang.org/docs/flow.html)
- [Architecture components](https://developer.android.com/topic/libraries/architecture) (Room, LiveData, ViewModel)
- [Dagger 2](https://developer.android.com/training/dependency-injection) для контроля зависимостей
- [OkHttp](https://square.github.io/okhttp/) и [Retrofit](https://square.github.io/retrofit/) для работы с сетью
- Другие компоненты [Android Jetpack](https://developer.android.com/jetpack)
- [JUnit4](https://junit.org/junit4/) для тестирования и [Kakao](https://github.com/KakaoCup/Kakao) для UI-тестирования

## Архитектура

Clean Architecture (используется 3 слоя) с Model-View-ViewModel паттерном.  
*\*красивая картинка про MVVM паттерн\**
<img width="500px" src="content\mvvm.png">

## Разработка

Используйте Android Studio для запуска проекта. Для сборки проекта потребуется ключ API.  
Ключ можно получить в боте [DoubleDroidStudentBot](https://t.me/DoubleDroidStudentBot). Затем создайте в корне проекта файл `keystore.properties` и добавьте туда ключ в следующем формате:
```
    AUTHORIZATION_TOKEN="<INSERT_YOUR_API_KEY>"
```

## Тестирование

Тестирование проведено с целью ознакомления с инструментами тестирования под Android. Для UI-тестов была использована библиотека [Kakao](https://github.com/KakaoCup/Kakao). Кроме того написано несколько тестов для тестирования ViewModel и UseCases с использованием библиотек [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test) и [mockito](https://github.com/mockito/mockito-kotlin).
