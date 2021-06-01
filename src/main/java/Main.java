import static com.mongodb.client.model.Filters.eq;
import java.util.*;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class Main {
    static Scanner scanner;
    static MongoCollection<Document> collection;
    static int id = 0;

    public static void main(String[] args) {
        String user = "student01";
        String password = "student01";
        String host = "localhost";
        int port = 27017;
        String database = "database01";
        String clientURI = "mongodb://" + user + ":" + password + "@" + host + ":" + port + "/" + database;
        MongoClientURI uri = new MongoClientURI(clientURI);
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase db = mongoClient.getDatabase(database);
        db.getCollection("library").drop();
        collection = db.getCollection("library");
        scanner = new Scanner(System.in);
        while (true) {
            showAllRecords();
            showMenu();
            switch (scanner.nextInt()) {
                case 0:
                    mongoClient.close();
                    System.out.println("Zakonczono");
                    return;
                case 1:
                    saveRecord();
                    break;
                case 2:
                    updateRecord();
                    break;
                case 3:
                    deleteRecord();
                    break;
                case 4:
                    getRecordById();
                    break;
                case 5:
                    getRecordByTitle();
                    break;
                case 6:
                    processing();
                    break;
            }
        }
    }

    private static void showAllRecords() {
        System.out.println("\nWszystkie rekordy w bazie:");
        FindIterable<Document> iterDoc = collection.find();
        Iterator it = iterDoc.iterator();
        while (it.hasNext())
            System.out.println(it.next());
        System.out.println("\n");
    }

    private static void processing() {
        System.out.println("Kategoria do przetwarzania: ");
        String category = categoryMenu();
        scanner.nextLine();
        System.out.println("Ustaw nowy rok dla ksiazek w tej kategorii: ");
        String year = scanner.nextLine();
        System.out.println("Rok wszystkich ksiazek z kategorii " + category + " zostanie ustawiony na " + year);
        collection.updateMany(eq("category", category), new Document("$set", new Document("year", year)));
    }

    private static void getRecordByTitle() {
        scanner.nextLine();
        System.out.println("Podaj tytul do pobrania: ");
        String title = scanner.nextLine();
        System.out.println("Znaleziono: " + collection.find(eq("title", title)).first().toJson());
    }

    private static void getRecordById() {
        System.out.println("Podaj id do pobrania: ");
        int id = scanner.nextInt();
        System.out.println("Znaleziono: " + collection.find(eq("_id", id)).first().toJson());
    }

    static void deleteRecord() {
        scanner.nextLine();
        System.out.println("Podaj tytul ktory chcesz usunac: ");
        String title = scanner.nextLine();
        collection.deleteOne(eq("title", title));
    }

    static void updateRecord() {
        scanner.nextLine();
        System.out.println("Podaj tytul wg ktorego aktualizujesz: ");
        String title = scanner.nextLine();
        System.out.println("Podaj zaktualizowany rok: ");
        String year = scanner.nextLine();
        collection.updateOne(eq("title", title), new Document("$set", new Document("year", year)));
    }

    static void saveRecord() {
        scanner.nextLine();
        String title, year, category;
        System.out.println("Podaj tytul: ");
        title = scanner.nextLine();
        System.out.println("Podaj rok:");
        year = scanner.nextLine();
        category = categoryMenu();
        collection.insertOne(new Document("_id", ++id)
                .append("title", title)
                .append("year", year)
                .append("category", category));
    }

    static void showMenu() {
        System.out.print("\n2) Biblioteka (MongoDB)\n\nWybierz operacje:\n" +
                "1.Zapisywanie\n2.Aktualizowanie\n3.Kasowanie\n4.Pobieranie po ID\n5.Pobieranie (po tytule)\n" +
                "6.Przetwarzanie(po kategorii)\n0.Zakoncz\n\nWpisz cyfre i zatwierdz enterem: ");
    }

    enum Category {
        History, SciFi, Education;
    }

    static String getCategory(Category category) {
        return category.name();
    }

    static String categoryMenu() {
        System.out.println("Wybierz:\n1. " + Category.History.name() + "\n2. " + Category.SciFi.name() + "\n3. " + Category.Education.name());
        switch (scanner.nextInt()) {
            case 1:
                return getCategory(Category.History);
            case 2:
                return getCategory(Category.SciFi);
            case 3:
                return getCategory(Category.Education);
        }
        return "";
    }
}
