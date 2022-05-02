package bgu.spl.mics.application.objects;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methoֺds to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	//CPUSDBforcomperator IS just for compartor TO KNOW HOW MANY ITEMS EACH CPU IS HANDLING AT THE MOMENT
	private HashMap<CPU, Vector<DataBatch>> CPUSDBforcomperator;
	private HashMap<CPU, Vector<DataBatch>> CPUSDB;
	private int totalFromGPU;
	private int totalFromCPU;
	PriorityBlockingQueue<CPU> CpusQueue;
	int numofprosseseddata;
	Vector<DataBatch> unProcessDB;
	HashMap<GPU, Vector<DataBatch>> ProcessDB;
	Object lockCPUSDB = new Object();
	Object lockProcessDB = new Object();
	private int GPUTime=0;
	private int howManyProcessed;
	private Vector<CPU> cpus;
	private Vector<Model> trainedModels = new Vector<Model>();
	private static class ClusterHolder {
		private static Cluster instance = new Cluster();
	}

	private Cluster() {
		this.numofprosseseddata = 0;
		this.unProcessDB = new Vector<DataBatch>();
		this.ProcessDB = new HashMap<GPU, Vector<DataBatch>>();
		this.CPUSDB = new HashMap<CPU, Vector<DataBatch>>();
		this.CPUSDBforcomperator = new HashMap<CPU, Vector<DataBatch>>();
		this.CpusQueue = new PriorityBlockingQueue<CPU>(20, (a, b) -> Integer.compare(a.getDataCurrentlyProccesing().size() / a.getCores(), b.getDataCurrentlyProccesing().size()) / b.getCores());
		this.cpus = new Vector<CPU>();
		this.totalFromCPU = 0;
		this.totalFromGPU = 0;
	}

	public void recieveDBfromgpu(Vector<DataBatch> vec) {
		if (vec != null) {
			synchronized (new Object()) {
				totalFromGPU += vec.size();
				GPUTime++;
				CPU cpu = cpus.remove(0);
				for (DataBatch db : vec) {
					if (cpu != null && db != null&&CPUSDB.get(cpu)!=null) {
						synchronized (lockCPUSDB) {
							CPUSDB.get(cpu).add(db);
							CPUSDBforcomperator.get(cpu).add(db);
						}
					}
				}
				cpus.add(cpu);
			}
		}
	}



	public Vector<DataBatch> withdrawDB(int maxCapacity, GPU gpu) {
		Vector temp = new Vector<DataBatch>();
		for (int i = 0; i < maxCapacity; i++) {
			if (ProcessDB.get(gpu) != null && ProcessDB.get(gpu).size() != 0)
				temp.add(ProcessDB.get(gpu).remove(0));
		}
		return temp;
	}

	public Vector<DataBatch> withdrawDB(CPU cpu) {
		synchronized (lockCPUSDB) {
			Vector<DataBatch> temp = CPUSDB.get(cpu);
			CPUSDB.remove(cpu);
			CPUSDB.put(cpu, new Vector<DataBatch>());
			return temp;
		}
	}

	public void addCPU(CPU cpu) {
	//	synchronized (lockCPUSDB) {
			CPUSDBforcomperator.put(cpu, new Vector<DataBatch>());
			CPUSDB.put(cpu, new Vector<DataBatch>());
			CpusQueue.add(cpu);
			cpus.add(cpu);
	//	}
	}

	public void recieveDBfromcpu(DataBatch db, CPU cpu) {
		totalFromCPU++;
		if(db!=null&&ProcessDB.containsKey(db.getGpu()))
			ProcessDB.get(db.getGpu()).add(db);
		else if(db!=null) {
			ProcessDB.put(db.getGpu(), new Vector<DataBatch>());
			ProcessDB.get(db.getGpu()).add(db);
		}
		if (CPUSDBforcomperator.get(cpu).size() != 0)
			CPUSDBforcomperator.get(cpu).remove(0);
	}

	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	public int getTotalFromGPU() {
		return totalFromGPU;
	}

	public int getTotalFromCPU() {
		return totalFromCPU;
	}
	public int getCpuTime(){
		int sum=0;
		for(CPU cpu:cpus){
			sum+=cpu.getTick();
		}
		return sum;
	}

	public Vector<Model> getTrainedModels() {
		return trainedModels;
	}

	public int getGPUTime(){
		return GPUTime;
	}


	public void setProcessDB(GPU gpu,Vector<DataBatch> vec) {
		if(!ProcessDB.containsKey(gpu)){
			ProcessDB.put(gpu,new Vector<DataBatch>());
		}
		int size= vec.size();
		for(int i=0;i<size;i++){
			ProcessDB.get(gpu).add(vec.get(i));
		}
	}
}