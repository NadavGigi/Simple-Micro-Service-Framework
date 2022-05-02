package bgu.spl.mics.application.objects;

/**
 * Passive object representing a Deep Learning model.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class Model {



    public enum Status {PreTrained,Training, Trained, Tested}
    public enum Result {None,Good,Bad}
    private String name;
    private Data data;
    Status status;
    Result result;
    public void setResult(Result result) {
        this.result = result;
    }



    public String getName() {
        return name;
    }

    public Data getData() {
        return data;
    }

    public Status getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }

    public Model(String name, Data data){
        this.name=name;
        this.data = data;
        this.status = null;
        this.result=Result.None;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
