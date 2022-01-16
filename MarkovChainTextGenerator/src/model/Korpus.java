package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Korpus implements Serializable {

    private final String title;
    private final String text;

    /**
     * constructor for an empty korpus
     */
    public Korpus(){
        this.title = "";
        this.text = "";
    }

    /**
     * Constructor for a filled Korpus
     * @param title title of the new Korpus
     * @param text content of the new Korpus
     */

    public Korpus(String title, String text){
        this.title = title;
        this.text = cleanupText(text);
    }


    /**
     * Removes control characters, double whitespaces and newlines
     * @param oldText the raw text to clean
     * @return String of cleaned text
     */
    public String cleanupText(String oldText){
        ArrayList<Character> allowedChars = new ArrayList<>(
                // I need to filter out both newline characters and other control characters, so a positive list might be the easiest solution
                // Problem are letters with accents & co -> limits the input languages, not elegant, need to think about this some more
                Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
                        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',',','.',
                        ';',':','-','_', '!', '"','€','$','%', '1','2','3','4','5','6','7','8','9','0','ä','Ä','ö','Ö','ü','Ü','ß','?', ' ', '\'', '’',
                        // French Letters
                        'À', 'à', 'Â', 'â', 'Æ', 'æ', 'Ç', 'ç', 'É', 'é', 'È', 'è', 'Ê', 'ê', 'Ë', 'ë', 'Î', 'î', 'Ï', 'ï', 'Ô', 'ô',
                        'Œ', 'œ', 'Ù', 'ù', 'Û', 'û', 'Ü', 'ü', 'Ÿ', 'ÿ',
                        // Vocals with Accent
                        'Á', 'á', 'É', 'é', 'Ó', 'ó', 'Í', 'í', 'Ú', 'ú')
        );
        ArrayList<Character> punctuation = new ArrayList<>(
                Arrays.asList(',','.',';',':','-','_', '!', '"','€','$','%','?','\'', '’', ' ')
        );
        StringBuilder finishedString = new StringBuilder();
        for (int i = 0; i < oldText.length(); i++) {
           boolean skipOne = false;
           if(allowedChars.contains(oldText.charAt(i))){
               // if the current character is allowed
               if(i != oldText.length() -1 && finishedString.length() > 0){
                   // for safety against over or underflows
                   if(finishedString.charAt(finishedString.length()-1) == ' ' && oldText.charAt(i) == ' '){
                       // if for whatever reason the algorithm tries to insert
                       // a blank space directly after another blank space we skip it
                       skipOne = true;
                   }
               }
               if(!skipOne){
                   finishedString.append(oldText.charAt(i));
               }

           }else{
               // Newlines get replaced by sentence end tokens if there isn't punctuation as the last character before the newline
               // other disallowed characters get replaced by blank spaces if there isn't one already present
               // and if there is no punctuation immediately after
               if (finishedString.length() > 0 && i != oldText.length()-1){
                   if (oldText.charAt(i) == '\n'){
                       if(!punctuation.contains(oldText.charAt(i-1))){
                           finishedString.append(". ");
                       }else{
                           finishedString.append(' ');
                       }
                   }else if (finishedString.charAt(finishedString.length()-1) != ' ' && !punctuation.contains(oldText.charAt(i+1))){
                       finishedString.append(' ');
                   }
               }
           }
        }

        return finishedString.toString();

    }





    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }
}
