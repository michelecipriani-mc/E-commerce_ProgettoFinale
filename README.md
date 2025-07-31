## E-commerce_SmartBay
                        🛒 SmartBAY – Progetto eCommerce
SmartBAY è una piattaforma eCommerce simulata, sviluppata in Java con Spring Boot e Thymeleaf, progettata per offrire un'esperienza di shopping online completa, funzionale e moderna. Il progetto riproduce il funzionamento di un centro commerciale digitale, con gestione avanzata di utenti, articoli, carrello e ruoli personalizzati.

                        🚀 Funzionalità principali
## 🏠 Homepage
Barra di navigazione (navbar) con:
 - Logo
 - Searchbar
 - Collegamento per tornare alla homepage
 - Store per la ricerca di tutti gli articoli
 - Login/Registrazione nel caso di utente non autenticato non visibili dopo il Login
 - Visibilità del collegamento alle varie Dashboard in relazione al ruolo 
 - Accesso alle dashboard utente e al carrello
 - Visualizzazione degli articoli sotto forma di card con:
Titolo, anteprima immagine, descrizione e autore
Pulsante per aggiungere al carrello solo nello shop con aggiunta di 1 elemento al carrello al click sul pulsante della card, quantità gestibile invece all'interno del dettaglio del prodotto (massimo 5 per articolo), visibilità del bottone aggiungi al carrello oscurata nella homepage dove l'utente avrà visibilità solamente degli ultimi 3 articoli postati.

## 👥 🔐 Gestione utenti
Login/Registrazione con distinzione tra quattro ruoli:
 - Utente: può selezionare articoli dallo shop e successivamente completare l'acquisto dal carello, visualizzare i propri dati e il suo ruolo nella pagina, settare il proprio saldo, visionare lo storico degli ordini nella dashboard dedicata.
 - Venditore: può in aggiunta caricare e gestire i propri articoli.
 - Revisore: può in aggiunta revisionare tutti gli articoli pubblicati dai venditori decidere se approvarli o non approvarli nella sua dashboard dedicata, la sua sceltà positiva renderà visibile l'articolo nello shop.
 - Admin: ha privilegi di controllo avanzato su sistema e utenti ha il compito univoco di assegnare i ruoli, creare e eliminare o modificare le categorie visibili nel WebSite. 

## 🛍️ Carrello
Aggiunta articoli con limite massimo per prodotto, rimozione articoli in aggiunta nelle action abbiamo la possibilità di eliminare lo specifico articolo dal carello, nelle quantità possiamo aggiungere(+1) o rimuovere(-1) la quantità del prodotto che autonomamente se <0 verrà eliminato, button per svuotare interamente il carrello, riepilogo del prezzo totale e completamento ordine.

## 📩 Footer
Sezione "Lavora con noi" con form di invio automatico a email, visibilità dei contatti, sede legale e mail, varie partnership per consegna, metodi di pagamento e download app. Link a: 
 - Profilo LinkedIn del team/progetto 
 - Repository GitHub

                        🛠️ Tecnologie utilizzate
 - IDE: VSCode
 - Extensions Pack: Extension Pack for Java, Spring Boot Extension Pack
 - GitHub
 - Java 21
 - Spring Boot:
    - Spring Security (per gestione ruoli/login)
    - Hibernate/JPA
 - Thymeleaf
 - Maven
 - MySQL
 - HTML + CSS + JS
 - Librerie: MDB, Bootstrap, FontAwsom e Google Fonts

## 📎 Requisiti
 - JDK 17+
 - Maven
 - Database relazionale (es. MySQL)


## 📂 Avvio progetto

1. Clona la repository:
```bash
git clone https://github.com/tuo-nome-utente/smartbay.git
```
Configura il database in application.properties

Avvia l'app da IDE:

```bash
./mvnw spring-boot:run
```
O da terminale:

Apri il browser su http://localhost:8080

Dopo aver eseguito e lanciato il progetto da ProgettofinaleApplication.java verrà automaticamente creato un DataBase chiamato smartbay, e verrà automaticamente popolata la tabella dei ruoli. Per poter eseguire correttamente tutte le funzionalità, risulta necessario impostare manualmente per l'utente amministratore solo per la prima volta il ruolo. 
Viene effetuato recandosi nella cartella -> .sql -> nel file insert.sql e cliccare su RUN