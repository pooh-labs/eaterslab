# System **MyCanteen**

System MyCanteen jest przeznaczony dla klientów oraz właścicieli stołówek. Klienci przed wyjściem na obiad będą mogli sprawdzić aktualne obłożenie stołówki, oferowane menu, a także po posiłku ocenić serwowane potrawy. Właściciel stołówki może przeglądać zebrane informacje, zapoznać się z historycznymi trendami odwiedzin, a także przeglądać informacje zwrotne, które mogą pozwolić mu na lepsze zarządzanie obiektem poprzez dostosowywanie swojej oferty do potrzeb klientów.

---

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

Opisane funkcje będą oferowane poprzez stronę WWW oraz aplikację na system Android.

---

## Zarys architektury

1. Centralny punkt dostępowy zbiera informacje i udostępnia je klientom. [Kotlin/Java/Python]
2. System kamer zlicza liczbę osób wchodzących i wychodzących ze stołówki oraz liczbę zajętych miejsc siedzących w stołówce i przekazuje je do serwera. Obrazy są przetwarzane na urządzeniu, do serwera trafiają tylko liczby użytkowników. [OpenCV/C++/Java/Python]
3. Klient WWW udostępnia informacje dla użytkowników i dostarcza panel administracyjny dla właścicieli. [Kotlin/Java/Python]
4. Aplikacja Android udostępnia informacje dla użytkowników i dostarcza panel administracyjny dla właścicieli. [Java/Kotlin]

---

## Autorzy

* Krzysztof Antoniak
* Robert Michna
* Maciej Procyk
* Jakub Walendowski