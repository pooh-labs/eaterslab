# System **MyCanteen**

## Krótki opis projektu

Stworzone oprogramowanie ma być odpowiedzialne przede wszystkim za zarządzanie ruchem w stółkach, ich opiniowaniem, usprawnianiem pracy pracowników w stołówkach oraz ułatwieniem klientom stołówek pozyskimwania zaróno informacji dotyczących aktualnego stanu stołówek, jak i zasiegania opinii innych uzytkowników dotyczących wszelakich potraw serwowanych w stołówkach.

## Funkcjonalności stworzonego oprogramowania

Stworzony system ma w przyjazny dla użytkowników sposób umożliwiać im:

1. Śledzenie aktualnego stanu stołówek:
    * zbieranie informacji na temat aktualnego zatłoczenia w pomieszczeniach stołówki z wykorzystaniem kamer internetowych
    * zbieranie informacji na temat aktulanego menu w stołówkach od użytkowników (rozpoznawanie tekstu ze zdjęć menu) jak i możliwość dodawania aktualnego menu przez osoby prowadzące lokale
    * zbieranie informacji na temat aktualnych długości kolejek w lokalach, średniego czasu oczekiwania na posiłek
2. Zbieranie danych od użytkowników aplikacji:
    * wystawianie opinii na temat posiłków serwowanych w stołówkach każdego dnia
    * prowadzenie statystyk ogólnego zadowolenia klientów z lokali
3. Udostępnianie zebranych informacji w przyjazny dla użyttkownika sposób:
   * stworzenie aplikacji WWW zawierającej panel użytkownika stołówek wraz ze wszystkimi zebranymi danymi
   * stworzenie aplikacji mobilnej dla pracowników stółówek pomagającej im w pracy na podstawie zbieranych danych:
       * powiadomienia o nadchodzących klientach
       * statystyki oczekiwanych przez klientów dań i szacowanie ilości potrzebnego jedzenia

## Plany wykorzystania technologii

Projekt ma składać się z 3 modułów, rozwijanych neizależnie, które mają ze sobą komunikować się przez stworzone API:

1. Serwer zbierający i porządkujący wszystkie dane, który jednocześnie udostępnia użytkownikom interfejs WWW stworzony z wykorzystaniem - Serwer WWW [Kotlin/Java/Python]

2. System zbierający dane z kamer i wysyłający przetworzone dane na serwer w celu, tak by krytyczne analizy obrazu odbywały się poza serwerem klienckim - OpenCV [C++/Java/Python]

3. Aplikacja mobilna - Android [Java/Kotlin]
