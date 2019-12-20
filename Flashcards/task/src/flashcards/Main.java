package flashcards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {
    static class Logger {
       ArrayList<String> log = new ArrayList<>();
       Scanner scanner = new Scanner(System.in);

       public void println(String text) {
           System.out.println(text);
           log.add(text);
       }

        public String nextLine() {
            String s = scanner.nextLine();
            log.add(s);
            return s;
        }

        public String[] getLog() {
           return log.toArray(new String[log.size()]);
        }

    }

    // static Scanner scanner = new Scanner(System.in);
    static Map<String, String> dict = new LinkedHashMap<>();
    static Map<String, Integer> rating = new LinkedHashMap<>();
    static Logger log = new Logger();

    public static Object getKeyFromValue(Map map, Object value) {
        for (Object obj: map.keySet()) {
            if (map.get(obj).equals(value)) {
                return obj;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        //public void main(String[] args) {
        boolean isImport = false;
        String importFileName = "";
        boolean isExport = false;
        String exportFileName = "";
        for(int i = 0; i < args.length; i++) {
            if ("-import".equals(args[i]) && i+1 < args.length) {
                isImport = true;
                importFileName = args[i+1];
            }
            if ("-export".equals(args[i]) && i+1 < args.length) {
                isExport = true;
                exportFileName = args[i+1];
            }
        }

        if (isImport) {
            importCards(importFileName);
        }
        executeActon();

        if (isExport) {
            exportCards(exportFileName);
        }
    }

    public static void executeActon() {
        String action;
        do {
            log.println("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):");
            action = log.nextLine();
            switch (action) {
                case "add":
                    addCard();
                    break;
                case "remove":
                    removeCard();
                    break;
                case "import":
                    importCards("");
                    break;
                case "export":
                    exportCards("");
                    break;
                case "ask":
                    askCard();
                    break;
                case "log":
                    saveLog();
                    break;
                case "hardest card":
                    getHardestCard();
                    break;
                case "reset stats":
                    resetStats();
                    break;
                default:
                    break;
            }
        } while (!"exit".equals(action));
        log.println("Bye bye!");
    }

    public static void addCard() {
        String cardTerm;
        String cardDefinition;

        log.println("The card:");
        cardTerm = log.nextLine();
        if (dict.containsKey(cardTerm)) {
            log.println("The card \""+cardTerm+"\" already exists.");
        } else {
            log.println("The definition of the card:");
            cardDefinition = log.nextLine();
            if (getKeyFromValue(dict, cardDefinition) != null) {
                log.println("The definition \""+cardDefinition+"\" already exists.");
            } else {
                dict.put(cardTerm, cardDefinition);
                log.println("The pair (\""+cardTerm+"\":\""+cardDefinition+"\") has been added.");
            }
        }
    }

    public static void removeCard() {
        log.println("The card:");
        String cardTerm = log.nextLine();

        rating.remove(cardTerm);

        if (dict.containsKey(cardTerm)) {
            dict.remove(cardTerm);
            log.println("The card has been removed.");
        } else {
            log.println("Can't remove \""+cardTerm+"\": there is no such card.");
        }
    }

    public static void askCard() {
        log.println("How many times to ask?");
        int cnt = Integer.parseInt(log.nextLine());

        Random random = new Random();

        Object[] keys = dict.keySet().toArray();
        Object[] values = dict.values().toArray();

        while (cnt-- > 0)  {
            int r = random.nextInt(keys.length);
            String randKey = keys[r].toString();
            String randValue = values[r].toString();

            log.println("Print the definition of \"" + randKey + "\":");
            String userAnswer = log.nextLine();

            if (userAnswer.equals(randValue)) {
                log.println("Correct answer.");
            } else {
                Object key = getKeyFromValue(dict, userAnswer);
                if (key != null) {
                    log.println("Wrong answer. The correct one is \"" + randValue + "\" "+
                            "you've just written the definition of \"" + key.toString() + "\"");
                    addRating(randKey);
                }
                else  {
                    log.println("Wrong answer. The correct one is \"" + randValue + "\".");
                    addRating(randKey);
                }
            }
        }
    }

    private static void addRating(String cardTerm) {
        if (rating.containsKey(cardTerm)) {
            rating.replace(cardTerm, rating.get(cardTerm) + 1);
        } else {
            rating.put(cardTerm, 1);
        }
    }

    public static void exportCards(String fName) {
        String fileName = "";
        if (fName.isEmpty()) {
            log.println("File name:");
            fileName = log.nextLine();
        } else {
            fileName = fName;
        }
        File file = new File(fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (Map.Entry<String, String> pair : dict.entrySet())  {
                printWriter.println(pair.getKey());
                printWriter.println(pair.getValue());

                if (rating.containsKey(pair.getKey())) {
                    printWriter.println(rating.get(pair.getKey()));
                } else {
                    printWriter.println(0);
                }
            }
            log.println(dict.size()+" cards have been saved.");
        } catch (IOException e) {
            log.println("An exception occurs "+e.getMessage());
        }
    }

    public static void importCards(String fName) {
        String fileName = "";

       if (fName.isEmpty()) {
            log.println("File name:");
            fileName = log.nextLine();
        } else {
            fileName = fName;
        }

        File file = new File(fileName);
        int cnt = 0;

        try (Scanner scn = new Scanner(file)) {
            while (scn.hasNext()) {
                cnt++;
                String cardTerm = scn.nextLine();
                String cardDefinition = scn.nextLine();
                int cardRating = Integer.parseInt(scn.nextLine());

                if (dict.containsKey(cardTerm)) {
                    // dict.remove(cardTerm);
                    dict.replace(cardTerm, cardDefinition);
                } else {
                    dict.put(cardTerm, cardDefinition);
                }

                if (rating.containsKey(cardTerm)) {
                    rating.replace(cardTerm, cardRating);
                } else {
                    rating.put(cardTerm, cardRating);
                }
            }
            log.println(cnt+" cards have been loaded.");
        } catch (FileNotFoundException e) {
            log.println("not found" );
        }
    }

    public static void saveLog() {
        log.println("File name:");
        String fileName = log.nextLine();
        File file = new File(fileName);

        try (PrintWriter printWriter = new PrintWriter(file)) {
            for (String logLine : log.getLog())  {
                printWriter.println(logLine);
            }
           log.println("The log has been saved.");
        } catch (IOException e) {
            log.println("An exception occurs "+e.getMessage());
        }
    }

    public static void getHardestCard() {
        int max = 0;

        for (Integer value: rating.values()) {
            if (value > max) {
                max = value;
            }
        }

        ArrayList<String> cards = new ArrayList<>();

        for (Map.Entry<String, Integer> pair : rating.entrySet())  {
            if (pair.getValue().equals(max)) {
                cards.add(pair.getKey());
            }
        }

        if (cards.size() == 0) {
            log.println("There are no cards with errors.");
        } else if(cards.size() == 1) {
            log.println("The hardest card is \"" + cards.get(0) + "\". You have " + max + " errors answering it.");
        } else {
            String cAll = "";
            for(String s : cards) {
                cAll.concat("\""+s+"\", ");
            }
            String cAll2 = cAll.substring(0, cAll.length()-1);
            log.println("The hardest cards are " + cAll2 + ". You have " + max + " errors answering them.");
        }
    }

    public static void resetStats() {
        rating.clear();
        log.println("Card statistics has been reset.");
    }
}
