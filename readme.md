# Трекер привычек

Учебное приложение, сделанное на [Android-курсе](https://www.youtube.com/playlist?list=PLQ09TvuOLytS_vYHtFHQzZJFcnbYCYF6x) от Doubletapp.  

Это простое приложение для отслеживания хороших и плохих привычек. Хранение привычек осущестляется как локально, так и на сервере с помощью [API](https://doublet.app/droid/8/api). Доступны английский и русский языки интерфейса (определяется языком системы).  

Можно скачать apk по [ссылке](https://drive.google.com/file/d/1HzZveMS0QobDgt5nylLwsazkS4x31aWn/view?usp=sharing).  
*\* Примечание: здесь используется токен доступа с курса \**  

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

## Работа с сетью

Используется библиотека [Retrofit](https://square.github.io/retrofit/) и клиент от [OkHttp](https://square.github.io/okhttp/). Запросы сохраняются в базе данных и выполняются в порядке очереди. При неудаче (отсутвие интернета или ошибка сервера) запрос повторяется через установленный промежуток времени. Обновление данных невозможно пока все запросы на изменение привычек не отправлены на сервер.  
В интерфейсе приложения отображается информация из базы данных, таким образом приложение корректно работает даже при отсутствии интернета. 

## Разработка

Используйте Android Studio для запуска проекта. Для сборки проекта потребуется ключ API.  
Ключ можно получить в боте [DoubleDroidStudentBot](https://t.me/DoubleDroidStudentBot). Затем создайте в корне проекта файл `keystore.properties` и добавьте туда ключ в следующем формате:
```
    AUTHORIZATION_TOKEN="<INSERT_YOUR_API_KEY>"
```

## Тестирование

Тестирование проведено с целью ознакомления с инструментами тестирования под Android. Для UI-тестов была использована библиотека [Kakao](https://github.com/KakaoCup/Kakao). Кроме того написано несколько тестов для тестирования ViewModel и UseCases с использованием библиотек [kotlinx-coroutines-test](https://github.com/Kotlin/kotlinx.coroutines/tree/master/kotlinx-coroutines-test) и [mockito](https://github.com/mockito/mockito-kotlin).
