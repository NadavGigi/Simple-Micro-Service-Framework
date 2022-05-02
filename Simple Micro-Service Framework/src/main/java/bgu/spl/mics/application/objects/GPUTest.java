package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.jws.WebParam;
import java.util.Vector;

import static org.junit.Assert.*;

public class GPUTest {

    static GPU gpu1;
    static GPU gpu2;
    static Cluster cluster;
    static Model model;
    static Data data;
    static Student student;
    static Vector<DataBatch>  dbVec;
    static Vector<Model> models;
    static CPU cpu;




    @Before
    public void setUp() throws Exception {
        cluster = Cluster.getInstance();
        data = new Data("Images", 40000);
        model = new Model ("check", data);
        gpu1 = new GPU ("RTX3090",model);
        gpu2 = new GPU("GTX1080",model);
        models = new Vector<Model>();
        models.add(model);
        dbVec = new Vector<DataBatch>();
        student = new Student("name","departmant", "PhD",models);
        cpu = new CPU(32);
        cluster.addCPU(cpu);
    }

    @After
    public void tearDown() throws Exception {
        assertTrue(2==2);
    }

    @Test
    public void sendBatches() {
        assertTrue(2==2);
        gpu1.sendBatches();
        int afterSending =gpu1.getUnProcessedDataBatchVector().size();
        assertEquals(0,afterSending);
    }

    @Test
    public void getBatches() {
        gpu1.sendBatches();
        DataBatch db=new DataBatch(data,5,gpu1);
        dbVec.add(db);
        dbVec.add(db);
        dbVec.add(db);
        dbVec.add(db);
        dbVec.add(db);
        cluster.setProcessDB(gpu1,dbVec);
        int after=dbVec.size()+gpu1.getUnProcessedDataBatchVector().size();
        gpu1.getBatchesFromCluster();
        assertEquals(5,after);

    }


    @Test
    public void testOnTick() {

    }
}