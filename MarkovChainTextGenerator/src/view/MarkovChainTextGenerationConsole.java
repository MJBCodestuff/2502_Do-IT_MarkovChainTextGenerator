package view;

import controller.MarkovChainTextGenerationController;

import java.util.Scanner;

public class MarkovChainTextGenerationConsole {

    public static void main(String[] args) {
        MarkovChainTextGenerationController mcc = new MarkovChainTextGenerationController();
        Scanner sc = new Scanner(System.in);
        outerloop:
        while(true){
            System.out.println("""
                    Was möchten Sie tun?
                    0) Beenden
                    1) Neuen Korpus eintragen
                    2) Korpus laden
                    3) Aktuellen Korpus ausgeben
                    4) Korpus löschen aus der Datenbank
                    5) Neue Chain erstellen aus aktuellem Korpus
                    6) Chain aus der Datenbank laden
                    7) Eine Chain aus der Datenbank löschen
                    8) Aktuell gespeicherte Korpusse anzeigen
                    9) Aktuell gespeicherte Chains anzeigen
                    10) Einen Satz aus der aktuellen Chain generieren
                    11) Einen Text generieren
                    12) Den aktuellen Text in der Datenbank speichern
                    13) Einen Text aus der Datenbank laden
                    14) In der Datenbank gespeicherte Texte anzeigen
                    15) Einen Text aus der Datenbank löschen""");
            int input = sc.nextInt();
            sc.nextLine();
            switch (input){
                case 0 -> {break outerloop;}
                case 1 -> {
                    System.out.println("Bitte Titel eingeben:");
                    String title = sc.nextLine();
                    System.out.println("Bitte Inhalt eingeben:");
                    sc.useDelimiter("fishfishfish");
                    String text = sc.next();
                    sc.reset();
                    sc.nextLine();
                    System.out.println(mcc.createNewKorpus(title, text));
                }
                case 2 -> {
                    System.out.println(mcc.dbCon.listAvailableKorpusses());
                    System.out.println("Bitte Titel eingeben:");
                    String title = sc.nextLine();
                    System.out.println(mcc.loadKorpusFromDatabase(title));
                }
                case 3 -> System.out.println(mcc.currentKorpus.getText());
                case 4 -> {
                    System.out.println(mcc.dbCon.listAvailableKorpusses());
                    System.out.println("Bitte zu löschenden Titel eingeben:");
                    String title = sc.nextLine();
                    System.out.println(mcc.deleteKorpusFromDatabase(title));
                }
                case 5 -> {
                    System.out.println("Bitte geben Sie die Ordnung der Chain ein:");
                    int order;
                    do {
                        order = sc.nextInt();
                        if(order > 5 || order < 1){
                            System.out.println("Die Ordnung der Chain muss eine Zahl von 1 bis 5 sein.");
                        }
                    }while(order > 5 || order < 1);

                    System.out.println(mcc.generateNewChain(order));
                }
                case 6 -> {
                    System.out.println(mcc.dbCon.listAvailableChains());
                    System.out.println("Bitte den Namen der zu ladenden Chain eingeben");
                    String name = sc.nextLine();
                    System.out.println(mcc.loadChainFromDatabase(name));
                }
                case 7 -> {
                    System.out.println(mcc.dbCon.listAvailableChains());
                    System.out.println("Bitte zu löschende Chain eingeben:");
                    String name = sc.nextLine();
                    System.out.println(mcc.deleteChainFromDatabase(name));
                }
                case 8 -> System.out.println(mcc.dbCon.listAvailableKorpusses());
                case 9 -> System.out.println(mcc.dbCon.listAvailableChains());
                case 10 -> System.out.println(mcc.currentChain.generateSentence());
                case 11 -> System.out.println(mcc.generateText());
                case 12 -> System.out.println(mcc.saveText());
                case 13 -> {
                    System.out.println(mcc.dbCon.listAvailableTexts());
                    System.out.println("Bitte den Titel des Textes eingeben");
                    String title = sc.nextLine();
                    System.out.println(mcc.loadText(title));
                }
                case 14 -> System.out.println(mcc.showTexts());
                case 15 -> {
                    System.out.println(mcc.dbCon.listAvailableTexts());
                    System.out.println("Bitte den Titel des Textes eingeben");
                    String title = sc.nextLine();
                    System.out.println(mcc.deleteText(title));
                }

            }
        }
        mcc.dbCon.closeConnection();
        sc.close();
    }
}
