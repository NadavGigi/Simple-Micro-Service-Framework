package bgu.spl.mics.application.objects;

import java.util.Vector;

/**
 * Passive object representing information on a conference.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Conference {

    private String name;
    private int date;
    Vector<Model> goodModels;
    public Conference(String name, int date){
        this.name = name;
        this.date = date;
        this.goodModels = new Vector<Model>();
    }
    public int getDate(){
        return this.date;
    }

    public void addModel(Model model){
        this.goodModels.add(model);
    }

    public Vector<Model> getModels(){
        return this.goodModels;
    }

    public String getName() {
        return name;
    }

    public Vector<Model> getGoodModels() {
        return goodModels;
    }
}
