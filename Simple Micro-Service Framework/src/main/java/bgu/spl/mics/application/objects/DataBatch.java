package bgu.spl.mics.application.objects;

/**
 * Passive object representing a data used by a model.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */

public class DataBatch {
    Data data;
    int start_index;
    boolean isProcessed;

    public void setProcessed(boolean processed) {
        isProcessed = processed;
    }

    public boolean isProcessed() {
        return isProcessed;
    }

    GPU gpu;
    DataBatch(Data data, int start_index, GPU gpu){
        this.data = data;
        this.start_index = start_index;
        this.gpu = gpu;
        this.isProcessed=false;
    }

    public Data getData() {
        return data;
    }

    public int getStart_index() {
        return start_index;
    }

    public GPU getGpu() {
        return gpu;
    }
}
