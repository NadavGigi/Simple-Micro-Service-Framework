package bgu.spl.mics.application.objects;

import java.sql.Time;
import java.util.Collection;
import java.util.Comparator;
import java.util.Vector;

/**
 * Passive object representing a single CPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class CPU {
    private int timeforcurrentDB;
    private int numofBatchesProcessed;
    private int cores;

    public int getCores() {
        return cores;
    }

    private Vector<DataBatch> dataCurrentlyProccesing;
    private Cluster cluster;
    private int tick;
    private DataBatch currentDB;


    public CPU(int cores) {
        this.cores = cores;
        this.cluster = Cluster.getInstance();
        this.dataCurrentlyProccesing = new Vector<DataBatch>();
        this.tick = 0;
        this.currentDB = null;
        this.timeforcurrentDB = 0;
        this.numofBatchesProcessed = 0;
    }

    public int ProcessData(DataBatch dataToProcess) {
        int timeToProcess = 0;
        if (dataToProcess.getData().getType() == Data.Type.Images) {
            timeToProcess = (32 / this.cores) * 4;
        } else if (dataToProcess.getData().getType() == Data.Type.Text) {
            timeToProcess = (32 / this.cores) * 2;
        } else
            timeToProcess = (32 / this.cores) * 1;

        return timeToProcess;
    }
    public void onTick(){
        Vector<DataBatch> temp=cluster.withdrawDB(this);

        if(temp!=null) {
            for (DataBatch DB : temp) {
                dataCurrentlyProccesing.add(DB);
            }
        }
        if (currentDB != null) {
            tick++;
            if (timeforcurrentDB == 0) {
                currentDB.getData().addToProcessed(1000);
                numofBatchesProcessed++;
                cluster.recieveDBfromcpu(currentDB, this);
                if (dataCurrentlyProccesing.size() != 0) {
                    currentDB = dataCurrentlyProccesing.remove(0);
                    timeforcurrentDB = ProcessData(currentDB);
                }else{
                    currentDB=null;
                }
            }
            if(currentDB!=null)
                timeforcurrentDB--;
        } else {
            if (dataCurrentlyProccesing.size() != 0) {
                currentDB = dataCurrentlyProccesing.remove(0);
                timeforcurrentDB = ProcessData(currentDB);
                timeforcurrentDB--;
            } else
                currentDB = null;
        }
    }

    public Vector<DataBatch> getDataCurrentlyProccesing() {
        return dataCurrentlyProccesing;
    }

    public int getNumofBatchesProcessed() {
        return numofBatchesProcessed;
    }

    public int getTick() {
        return tick;
    }
}