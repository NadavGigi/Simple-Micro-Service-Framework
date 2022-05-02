package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Data {
    /**
     * Enum representing the Data type.
     */
    public enum Type {
        Images, Text, Tabular
    }

    private Type type;
    private int processed;
    private int size;
    public Data(String type, int size){
        switch (type){
            case "Images":{
                this.type=Type.Images;
            }
            case "Text":{
                this.type=Type.Text;
            }
            case "Tabular":{
                this.type=Type.Tabular;
            }
        }
        this.size=size;
        this.processed=0;

    }
    Data(){}

    public Type getType() {
        return type;
    }

    public int getProcessed() {
        return processed;
    }

    public int getSize() {
        return size;
    }
    public void addToProcessed(int num){processed+=num;}
}
