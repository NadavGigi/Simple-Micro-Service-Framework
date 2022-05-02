package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TrainModelEvent;

import java.util.Vector;

/**
 * Passive object representing a single GPU.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class GPU {
    enum Type {RTX3090, RTX2080, GTX1080}
    private int capacity;
    private int tickstoTrain;
    private int numofBatches;//number of batches we need to prossses for current model
    private int ticksforCurrentDataBatch;
    private int howmanytosend;//this is the number of db i can get back
    private boolean inprocces;




    private boolean isFinished;
    private Model model;
    private Cluster cluster;
    private Type type;
    private DataBatch inprogressdata;
    private int databackfromclustcounter;
    private Vector<DataBatch> unProcessedDataBatchVector;
    private Vector<DataBatch> processedDataBatchVector; //that data is waiting to be prossessed
    private Vector<DataBatch> TrainedDataVector;
    //---------------------------CONSTRUCTORS-------------------------------------------
    public GPU(String typeString, Model model){
        this.cluster = Cluster.getInstance();
        if(typeString == "RTX2080"){
            this.capacity = 16;
            this.tickstoTrain=2;
            this.type=Type.RTX2080;
        }
        else if(typeString == "GTX1080") {
            this.capacity = 8;
            this.tickstoTrain=4;
            this.type=Type.GTX1080;
        }
        else {
            this.capacity = 32;
            this.tickstoTrain = 1;
            this.type = Type.RTX3090;
        }
        this.model=model;
        this.isFinished=false;
        this.unProcessedDataBatchVector =  new Vector<DataBatch>();
        this.processedDataBatchVector =  new Vector<DataBatch>();
        this.TrainedDataVector=new Vector<DataBatch>();
        this.inprogressdata=null;
        this.inprocces=true;
        this.numofBatches = this.model.getData().getSize()/1000;
        for(int i=0;i<numofBatches;i++){
            DataBatch db=new DataBatch(this.model.getData(),i*1000,this);
            this.unProcessedDataBatchVector.add(db);
        }
        this.howmanytosend=numofBatches;
        this.databackfromclustcounter=0;


    }

    public GPU(){
        isFinished=true;
    }//construtor for testmodelevent
//-------------------------------FUNCTIONS-------------------------------------------

    //TODO
    public void sendBatches(){
        Vector<DataBatch> temp=new Vector<DataBatch>();
        while(howmanytosend>0){
            if(unProcessedDataBatchVector.size()!=0) {
                temp.add(unProcessedDataBatchVector.remove(0));
            }
            howmanytosend--;
        }
        cluster.recieveDBfromgpu(temp);
    }
    public void getBatchesFromCluster(){
        Vector<DataBatch> temp=cluster.withdrawDB(capacity-processedDataBatchVector.size(),this);
        for(int i=0;i<temp.size();i++){
            processedDataBatchVector.add(temp.get(i));
            howmanytosend++;
            databackfromclustcounter++;
        }
        if(inprogressdata==null &processedDataBatchVector.size()!=0) {
            inprogressdata = processedDataBatchVector.remove(0);
            ticksforCurrentDataBatch=tickstoTrain;
            inprocces=true;
        }
    }
    public void onTick(){
        getBatchesFromCluster();
        sendBatches();
        if(inprogressdata!=null){
            if(ticksforCurrentDataBatch>0)
                ticksforCurrentDataBatch--;
            if(ticksforCurrentDataBatch==0){
                TrainedDataVector.add(inprogressdata);
                if(!processedDataBatchVector.isEmpty()){
                    inprogressdata=processedDataBatchVector.remove(0);
                    ticksforCurrentDataBatch=tickstoTrain;
                }else{
                    if(TrainedDataVector.size()>=numofBatches){
                        model.setStatus(Model.Status.Trained);
                        isFinished=true;
                    }
                }
            }
        }

    }
    public void startProcessingTestEvent(TestModelEvent event){
        double prob=Math.random();
        if(event.getStudentdegree()== Student.Degree.MSc & prob>0.4)
            event.getModel().setResult(Model.Result.Good);
        else if(event.getStudentdegree()== Student.Degree.PhD & prob>0.2)
            event.getModel().setResult(Model.Result.Good);
        else
            event.getModel().setResult(Model.Result.Bad);
    }
    //----------------------GETTERS--------------------------------------------------------------
    public Vector<DataBatch> getUnProcessedDataBatchVector() {
        return unProcessedDataBatchVector;
    }
    public Vector<DataBatch> getProcessedDataBatchVector() {
        return processedDataBatchVector;
    }
    public Vector<DataBatch> getTrainedDataVector() {return TrainedDataVector;}
    public boolean isFinished() {return isFinished;}
    public boolean isInprocces() {
        return inprocces;
    }

    public Model getModel() {
        return model;
    }
    public int getCapacity() {
        return capacity;
    }
    public int getTickstoTrain() {
        return tickstoTrain;
    }
    public int getNumofBatches() {
        return numofBatches;
    }
    public int getTicksforCurrentDataBatch() {
        return ticksforCurrentDataBatch;
    }
    public int getHowmanytosend() {
        return howmanytosend;
    }
    public Cluster getCluster() {
        return cluster;
    }
    public DataBatch getInprogressdata() {
        return inprogressdata;
    }
    public int getDatabackfromclustcounter() {
        return databackfromclustcounter;
    }
    public Type getType() {
        return type;
    }

    //----------------------SETTERS--------------------------------------------------------------
    public void setModel(Model model) {
        this.model = model;
    }

}