package bgu.spl.mics.application.objects;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

public class CPUTest {
    static Thread t1;
    static CPU cpu;
    static GPU gpu1;
    static GPU gpu2;
    static Cluster cluster;
    static Data data;
    static Vector<DataBatch> dbVec;
    static Student student;
    static Model model;
    static DataBatch db;

    @Before
    public void setUp() throws Exception {
        cpu = new CPU(16);
        data = new Data("Images", 4000);
        Model model = new Model ("check", data);
        gpu1 = new GPU ("RTX3090",model);
        cluster = Cluster.getInstance();
        cluster.addCPU(cpu);
        db = new DataBatch(data,0,gpu1);
        gpu1 = new GPU ("RTX3090",model);
        gpu2 = new GPU("GTX1080",model);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testProcessData() {
        gpu1.sendBatches();
        gpu2.sendBatches();
        assertTrue(cluster.getTotalFromGPU()==8);
        cpu.ProcessData(db);
        cpu.onTick();
    }
}