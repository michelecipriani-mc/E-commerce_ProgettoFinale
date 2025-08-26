/* Da eseguire manualmente sul primo profilo admin, così da poter gestire le varie richieste di work e rilasciare i permessi a tutti gli utenti che ne fanno richiesta */ 
-- il primo valore sarà l'id dell'utente da voi registrato "settare all'esigenza"
insert into users_roles (user_id, role_id) values (1, 4); 