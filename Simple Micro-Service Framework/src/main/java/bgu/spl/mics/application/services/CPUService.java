package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.CPU;
import bgu.spl.mics.application.objects.Cluster;

/**
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class CPUService extends MicroService {
    CPU cpu;
    public CPUService(CPU cpu) {
        super("CPU");
        this.cpu=cpu;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class , (TickBroadcast e) -> {setTick();
            });
        subscribeBroadcast(FinishBroadcast.class, a -> {
            Thread.currentThread().interrupt();
            terminate();
        });
    }
    public void setTick(){
        cpu.onTick();
    }
}