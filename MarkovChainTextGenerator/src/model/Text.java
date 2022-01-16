package model;

public class Text {
    private final String title;
    private final String text;

    /**
     * Constructor for filled Textobject
     * @param title title of the Text
     * @param text content of the text
     */
    public Text(String title, String text){
        this.title = title;
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

}
