package pl.klolo.workshops.logic;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pl.klolo.workshops.domain.*;
import pl.klolo.workshops.domain.Currency;
import pl.klolo.workshops.mock.HoldingMockGenerator;
import sun.reflect.generics.tree.Tree;

import javax.swing.text.html.Option;

import static java.util.Objects.isNull;

class WorkShop {

    public static final String NAMES_START = "(";
    /**
     * Lista holdingów wczytana z mocka.
     */
    private final List<Holding> holdings;
    Predicate<Holding> hasMoreThanOneCompany = h -> h.getCompanies().size() > 0;

    // Predykat określający czy użytkownik jest kobietą
    private final Predicate<User> isWoman = user -> Sex.WOMAN.equals(user.getSex());

    WorkShop() {
        final HoldingMockGenerator holdingMockGenerator = new HoldingMockGenerator();
        holdings = holdingMockGenerator.generate();
    }

    /**
     * Metoda zwraca liczbę holdingów w których jest przynajmniej jedna firma.
     */
    long getHoldingsWhereAreCompanies() {
        int count = 0;
        for (Holding holding : holdings) {
            if (hasMoreThanOneCompany.test(holding)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Metoda zwraca liczbę holdingów w których jest przynajmniej jedna firma. Napisz to za pomocą strumieni.
     */
    long getHoldingsWhereAreCompaniesAsStream() {
        return holdings
                .stream()
                .filter(hasMoreThanOneCompany)
                .count();
    }

    /**
     * Zwraca nazwy wszystkich holdingów pisane z małej litery w formie listy.
     */
    List<String> getHoldingNames() {
        List<String> holdingsNamesLowerCase = new ArrayList<>();
        for (Holding holding : holdings) {
            holdingsNamesLowerCase.add(holding.getName().toLowerCase());
        }
        return holdingsNamesLowerCase;
    }

    /**
     * Zwraca nazwy wszystkich holdingów pisane z małej litery w formie listy. Napisz to za pomocą strumieni.
     */
    List<String> getHoldingNamesAsStream() {
        return holdings
                .stream()
                .map(holding -> holding.getName().toLowerCase())
                .collect(Collectors.toList());
    }

    /**
     * Zwraca nazwy wszystkich holdingów sklejone w jeden string i posortowane. String ma postać: (Coca-Cola, Nestle, Pepsico)
     */
    String getHoldingNamesAsString() {
        String holdingsNamesSorted = "(";
        holdings.sort(Comparator.comparing(Holding::getName));
        for (Holding holding : holdings) {
            if (!holdingsNamesSorted.equals(NAMES_START)) {
                holdingsNamesSorted += ", ";
            }
            holdingsNamesSorted += holding.getName();
        }
        holdingsNamesSorted += ")";
        return holdingsNamesSorted;
    }

    /**
     * Zwraca nazwy wszystkich holdingów sklejone w jeden string i posortowane. String ma postać: (Coca-Cola, Nestle, Pepsico). Napisz to za pomocą strumieni.
     */
    String getHoldingNamesAsStringAsStream() {
        return holdings
                .stream()
                .map(Holding::getName)
                .sorted()
                .collect(Collectors.joining(", ", "(", ")"));
    }

    /**
     * Zwraca liczbę firm we wszystkich holdingach.
     */
    long getCompaniesAmount() {
        long companiesAmount = 0L;
        for (Holding holding : holdings) {
            companiesAmount += holding.getCompanies().size();
        }
        return companiesAmount;
    }

    /**
     * Zwraca liczbę firm we wszystkich holdingach. Napisz to za pomocą strumieni.
     */
    long getCompaniesAmountAsStream() {
        return holdings
                .stream()
                .mapToLong(holding -> holding.getCompanies().size())
                .sum();
        /*return holdings
                .stream()
                .flatMap(holding -> holding.getCompanies().stream())
                .count();
        return holdings
                 .stream()
                 .map(holding -> holding.getCompanies().size())
                 .reduce(0, (a, b) -> Integer.sum(a, b));*/
    }

    /**
     * Zwraca liczbę wszystkich pracowników we wszystkich firmach.
     */
    long getAllUserAmount() {
        long allUserAmoutn = 0L;
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                allUserAmoutn += company.getUsers().size();
            }
        }
        return allUserAmoutn;
    }

    /**
     * Zwraca liczbę wszystkich pracowników we wszystkich firmach. Napisz to za pomocą strumieni.
     */
    long getAllUserAmountAsStream() {
        return holdings
                .stream()
                .flatMap(holding -> holding.getCompanies().stream())
                .mapToLong(company -> company.getUsers().size())
                .sum();
    }

    /**
     * Zwraca listę wszystkich nazw firm w formie listy.
     */
    List<String> getAllCompaniesNames() {
        List<String> allCompaniesNames = new ArrayList<>();
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                allCompaniesNames.add(company.getName());
            }
        }
        return allCompaniesNames;
    }

    /**
     * Zwraca listę wszystkich nazw firm w formie listy. Tworzenie strumienia firm umieść w osobnej metodzie którą później będziesz wykorzystywać. Napisz to za
     * pomocą strumieni.
     */
    List<String> getAllCompaniesNamesAsStream() {
        return getCompanyStream()
                .map(company -> company.getName())
                .collect(Collectors.toList());
    }

    /**
     * Zwraca listę wszystkich firm jako listę, której implementacja to LinkedList.
     */
    LinkedList<String> getAllCompaniesNamesAsLinkedList() {
        return new LinkedList<>(getAllCompaniesNames());
    }

    /**
     * Zwraca listę wszystkich firm jako listę, której implementacja to LinkedList. Obiektów nie przepisujemy po zakończeniu działania strumienia. Napisz to za
     * pomocą strumieni.
     */
    LinkedList<String> getAllCompaniesNamesAsLinkedListAsStream() {
        return getCompanyStream()
                .map(Company::getName)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Zwraca listę firm jako string gdzie poszczególne firmy są oddzielone od siebie znakiem "+"
     */
    String getAllCompaniesNamesAsString() {
        String allCompaniesNamesAsString = "";
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                if (!allCompaniesNamesAsString.equals("")) {
                    allCompaniesNamesAsString += "+";
                }
                allCompaniesNamesAsString += company.getName();
            }
        }
        return allCompaniesNamesAsString;
    }

    /**
     * Zwraca listę firm jako string gdzie poszczególne firmy są oddzielone od siebie znakiem "+" Napisz to za pomocą strumieni.
     */
    String getAllCompaniesNamesAsStringAsStream() {
        return getCompanyStream()
                .map(Company::getName)
                .collect(Collectors.joining("+"));
    }

    /**
     * Zwraca listę firm jako string gdzie poszczególne firmy są oddzielone od siebie znakiem "+". Używamy collect i StringBuilder. Napisz to za pomocą
     * strumieni.
     * <p>
     * UWAGA: Zadanie z gwiazdką. Nie używamy zmiennych.
     */
    String getAllCompaniesNamesAsStringUsingStringBuilder() {
        return null; //getCompanyStream()
//                .map(Company::getName)
//                .reduce().                                                      /////////////////////////to implement
    }

    /**
     * Zwraca liczbę wszystkich rachunków, użytkowników we wszystkich firmach.
     */
    long getAllUserAccountsAmount() {
        long allUserAccountsAmount = 0;
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    allUserAccountsAmount += user.getAccounts().size();
                }
            }
        }
        return allUserAccountsAmount;
    }

    /**
     * Zwraca liczbę wszystkich rachunków, użytkowników we wszystkich firmach. Napisz to za pomocą strumieni.
     */
    long getAllUserAccountsAmountAsStream() {
        return getCompanyStream()
                .flatMap(company -> company.getUsers().stream())
                .mapToLong(user -> user.getAccounts().size())
                .sum();
    }

    /**
     * Zwraca listę wszystkich walut w jakich są rachunki jako string, w którym wartości występują bez powtórzeń i są posortowane.
     */
    String getAllCurrencies() {
        TreeSet<String> currenciesWithoutDuplicates = new TreeSet<>(Comparator.naturalOrder());
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    for (Account account : user.getAccounts()) {
                        currenciesWithoutDuplicates.add(account.getCurrency().toString());
                    }
                }
            }
        }

        return currenciesWithoutDuplicates.toString()
                .replace("[", "")
                .replace("]", "");
    }

    /**
     * Zwraca listę wszystkich walut w jakich są rachunki jako string, w którym wartości występują bez powtórzeń i są posortowane. Napisz to za pomocą strumieni.
     */
    String getAllCurrenciesAsStream() {
        return getCompanyStream()
                .flatMap(company -> company.getUsers().stream())
                .flatMap(user -> user.getAccounts().stream())
                .map(account -> account.getCurrency().toString())
                .distinct()
                .sorted()
                .collect(Collectors.joining(", "));
    }

    /**
     * Metoda zwraca analogiczne dane jak getAllCurrencies, jednak na utworzonym zbiorze nie uruchamiaj metody stream, tylko skorzystaj z  Stream.generate.
     * Wspólny kod wynieś do osobnej metody.
     *
     * @see #getAllCurrencies()
     */
    String getAllCurrenciesUsingGenerate() {
        return null;
    }

    /**
     * Zwraca liczbę kobiet we wszystkich firmach.
     */
    long getWomanAmount() {
        long womanAmount = 0L;
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    if (Sex.WOMAN.equals(user.getSex())) {
                        womanAmount++;
                    }
                }
            }
        }
        return womanAmount;
    }

    /**
     * Zwraca liczbę kobiet we wszystkich firmach. Powtarzający się fragment kodu tworzący strumień uzytkowników umieść w osobnej metodzie. Predicate określający
     * czy mamy doczynienia z kobietą inech będzie polem statycznym w klasie. Napisz to za pomocą strumieni.
     */
    long getWomanAmountAsStream() {
        return getUserStream()
                .filter(isWoman)
                .count();
    }


    /**
     * Przelicza kwotę na rachunku na złotówki za pomocą kursu określonego w enum Currency. Ustaw precyzje na 3 miejsca po przecinku.
     */
    BigDecimal getAccountAmountInPLN(final Account account) {
        return new BigDecimal(account.getCurrency().rate)
                .multiply(account.getAmount())
                .setScale(3, BigDecimal.ROUND_HALF_DOWN);
    }


    /**
     * Przelicza kwotę na rachunku na złotówki za pomocą kursu określonego w enum Currency. Napisz to za pomocą strumieni.
     */
    BigDecimal getAccountAmountInPLNAsStream(final Account account) {
        return Optional
                .ofNullable(account)
                .map(acc -> acc.getAmount()
                        .multiply(new BigDecimal(acc.getCurrency().rate))
                        .setScale(3, BigDecimal.ROUND_HALF_DOWN))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Przelicza kwotę na podanych rachunkach na złotówki za pomocą kursu określonego w enum Currency  i sumuje ją.
     */
    BigDecimal getTotalCashInPLN(final List<Account> accounts) {
        BigDecimal totalCashInPLN = BigDecimal.ZERO;
        for (Account account : accounts) {
            totalCashInPLN = totalCashInPLN
                    .add(getAccountAmountInPLN(account));
        }
        return totalCashInPLN;
    }

    /**
     * Przelicza kwotę na podanych rachunkach na złotówki za pomocą kursu określonego w enum Currency  i sumuje ją. Napisz to za pomocą strumieni.
     */
    BigDecimal getTotalCashInPLNAsStream(final List<Account> accounts) {
        return accounts
                .stream()
                .map(account -> getAccountAmountInPLN(account))
                .reduce((first, augend) -> first.add(augend))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Zwraca imiona użytkowników w formie zbioru, którzy spełniają podany warunek.
     */
    Set<String> getUsersForPredicate(final Predicate<User> userPredicate) {
        Set<String> userNames = new TreeSet<>();
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    if (userPredicate.test(user)) {
                        userNames.add(user.getFirstName());
                    }
                }
            }
        }
        return userNames;
    }

    /**
     * Zwraca imiona użytkowników w formie zbioru, którzy spełniają podany warunek. Napisz to za pomocą strumieni.
     */
    Set<String> getUsersForPredicateAsStream(final Predicate<User> userPredicate) {
        return getUserStream()
                .filter(userPredicate)
                .map(User::getFirstName)
                .collect(Collectors.toSet());
    }

    /**
     * Metoda filtruje użytkowników starszych niż podany jako parametr wiek, wyświetla ich na konsoli, odrzuca mężczyzn i zwraca ich imiona w formie listy.
     */
    List<String> getOldWoman(final int age) {
        List<String> oldWomen = new ArrayList<>();
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    if (user.getAge() > age) {
                        System.out.println(user.getFirstName());

                        if (Sex.WOMAN.equals(user.getSex())) {
                            oldWomen.add(user.getFirstName());
                        }
                    }
                }
            }
        }
        return oldWomen;
    }

    /**
     * Metoda filtruje użytkowników starszych niż podany jako parametr wiek, wyświetla ich na konsoli, odrzuca mężczyzn i zwraca ich imiona w formie listy. Napisz
     * to za pomocą strumieni.
     */
    List<String> getOldWomanAsStream(final int age) {
        return getUserStream()
                .filter(user -> user.getAge() > age)
                .peek(user -> System.out.println(user))
                .filter(isWoman)
                .map(User::getFirstName)
                .collect(Collectors.toList());
    }

    /**
     * Dla każdej firmy uruchamia przekazaną metodę.
     */
    void executeForEachCompany(final Consumer<Company> consumer) {
        /*for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                consumer.accept(company);
            }
        }*/
        getCompanyStream().forEach(consumer);
        //throw new IllegalArgumentException();
    }

    /**
     * Wyszukuje najbogatsza kobietę i zwraca ja. Metoda musi uzwględniać to że rachunki są w różnych walutach.
     */
    Optional<User> getRichestWoman() {
        User richestWoman = null;
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    if (isWoman.test(user)) {
                        if (isNull(richestWoman)) {
                            richestWoman = user;
                        } else {

                            BigDecimal totalCashCurretRichestWoman = getTotalCashInPLN(richestWoman.getAccounts());
                            BigDecimal totalCashPretendingWomn = (getTotalCashInPLN(user.getAccounts()));
                            if (totalCashCurretRichestWoman.compareTo(totalCashPretendingWomn) < 0) {
                                richestWoman = user;
                            }
                        }
                    }

                }
            }
        }
        return Optional.ofNullable(richestWoman);
    }

    /**
     * Wyszukuje najbogatsza kobietę i zwraca ja. Metoda musi uzwględniać to że rachunki są w różnych walutach. Napisz to za pomocą strumieni.
     */
    Optional<User> getRichestWomanAsStream() {
        return getUserStream()
                .filter(isWoman)
                .max(Comparator.comparing(user -> getTotalCashInPLN(user.getAccounts())));
    }

    /**
     * Zwraca nazwy pierwszych N firm. Kolejność nie ma znaczenia.
     */
    Set<String> getFirstNCompany(final int n) {
        Set<String> firstNCompany = new HashSet<>();
        int i = 0;
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                if (firstNCompany.size() < n) {
                    firstNCompany.add(company.getName());
                }
            }
        }
        return firstNCompany;
    }

    /**
     * Zwraca nazwy pierwszych N firm. Kolejność nie ma znaczenia. Napisz to za pomocą strumieni.
     */
    Set<String> getFirstNCompanyAsStream(final int n) {
        return getCompanyStream()
                .map(company -> company.getName())
                .limit(n)
                .collect(Collectors.toSet());
    }

    /**
     * Metoda zwraca jaki rodzaj rachunku jest najpopularniejszy. Stwórz pomocniczą metdę getAccountStream. Jeżeli nie udało się znaleźć najpopularnijeszego
     * rachunku metoda ma wyrzucić wyjątek IllegalStateException.
     */
    AccountType getMostPopularAccountType() {
        EnumMap<AccountType, Integer> accountMap = new EnumMap<>(AccountType.class);

        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    for (Account account : user.getAccounts()) {
                        Integer accountsCount = accountMap.getOrDefault(account.getType(), 0);
                        accountMap.put(account.getType(), accountsCount++);
                    }
                }
            }
        }
        AccountType mostPopularAccounType = null;
        Integer mostPopularAccounTypeCount = null;
        for (Map.Entry<AccountType, Integer> entry : (accountMap.entrySet())) {
            if (isNull(mostPopularAccounType)) {
                mostPopularAccounType = entry.getKey();
                mostPopularAccounTypeCount = entry.getValue();
            } else if (entry.getValue() > mostPopularAccounTypeCount) {
                mostPopularAccounType = entry.getKey();
                mostPopularAccounTypeCount = entry.getValue();
            }
        }
        if (isNull(mostPopularAccounType)) {
            throw new IllegalStateException();
        }
        return mostPopularAccounType;
    }

    /**
     * Metoda zwraca jaki rodzaj rachunku jest najpopularniejszy. Stwórz pomocniczą metdę getAccountStream. Jeżeli nie udało się znaleźć najpopularnijeszego
     * rachunku metoda ma wyrzucić wyjątek IllegalStateException. Pierwsza instrukcja metody to return. Napisz to za pomocą strumieni.
     */
    AccountType getMostPopularAccountTypeAsStream() {
        return getAccoutStream()
                .map(account -> account.getType())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .orElseThrow(IllegalStateException::new)
                .getKey();
    }

    /**
     * Zwraca pierwszego z brzegu użytkownika dla podanego warunku. W przypadku kiedy nie znajdzie użytkownika wyrzuca wyjątek IllegalArgumentException.
     */
    User getUser(final Predicate<User> predicate) {
        User firstUserForPredicate = null;
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                for (User user : company.getUsers()) {
                    if (predicate.test(user)) {
                        return firstUserForPredicate;
                    }
                }
            }
        }
        throw new IllegalArgumentException();
    }

    /**
     * Zwraca pierwszego z brzegu użytkownika dla podanego warunku. W przypadku kiedy nie znajdzie użytkownika wyrzuca wyjątek IllegalArgumentException. Napisz to
     * za pomocą strumieni.
     */
    User getUserAsStream(final Predicate<User> predicate) {
        return getUserStream()
                .filter(predicate)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników.
     */
    Map<String, List<User>> getUserPerCompany() {
        Map<String, List<User>> userPerCompany = new HashMap<>();
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                userPerCompany.put(company.getName(), company.getUsers());
            }
        }
        return userPerCompany;
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników. Napisz to za pomocą strumieni.
     */
    Map<String, List<User>> getUserPerCompanyAsStream() {
        return getCompanyStream()
                .collect(Collectors.toMap(Company::getName, Company::getUsers));
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników przechowywanych jako string składający się z imienia i nazwiska. Podpowiedź:
     * Możesz skorzystać z metody entrySet.
     */
    Map<String, List<String>> getUserPerCompanyAsString() {
        Map<String, List<String>> userPerCompanyAsString = new HashMap<>();
        for (Holding holding : holdings) {
            for (Company company : holding.getCompanies()) {
                    List<String> userNames = new ArrayList<>();
                    for(User user: company.getUsers()) {
                        userNames.add(user.getFirstName() + " " + user.getLastName());
                    }
                    userPerCompanyAsString.put(company.getName(), userNames);
            }
        }
        return userPerCompanyAsString;
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników przechowywanych jako string składający się z imienia i nazwiska. Podpowiedź:
     * Możesz skorzystać z metody entrySet. Napisz to za pomocą strumieni.
     */
    Map<String, List<String>> getUserPerCompanyAsStringAsStream() {
        return getCompanyStream()
                .collect(Collectors
                                    .toMap(
                                            Company::getName,
                                            (Company company) -> company.getUsers()
                                    .stream()
                                    .map(user -> user.getFirstName()+ " " + user.getLastName())
                                    .collect(Collectors.toList())));
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników przechowywanych jako obiekty typu T, tworzonych za pomocą przekazanej
     * funkcji.
     */
    <T> Map<String, List<T>> getUserPerCompany(final Function<User, T> converter) {
        return null;
    }

    /**
     * Zwraca mapę firm, gdzie kluczem jest jej nazwa a wartością lista pracowników przechowywanych jako obiekty typu T, tworzonych za pomocą przekazanej funkcji.
     * Napisz to za pomocą strumieni.
     */
    <T> Map<String, List<T>> getUserPerCompanyAsStream(final Function<User, T> converter) {
        return null;
    }

    /**
     * Zwraca mapę gdzie kluczem jest flaga mówiąca o tym czy mamy do czynienia z mężczyzną, czy z kobietą. Osoby "innej" płci mają zostać zignorowane. Wartością
     * jest natomiast zbiór nazwisk tych osób.
     */
    Map<Boolean, Set<String>> getUserBySex() {
        return null;
    }

    /**
     * Zwraca mapę gdzie kluczem jest flaga mówiąca o tym czy mamy do czynienia z mężczyzną, czy z kobietą. Osoby "innej" płci mają zostać zignorowane. Wartością
     * jest natomiast zbiór nazwisk tych osób. Napisz to za pomocą strumieni.
     */
    Map<Boolean, Set<String>> getUserBySexAsStream() {
        return null;
    }

    /**
     * Zwraca mapę rachunków, gdzie kluczem jesy numer rachunku, a wartością ten rachunek.
     */
    Map<String, Account> createAccountsMap() {
        return null;
    }

    /**
     * Zwraca mapę rachunków, gdzie kluczem jesy numer rachunku, a wartością ten rachunek. Napisz to za pomocą strumieni.
     */
    Map<String, Account> createAccountsMapAsStream() {
        return null;
    }

    /**
     * Zwraca listę wszystkich imion w postaci Stringa, gdzie imiona oddzielone są spacją i nie zawierają powtórzeń.
     */
    String getUserNames() {
        return null;
    }

    /**
     * Zwraca listę wszystkich imion w postaci Stringa, gdzie imiona oddzielone są spacją i nie zawierają powtórzeń. Napisz to za pomocą strumieni.
     */
    String getUserNamesAsStream() {
        return null;
    }

    /**
     * zwraca zbiór wszystkich użytkowników. Jeżeli jest ich więcej niż 10 to obcina ich ilość do 10.
     */
    Set<User> getUsers() {
        return null;
    }

    /**
     * zwraca zbiór wszystkich użytkowników. Jeżeli jest ich więcej niż 10 to obcina ich ilość do 10. Napisz to za pomocą strumieni.
     */
    Set<User> getUsersAsStream() {
        return null;
    }

    /**
     * Zwraca użytkownika, który spełnia podany warunek.
     */
    Optional<User> findUser(final Predicate<User> userPredicate) {
        return null;
    }

    /**
     * Zwraca użytkownika, który spełnia podany warunek. Napisz to za pomocą strumieni.
     */
    Optional<User> findUserAsStream(final Predicate<User> userPredicate) {
        return null;
    }

    /**
     * Dla podanego użytkownika zwraca informacje o tym ile ma lat w formie: IMIE NAZWISKO ma lat X. Jeżeli użytkownik nie istnieje to zwraca text: Brak
     * użytkownika.
     * <p>
     * Uwaga: W prawdziwym kodzie nie przekazuj Optionali jako parametrów. Napisz to za pomocą strumieni.
     */
    String getAdultantStatusAsStream(final Optional<User> user) {
        return null;
    }

    /**
     * Metoda wypisuje na ekranie wszystkich użytkowników (imie, nazwisko) posortowanych od z do a. Zosia Psikuta, Zenon Kucowski, Zenek Jawowy ... Alfred
     * Pasibrzuch, Adam Wojcik
     */
    void showAllUser() {
        throw new IllegalArgumentException("not implemented yet");
    }

    /**
     * Metoda wypisuje na ekranie wszystkich użytkowników (imie, nazwisko) posortowanych od z do a. Zosia Psikuta, Zenon Kucowski, Zenek Jawowy ... Alfred
     * Pasibrzuch, Adam Wojcik. Napisz to za pomocą strumieni.
     */
    void showAllUserAsStream() {
        throw new IllegalArgumentException("not implemented yet");
    }

    /**
     * Zwraca mapę, gdzie kluczem jest typ rachunku a wartością kwota wszystkich środków na rachunkach tego typu przeliczona na złotówki.
     */
    Map<AccountType, BigDecimal> getMoneyOnAccounts() {
        return null;
    }

    /**
     * Zwraca mapę, gdzie kluczem jest typ rachunku a wartością kwota wszystkich środków na rachunkach tego typu przeliczona na złotówki. Napisz to za pomocą
     * strumieni. Ustaw precyzje na 0.
     */
    Map<AccountType, BigDecimal> getMoneyOnAccountsAsStream() {
        return null;
    }

    /**
     * Zwraca sumę kwadratów wieków wszystkich użytkowników.
     */
    int getAgeSquaresSum() {
        return -1;
    }

    /**
     * Zwraca sumę kwadratów wieków wszystkich użytkowników. Napisz to za pomocą strumieni.
     */
    int getAgeSquaresSumAsStream() {
        return -1;
    }

    /**
     * Metoda zwraca N losowych użytkowników (liczba jest stała). Skorzystaj z metody generate. Użytkownicy nie mogą się powtarzać, wszystkie zmienną muszą być
     * final. Jeżeli podano liczbę większą niż liczba użytkowników należy wyrzucić wyjątek (bez zmiany sygnatury metody).
     */
    List<User> getRandomUsers(final int n) {
        return null;
    }

    /**
     * Metoda zwraca N losowych użytkowników (liczba jest stała). Skorzystaj z metody generate. Użytkownicy nie mogą się powtarzać, wszystkie zmienną muszą być
     * final. Jeżeli podano liczbę większą niż liczba użytkowników należy wyrzucić wyjątek (bez zmiany sygnatury metody). Napisz to za pomocą strumieni.
     */
    List<User> getRandomUsersAsStream(final int n) {
        return null;
    }

    /**
     * Stwórz mapę gdzie kluczem jest typ rachunku a wartością mapa mężczyzn posiadających ten rachunek, gdzie kluczem jest obiekt User a wartoscią suma pieniędzy
     * na rachunku danego typu przeliczona na złotkówki.
     */
    Map<AccountType, Map<User, BigDecimal>> getAccountUserMoneyInPLNMap() {
        return null;
    }

    /**
     * Stwórz mapę gdzie kluczem jest typ rachunku a wartością mapa mężczyzn posiadających ten rachunek, gdzie kluczem jest obiekt User a wartoscią suma pieniędzy
     * na rachunku danego typu przeliczona na złotkówki.  Napisz to za pomocą strumieni.
     */
    Map<AccountType, Map<User, BigDecimal>> getAccountUserMoneyInPLNMapAsStream() {
        return null;
    }

    /**
     * Podziel wszystkich użytkowników po ich upoważnieniach, przygotuj mapę która gdzie kluczem jest upoważnenie a wartością lista użytkowników, posortowana po
     * ilości środków na koncie w kolejności od największej do najmniejszej ich ilości liczonej w złotówkach.
     */

    Map<Permit, List<User>> getUsersByTheyPermitsSorted() {
        return null;
    }

    /**
     * Podziel wszystkich użytkowników po ich upoważnieniach, przygotuj mapę która gdzie kluczem jest upoważnenie a wartością lista użytkowników, posortowana po
     * ilości środków na koncie w kolejności od największej do najmniejszej ich ilości liczonej w złotówkach. Napisz to za pomoca strumieni.
     */

    Map<Permit, List<User>> getUsersByTheyPermitsSortedAsStream() {
        return null;
    }

    /**
     * Podziel użytkowników na tych spełniających podany predykat i na tych niespełniających. Zwracanym typem powinna być mapa Boolean => spełnia/niespełnia,
     * List<Users>
     */
    Map<Boolean, List<User>> divideUsersByPredicate(final Predicate<User> predicate) {
        return null;
    }

    /**
     * Podziel użytkowników na tych spełniających podany predykat i na tych niespełniających. Zwracanym typem powinna być mapa Boolean => spełnia/niespełnia,
     * List<Users>. Wykonaj zadanie za pomoca strumieni.
     */
    Map<Boolean, List<User>> divideUsersByPredicateAsStream(final Predicate<User> predicate) {
        return null;
    }

    /**
     * Zwraca strumień wszystkich firm.
     */
    private Stream<Company> getCompanyStream() {
        return holdings
                .stream()
                .flatMap(holding -> holding.getCompanies().stream());
    }

    /**
     * Zwraca zbiór walut w jakich są rachunki.
     */
    private Set<Currency> getCurenciesSet() {
        return null;
    }

    /**
     * Tworzy strumień rachunków.
     */
    private Stream<Account> getAccoutStream() {
        return getUserStream()
                .flatMap(user -> user.getAccounts().stream());
    }

    /**
     * Tworzy strumień użytkowników.
     */
    private Stream<User> getUserStream() {
        return holdings
                .stream()
                .flatMap(holding -> holding.getCompanies().stream())
                .flatMap(company -> company.getUsers().stream());
    }

}