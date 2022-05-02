package bgu.spl.mics;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
//.
/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private HashMap<MicroService, Vector<Message>> service_message; //Each Micro-Service specializes in processing one
																	// or more types of events
	private HashMap<Class<? extends Message>,Vector<MicroService>> message_service; // It is possible that there are several
																					//Micro-Services that specialize in the same events

	private HashMap<Message,Future> message_future;


	private static class MessageBusHolder{
		private static MessageBusImpl instance=new MessageBusImpl();
	}
	public static MessageBusImpl getInstance(){
		return MessageBusHolder.instance;
	}


	public HashMap<MicroService, Vector<Message>> getServiceMessage(){
		return this.service_message;
	}
	public HashMap<Message,Future> getMessageFuture() {return this.message_future;}
	public HashMap<Class<? extends Message>,Vector<MicroService>> getMessageService(){return this.message_service;}


	private MessageBusImpl(){
		service_message =new HashMap<MicroService, Vector<Message>>();
		message_service =new HashMap<Class<? extends Message>,Vector<MicroService>>();
		message_future=new HashMap<Message,Future>();
	}
	@Override
	public synchronized  <T> void  subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if(message_service.get(type)==null)
			message_service.put(type,new Vector<MicroService>());
		message_service.get(type).add(m);
		notifyAll();

	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if(message_service.get(type)==null)
			message_service.put(type,new Vector<MicroService>());
		message_service.get(type).add(m);
		notifyAll();
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(message_future.get(e)!=null)
			message_future.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for (MicroService m : message_service.get(b.getClass())) {
			service_message.get(m).add(b);
		}
	}

	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		MicroService microservice=null ;
		if(message_service.get(e.getClass())!=null) {   //round robin
			if(!message_service.get(e.getClass()).isEmpty()) {
				MicroService temp = message_service.get(e.getClass()).get(0);
				message_service.get(e.getClass()).remove(0);
				message_service.get(e.getClass()).add(temp);
				microservice=temp;
			}
		}
		if(microservice!=null) {
			service_message.get(microservice).add(e);
			Future<T> f = new Future<>();
			message_future.put(e, f);
			return f;
		}
		return null;
	}

	@Override
	public synchronized void register(MicroService m) {
		service_message.put(m, new Vector<Message>());
	}

	@Override
	public void unregister(MicroService m) {
		service_message.remove(m);
		for (Map.Entry<Class<? extends Message>,Vector<MicroService>> pair : message_service.entrySet()){
			for (int i = 0; i < pair.getValue().size(); i++) {
				pair.getValue().remove(m);
			}
		}

	}


	@Override
	public synchronized Message awaitMessage(MicroService m) throws InterruptedException {
		while(service_message.get(m).isEmpty()) {
			wait(1000);
			Thread.interrupted();
		}
		Vector<Message> tempService = service_message.get(m);
		Message output=tempService.firstElement();
		service_message.get(m).remove(output);
		return output;

	}


}