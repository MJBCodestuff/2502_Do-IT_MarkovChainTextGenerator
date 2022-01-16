package model;

import java.io.Serializable;
import java.util.*;

public class MarkovChain implements Serializable {

    private final HashMap<String, ArrayList<String>> chain = new HashMap<>();
    private final ArrayList<String> seeds = new ArrayList<>();
    private String name;
    private String corpusName;

    /**
     * Constructor for an empty Chain
     */
    public MarkovChain(){

    }

    /**
     * Full Constructor for a filled chain
     * @param k Korpus that saves as a base for the chain
     * @param order Order of the chain, a number between 1 and 5
     */
    public MarkovChain(Korpus k, int order){
        this.corpusName = k.getTitle();
        this.name = "Chain " + order + "ter Ordnung für " + corpusName;
        // split corpus into sentences at the ". ", "! " and "? " token
        ArrayList<String> sentences = new ArrayList<>(Arrays.asList(k.getText().split("(?<=\\.\s)|(?<=\\?\s)|(?<=!\s)")));

        // convert list of sentences into list of list of words
        ArrayList<ArrayList<String>> words = makeWordlist(sentences, order);

        // Collect sentence seeds and
        // Build the chain
        buildTheChain(words, order);

    }

    /**
     * Constructs the Seedlist and the Chain as a HashMap from a List of List of Words
     * @param words List of List of words
     * @param order order of the chain
     */
    private void buildTheChain(ArrayList<ArrayList<String>> words, int order){
        for (ArrayList<String> sentence: words) {
            // first the sentence seed needs to be collected both in the seedlist and in the chain
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < order; i++) {
                sb.append(sentence.get(i));
            }
            String seedString = sb.toString();
            seeds.add(seedString);
            if (chain.containsKey(seedString)){
                ArrayList<String> temp = new ArrayList<>(chain.get(seedString));
                temp.add(sentence.get(order));
                chain.put(seedString, temp);
            }else{
                ArrayList<String> temp = new ArrayList<>();
                temp.add(sentence.get(order));
                chain.put(seedString, temp);
            }
            // next we build the rest of the chain
            for (int i = order; i < sentence.size()-1; i++) {
                // chain key needs to be the size of the order
                StringBuilder keybuilder = new StringBuilder();
                for (int j = order-1; j >= 0; j--) {
                    keybuilder.append(sentence.get(i-j));
                }
                String currentKey = keybuilder.toString();
                if (chain.containsKey(currentKey)){
                    ArrayList<String> temp = new ArrayList<>(chain.get(currentKey));
                    temp.add(sentence.get(i+1));
                    chain.put(currentKey, temp);
                }else{
                    ArrayList<String> temp = new ArrayList<>();
                    temp.add(sentence.get(i+1));
                    chain.put(currentKey, temp);
                }
            }
        }
    }

    /**
     * constructs a List of List of Words from a List of sentences
     * @param sentences List of Sentences
     * @param order Order of the chain
     * @return List of List of Words
     */
    private ArrayList<ArrayList<String>> makeWordlist(ArrayList<String> sentences, int order){
        // first make list of list of words
        ArrayList<ArrayList<String>> words = new ArrayList<>();
        for (String sentence : sentences) {
            words.add(new ArrayList<>(Arrays.asList(sentence.split(" "))));
        }

        //remove every list with order+1 or less words since they are useless for text generation
        Iterator<ArrayList<String>> itr = words.iterator();
        while (itr.hasNext()){
            ArrayList<String> temp = itr.next();
            if (temp.size() <= order+1){
                itr.remove();
            }
        }

        // add spaces after words back in except for the last word in the sentence
        for (ArrayList<String> sentence: words) {
            for (int i = 0; i < sentence.size()-1; i++) {
                sentence.set(i, sentence.get(i) + " ");
            }
        }
        return words;
    }

    /**
     * calls general generateText method with 3 paragraphs and randomized 3-6 sentences per paragraphs
     * @return generated Text
     */
    public String generateText(){
        Random r = new Random();
        return generateText(3 , r.nextInt(3)+3);
    }

    /**
     * uses generateSentence and combines a number of sentences into sentences and paragraphs
     * @param paragraphs number of paragraphs to generate
     * @param sentencesPerParagraph number of sentences per Paragraph
     * @return generated Text
     */
    public String generateText(int paragraphs, int sentencesPerParagraph){
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < paragraphs; i++) {
            for (int j = 0; j < sentencesPerParagraph; j++) {
                result.append(generateSentence());
            }
            result.append("\n\n");
        }
        return result.toString();
    }

    /**
     * Starts at a random point in the chain and walks it from there for up to six words, shaves of unwanted words afterwards
     * @return String: a title created from up to six words from the current chain
     */
    public String generateTitle(){
        Random randomizer = new Random();
        ArrayList<String> title = new ArrayList<>();
        ArrayList<String> disallowedStarts = new ArrayList<>(
                Arrays.asList("and", "is", "are", "or", "him", "her", "than", "too")
        );
        ArrayList<String> disallowedEndings = new ArrayList<>(
                Arrays.asList("and", "for", "he", "she", "a", "to", "or", "in", "on", "the", "that", "they", "at", "have", "of",
                        "would", "than", "what", "into", "but", "with", "quite", "his", "her", "its")
        );
        // ... I really should have saved the order of the chain ... this should get it
        int order = seeds.get(0).length() - seeds.get(0).replaceAll(" ", "").length();
        String titleseed = (String)chain.keySet().toArray()[randomizer.nextInt(chain.size())];
        String[] seedwords = titleseed.split("(?<=\s)");
        for (int i = 0; i < order; i++) {
            title.add(seedwords[i]);
        }
        outerloop:
        for (int i = order; i < 6; i++) {
            StringBuilder currentKey = new StringBuilder();
            for (int j = order; j >= 1; j--) {
                currentKey.append(title.get(title.size()-j));
            }
            if(chain.get(currentKey.toString()) == null){
                break outerloop;
            }
            // this is where the magic happens: We add a random value that fits the key
            title.add(chain.get(currentKey.toString()).get(randomizer.nextInt(chain.get(currentKey.toString()).size())));
        }

        // in this part we trim unwanted words from the start and the end of the title
        // the disallowed words are saved in lowercase and without punctuation or whitespaces, so we need to account for that
        Iterator<String> itr = title.iterator();
        boolean goOn = true;
        while (goOn){
            String temp = itr.next();
            if (disallowedStarts.contains(temp.toLowerCase().replaceAll("[.,\\/#!$%\\^&\\*;:{}=\\-_`~()\\s]",""))){
                itr.remove();
            }else{
                goOn = false;
            }
        }
        Collections.reverse(title);
        Iterator<String> itrBack = title.iterator();
        goOn = true;
        while (goOn){
            String temp = itrBack.next();
            if (disallowedEndings.contains(temp.toLowerCase().replaceAll("[.,\\/#!$%\\^&\\*;:{}=\\-_`~()\\s]",""))){
                itrBack.remove();
            }else{
                goOn = false;
            }
        }
        Collections.reverse(title);

        // put it together and ship it without punctuation
        StringBuilder titleString = new StringBuilder();
        for (String word:title) {
            titleString.append(word);
        }
        return titleString.toString().replaceAll("[.,\\/#!$%\\^&\\*;:{}=\\-_`~()]","").toUpperCase();
    }

    /**
     * Starts with a random sentence seed and walks the chain from there until the end of a sentence
     * @return generated sentence
     */
    public String generateSentence(){
        Random randomizer = new Random();
        ArrayList<String> result = new ArrayList<>();
        // ... I really should have saved the order of the chain ... this should get it
        int order = seeds.get(0).length() - seeds.get(0).replaceAll(" ", "").length();
        // first the seed
        String seedstring = seeds.get(randomizer.nextInt(seeds.size()));
        String[] seedwords = seedstring.split("(?<=\s)");
        for (int i = 0; i < order; i++) {
            result.add(seedwords[i]);
        }
        // while the last word in the sentence doesn't end with a dot, questionmark or exclamation mark
        while (result.get(result.size()-1).charAt(result.get(result.size()-1).length()-1) != '.' &&
                result.get(result.size()-1).charAt(result.get(result.size()-1).length()-1) != '!' &&
                result.get(result.size()-1).charAt(result.get(result.size()-1).length()-1) != '?'){
            StringBuilder currentKey = new StringBuilder();
            for (int i = order; i >= 1; i--) {
                currentKey.append(result.get(result.size()-i));
            }
            // this is where the magic happens: We add a random value that fits the key
            result.add(chain.get(currentKey.toString()).get(randomizer.nextInt(chain.get(currentKey.toString()).size())));

        }
        StringBuilder resultString = new StringBuilder();
        for (String word:result) {
            resultString.append(word);
        }
        resultString.append(" ");

        return resultString.toString();
    }


    public String getName() {
        return name;
    }

    public String getCorpusName() {
        return corpusName;
    }
}
