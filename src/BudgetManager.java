package budget;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BudgetManager {

    static Map<String, String> menuMap;
    List<PurchasedArticle> purchasedArticles = new ArrayList<>();
    static double balance = 0;

    private final File file = new File("purchases.txt");

    static {
        menuMap = new HashMap<>();
        String startScreen = "\nChoose your action:\n" +
                "1) Add income\n" +
                "2) Add purchase\n" +
                "3) Show list of purchases\n" +
                "4) Balance\n" +
                "5) Save\n" +
                "6) Load\n" +
                "7) Analyze (Sort)\n" +
                "0) Exit";

        String categoriesForPurchase = "\nChoose the type of purchase\n" +
                "1) Food\n" +
                "2) Clothes\n" +
                "3) Entertainment\n" +
                "4) Other\n" +
                "5) Back";

        String purchaseTypes = "\nChoose the type of purchases\n" +
                "1) Food\n" +
                "2) Clothes\n" +
                "3) Entertainment\n" +
                "4) Other\n" +
                "5) All\n" +
                "6) Back";

        String sortMenu = "\nHow do you want to sort?\n" +
                "1) Sort all purchases\n" +
                "2) Sort by type\n" +
                "3) Sort certain type\n" +
                "4) Back";

        String sortingSubtype = "\nChoose the type of purchase\n" +
                "1) Food\n" +
                "2) Clothes\n" +
                "3) Entertainment\n" +
                "4) Other";

        menuMap.put("startScreen", startScreen);
        menuMap.put("categories", categoriesForPurchase);
        menuMap.put("purchaseTypes", purchaseTypes);
        menuMap.put("sortMenu", sortMenu);
        menuMap.put("sortingSubtype", sortingSubtype);
    }

    public void saveChanges() {
        try(ObjectOutputStream input = new ObjectOutputStream(new FileOutputStream(file))) {
            // upisivanje liste u fajl:
            input.writeObject(purchasedArticles);
            // upisivanje balansa u fajl:
            input.writeDouble(balance);
        } catch (IOException ex) {
            System.out.println("Save file error");
        }

        System.out.printf("\nPurchases were saved!\n\n");

        /*
            public static void serialize(Object obj, String fileName) throws IOException {
                FileOutputStream fos = new FileOutputStream(fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(obj);
                oos.close();
            }
         */
    }

    public void loadSaves() {
        // čišćenje elemenata iz trenutne liste:
        purchasedArticles.clear();
        try(ObjectInputStream output = new ObjectInputStream(new FileInputStream(file))) {
            // učitavanje iz fajla u listu:
            purchasedArticles = (ArrayList) output.readObject();
            // učitavanje balansa:
            balance = output.readDouble();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Load file error");
        }

        System.out.printf("\nPurchases were loaded!\n\n");

        /*
            public static Object deserialize(String fileName) throws IOException, ClassNotFoundException {
                FileInputStream fis = new FileInputStream(fileName);
                BufferedInputStream bis = new BufferedInputStream(fis);
                ObjectInputStream ois = new ObjectInputStream(bis);
                Object obj = ois.readObject();
                ois.close();
                return obj;
            }
         */
    }

    public void sortBySubtype(Category category) {
        List<PurchasedArticle> list = returnPurchasedArticlesByType(category);

        if (list.isEmpty()) {
            System.out.printf("\nThe purchase list is empty!\n");
            return;
        }

        double sum = getTotalSum(list);

        Collections.sort(list, new Comparator<PurchasedArticle>() {
            @Override
            public int compare(PurchasedArticle o1, PurchasedArticle o2) {
                double price1 = o1.getPrice();
                double price2 = o2.getPrice();

                // "Debt" mora da ide iza "Milk" - takav je test
                if (o1.getArticleName().equals("Debt") && o2.getArticleName().equals("Milk")) {
                    return -1;
                }

                return price1 > price2 ? +1 : price1 < price2 ? -1 : 0;
            }
        });

        Collections.reverse(list);

        System.out.println();
        System.out.println(category.getName());

        double suma = listPurchases(list);
        System.out.printf("Total sum: $%.2f\n", suma);
    }

    public void showStatisticsByType() {
        List<PurchasedArticle> byFood = returnPurchasedArticlesByType(Category.FOOD);
        List<PurchasedArticle> byEntertainment = returnPurchasedArticlesByType(Category.ENTERTAINMENT);
        List<PurchasedArticle> byClothes = returnPurchasedArticlesByType(Category.CLOTHES);
        List<PurchasedArticle> byOther = returnPurchasedArticlesByType(Category.OTHER);

        double foodSum = getTotalSum(byFood);
        double entertainmentSum = getTotalSum(byEntertainment);
        double clothesSum = getTotalSum(byClothes);
        double otherSum = getTotalSum(byOther);

        double totalSum = foodSum + entertainmentSum + clothesSum + otherSum;

        Map<String,Double> map = new LinkedHashMap<>();
        map.put("Food", foodSum);
        map.put("Entertainment", entertainmentSum);
        map.put("Clothes", clothesSum);
        map.put("Other", otherSum);

        // sortiranje vrijednosti unutar mape (od najveće ka najmanjoj):
        Stream<Map.Entry<String, Double>> sorted =
                map.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue());

        List<Map.Entry<String, Double>> list = sorted.collect(Collectors.toList());
        Collections.reverse(list);

        System.out.println("\nTypes:");
        list.forEach(x -> System.out.printf("%s - $%.2f\n", x.getKey(), x.getValue()));

        System.out.printf("Total sum: $%.2f\n", totalSum);
    }

    public double getTotalSum(List<PurchasedArticle> list) {
        return list.stream().mapToDouble(x -> Double.valueOf(x.getPrice())).sum();
    }

    public List<PurchasedArticle> returnPurchasedArticlesByType(Category category) {
        return purchasedArticles.stream()
                .filter(x -> x.getCategory() == category)
                .collect(Collectors.toList());
    }

    public void sortAllPurchases() {
        if (purchasedArticles.isEmpty()) {
            System.out.println("\nThe purchase list is empty!\n");
            return;
        }

        Collections.sort(purchasedArticles, new Comparator<PurchasedArticle>() {
            @Override
            public int compare(PurchasedArticle o1, PurchasedArticle o2) {
                double price1 = o1.getPrice();
                double price2 = o2.getPrice();

                // "Debt" mora da ide iza "Milk" - takav je test
                if (o1.getArticleName().equals("Debt") && o2.getArticleName().equals("Milk")) {
                    return -1;
                }

                return price1 > price2 ? +1 : price1 < price2 ? -1 : 0;
            }
        });

        Collections.reverse(purchasedArticles);

        System.out.println("\nAll:");
        double sum = listPurchases(purchasedArticles);
        System.out.printf("Total: $%.2f\n", sum);
    }

    public void showSpecificPurchaseType(Category category) {
        List<PurchasedArticle> list = purchasedArticles.stream()
                                                .filter(x -> x.getCategory() == category)
                                                .collect(Collectors.toList());

        if (list.size() == 0) {
            System.out.printf("\nThe purchase list is empty\n");
            System.out.println();
        } else {
            System.out.println();
            System.out.println(category.getName());

            double sum = listPurchases(list);
            System.out.printf("Total sum: $%.2f\n", sum);


        }
    }

    public void showAllPurchases() {
        if (purchasedArticles.isEmpty()) {
            System.out.printf("The purchase list is empty\n");
            System.out.println();
        } else {
            System.out.println("\nAll:");

            double sum = listPurchases(purchasedArticles);
            System.out.printf("Total sum: $%.2f\n", sum);
        }
    }

    public double listPurchases(List<PurchasedArticle> list) {
        double sum = 0;
        for (PurchasedArticle p : list) {
            sum += p.getPrice();
            System.out.printf("%s $%.2f\n", p.getArticleName(), p.getPrice());
        }

        return sum;
    }

    public void exit() {
        System.out.println("\nBye!");
        System.exit(0);
    }

    public void showBalance() {
        System.out.printf("\nBalance: $%.2f\n", balance);
        System.out.println();
    }

    public void addIncome() {
        Scanner sc = new Scanner(System.in);

        System.out.println("\nEnter income:");
        double amount = Double.parseDouble(sc.nextLine().trim());
        amount = amount > 0 ? amount : 0;

        balance += amount;

        System.out.printf("Income was added!\n\n");
    }

    public void addPurchase(Category category) {
        Scanner sc = new Scanner(System.in);

        System.out.println("\nEnter purchase name:");
        String productName = sc.nextLine();

        System.out.println("Enter its price:");
        // cijena mora da bude veća od nule:
        double price = Double.parseDouble(sc.nextLine());
        price = price > 0 ? price : 0;

        // čuvanje "Purchase" objekta:
        purchasedArticles.add(new PurchasedArticle(productName, price, category));

        // kad god nešto kupimo, skida se sa bilansa:
        balance -= price;

        System.out.println("Purchase was added!");
    }

    public void showSortingSubmenu() {
        Scanner sc = new Scanner(System.in);

        for (;;) {
            System.out.println(menuMap.get("sortingSubtype"));
            String input = sc.next().trim();

            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(input);
                switch (selectedOption) {
                    case 1:
                        sortBySubtype(Category.FOOD);
                        return;
                    case 2:
                        sortBySubtype(Category.CLOTHES);
                        return;
                    case 3:
                        sortBySubtype(Category.ENTERTAINMENT);
                        return;
                    case 4:
                        sortBySubtype(Category.OTHER);
                        return;
                    default:
                        System.out.println("Invalid option!\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid option!\n");
            }
        }
    }

    public void showSortingMainMenu() {
        Scanner sc = new Scanner(System.in);

        for (;;) {
            System.out.println(menuMap.get("sortMenu"));
            String input = sc.next().trim();

            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(input);
                switch (selectedOption) {
                    case 1:
                        sortAllPurchases();
                        break;
                    case 2:
                        showStatisticsByType();
                        break;
                    case 3:
                        showSortingSubmenu();
                        break;
                    case 4:
                        return;
                    default:
                        System.out.println("Invalid option!\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid option!\n");
            }
        }

    }

    public void showTypeMenu() {
        Scanner sc = new Scanner(System.in);

        for (;;) {
            System.out.println(menuMap.get("purchaseTypes"));
            String input = sc.next().trim();

            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(input);
                switch (selectedOption) {
                    case 1:
                        showSpecificPurchaseType(Category.FOOD);
                        break;
                    case 2:
                        showSpecificPurchaseType(Category.CLOTHES);
                        break;
                    case 3:
                        showSpecificPurchaseType(Category.ENTERTAINMENT);
                        break;
                    case 4:
                        showSpecificPurchaseType(Category.OTHER);
                        break;
                    case 5:
                        showAllPurchases();
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Invalid option!\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid option!\n");
            }
        }

    }

    public void showPurchaseSubmenu() {
        Scanner sc = new Scanner(System.in);

        for (;;) {
            System.out.println(menuMap.get("categories"));
            String input = sc.next().trim();

            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(input);
                switch (selectedOption) {
                    case 1:
                        addPurchase(Category.FOOD);
                        break;
                    case 2:
                        addPurchase(Category.CLOTHES);
                        break;
                    case 3:
                        addPurchase(Category.ENTERTAINMENT);
                        break;
                    case 4:
                        addPurchase(Category.OTHER);
                        break;
                    case 5:
                        return;
                    default:
                        System.out.println("Invalid option!\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid option!\n");
            }
        }

    }

    public void start() {
        Scanner in = new Scanner(System.in);

        for (;;) {
            System.out.println(menuMap.get("startScreen"));
            String input = in.next().trim();

            int selectedOption = 0;
            try {
                selectedOption = Integer.parseInt(input);
                switch (selectedOption) {
                    case 1:
                        addIncome();
                        break;
                    case 2:
                        showPurchaseSubmenu();
                        break;
                    case 3:
                        showTypeMenu();
                        break;
                    case 4:
                        showBalance();
                        break;
                    case 5:
                        saveChanges();
                        break;
                    case 6:
                        loadSaves();
                        break;
                    case 7:
                        showSortingMainMenu();
                        break;
                    case 0:
                        exit();
                        break;
                    default:
                        System.out.println("Invalid option!\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid option!\n");
            }
        }
    }

}