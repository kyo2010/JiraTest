# JiraTest

Задача от работодателя.
Demonstration:
https://youtu.be/zvdDsboQ9X8

Two clients for automation:
<pre>
JiraAutomationJavaAPI - based on Java Jira API
JiraAutomationRest - based on Jira API
</pre>

<pre>
Log:
2022-08-20 Add rest API test (Mockbean)
2022-08-20 Add Jackson
2022-08-19 Add an alternative Rest Client without Java Jira API.
</pre>


<pre>
Замечания работодателя по поводу задания:
1.       Странно, что mainController назван с маленькой буквы. А метод PostConstroct с большой. Имхо лучше было бы назвать этот метод init.
> mainController - мне просто так захотелось выделить. Изменил
> inint - не очень подходит. Если я вижу, что метод называется postConstruct - я точно знаяю, что у него аннотация PostConstruct.

2.       Метод controlPanel занимает целых сто строк, тут присутствует и парсинг параметра mode вперемешку с версткой (тегами) и бизнес логикой. Как минимум, надо вынести бизнес логику в отдельный слой сервисов, в контроллере оставить только то, что касается отдачи страницы. А лучше как то разделить этот метод.
> этот класс должен состоять из 20 строчек, просто  нём была debug информация. Удалил

3.       В коде controlPanel  не хватает отступов, пробелов и пустых строк для читаемости.
> Да, это html templete. я не думал о его красоте. Исправил.

4.       Непонятно, что означает магическое число 10004, лучше было бы сделать константу с говорящим названием, а еще лучше не хардкодить эти числа. Это ведь issueTypeId? В идеале, надо получать его из жиры (у нас жира своя локальная и там эти константы могут меняться от инсталляции к инсталляции).
> debug - информация, которую удалил.

5.       Непонятно, зачем нужно поле Environment в JiraConfig.
> так как переменная JIRA_TOKEN - читается из переменных окружения. дабы не презагружать Idea при семене токена, я добавлял poctConstruct и читал из нее.

6.       Интеграционный тест в приложении не должен дергать реальное api в процессе запуска. Правильно будет мокать его заглушкой, тогда тест не будет зависеть от внешних условий. Юнит тесты должны тестировать приложение изолированно по частям (отдельно логику, отдельно отображение и т.д.).
> Добавил мок-тест. Но тесты к тестовой системе - оставил, так как их использовал при отладки.

7.       Для парсинга xml лучше использовать библиотеки, такие как jackson https://www.baeldung.com/jackson-xml-serialization-and-deserialization
> Для парсинга xml с тремя ключами, мне было проще использовать стандартный DOM парсер. Добавил зависимости и jackson

8.       В JiraAutomationRest лучше не парсить вручную джейсон, а использовать Jackson.
> Добавил. Но по факут кода стало больше.
</pre>
