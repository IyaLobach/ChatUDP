# ChatUDP
Текстовый чат для двух пользователей на сокетах. Чат должен быть реализован по принципу клиент-сервер. Один пользователь находится на сервере, второй --- на клиенте. Адреса и порты задаются через командную строку: клиенту --- куда соединяться, серверу --- на каком порту слушать. При старте программы выводится текстовое приглашение, в котором можно ввести одну из следующих команд:
Задать имя пользователя (@name Vasya)
Послать текстовое сообщение (Hello)
Выход (@quit)
История чата пишется в файл.
Принятые сообщения автоматически выводятся на экран. Программа работает по протоколу UDP.
