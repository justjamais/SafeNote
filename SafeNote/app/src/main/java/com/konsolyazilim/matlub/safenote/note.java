package com.konsolyazilim.matlub.safenote;

public class note {

    private String text;
    private int  id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public note(String text, int id) {
        this.text = text;
        this.id = id;
    }

    @Override
    public String toString() {

        if (text.length() < 100)
            return text;
        else {

            return text.substring(0, Math.min(text.length(), 100)) + "...";
        }
    }
}
