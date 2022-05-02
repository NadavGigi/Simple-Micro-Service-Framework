package bgu.spl.mics.application.services;

import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.FinishBroadcast;

import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Cluster;

import java.util.concurrent.TimeUnit;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private long speed;
	private int duration;
	private int tickCount;
	private MessageBusImpl msgBus;
	public TimeService(int speed,int duration){
		super("Time-Service");
		this.speed = TimeUnit.MILLISECONDS.toMillis(speed);;
		this.tickCount = 0;
		this.duration = duration;
		msgBus = MessageBusImpl.getInstance();
	}
	public void tick(){
		tickCount++;
	}
	@Override
	protected void initialize() {
		while(duration>=tickCount){
			sendBroadcast(new TickBroadcast());
			try{
				tick();
				Thread.sleep(speed);
			}
			catch (InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
		subscribeBroadcast(FinishBroadcast.class,(shut)->{
			terminate();
		});
		sendBroadcast(new FinishBroadcast());

	}

}