# **MyCanteen**  System 

The MyCanteen system is designed for customers and canteen owners. Before going out for dinner, customers will be able to check the current canteen occupancy, offered menu and served food. The canteen owner can view the collected information, compare thems with historical visit trends, as well as view cutomers feedback.

# System components

The whole system consists of three main apps:
* camera system for data capturing and main analyze process (written in C++ using openCV)
* webserver app for data management and webclient app (written in Python using Django)
* android app for users feedback and statistics management (app written in Kotlin for Android) 

## Zakres funkcjonalny

1. Śledzenie liczby użytkowników:
    * zbieranie informacji o aktualnym zatłoczeniu stołówki
    * zbieranie informacji o aktualnych długościach kolejek i średnim czasie oczekiwania na posiłek
    * podgląd informacji o obłożeniu na żywo
    * przegląd historii i trendów dla właścicieli
        * powiadomienia o spodziewanych skokach liczby użytkowników
        * szacowanie ilości zamówionych dań 
2. Obsługa menu:
    * dodawanie menu przez użytkowników i właścicieli stołówek
    * podgląd oferty dla użytkowników
3. Zbieranie informacji zwrotnej od użytkowników:
    * wystawianie opinii na temat serwowanych posiłków
    * prowadzenie statystyk ogólnego zadowolenia klientów z lokali

Opisane funkcje będą oferowane poprzed stronę WWW oraz aplikację na system Android.

## Zarys architektury

1. Centralny punkt dostępowy zbiera informacje i udostępnia je klientom. [Kotlin/Java/Python]
2. System kamer zlicza liczbę osób wchodzących i wychodzących ze stołówki i przekazuje je do serwera. Obrazy są przetwarzane na urządzeniu, do serwera trafiają tylko liczby użytkowników. [OpenCV/C++/Java/Python]
3. Klient WWW udostępnia informacje dla użytkowników i dostarcza panel administracyjny dla właścicieli. [?]
4. Aplikacja Android udostępnia informacje dla użytkowników i dostarcza panel administracyjny dla właścicieli. [Java/Kotlin]
