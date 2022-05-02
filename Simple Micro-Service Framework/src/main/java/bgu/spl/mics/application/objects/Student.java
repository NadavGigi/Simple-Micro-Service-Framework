package bgu.spl.mics.application.objects;

import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.Vector;

/**
 * Passive object representing single student.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Student {
    /**
     * Enum representing the Degree the student is studying for.
     */
    public enum Degree {
        MSc, PhD
    }
    private String name;
    private String department;
    private Degree status;
    private int publications;
    private Vector<Model> trainedModels;
    private int papersRead;
    private Vector<Model> models;


    public Student(String name, String department, String status, Vector<Model> models){
        this.name=name;
        this.department = department;
        switch (status){
            case "MSc":{
                this.status=Degree.MSc;
            }
            case "PhD":{
                this.status=Degree.PhD;
            }
        }
        this.publications = 0;
        this.papersRead = 0;
        this.models=models;
        trainedModels=new Vector<Model>();
    }

    public Vector<Model> getTrainedModels() {
        return trainedModels;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getPublications() {
        return publications;
    }

    public int getPapersRead() {
        return papersRead;
    }

    public Vector<Model> getModels() {
        return models;
    }
    public Degree getStatus(){
        return this.status;
    }

    public void upgradePapersRead(){
        this.papersRead++;
    }

    public void upgradePublications() {
        this.publications++;
    }
    public void setPapersRead(int papersRead) {
        this.papersRead = papersRead;
    }

}
