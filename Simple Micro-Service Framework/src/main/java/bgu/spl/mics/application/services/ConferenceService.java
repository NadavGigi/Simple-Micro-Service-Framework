package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.FinishBroadcast;
import bgu.spl.mics.application.objects.Conference;

import java.util.Vector;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class ConferenceService extends MicroService {
    static Vector<Conference> conferences = new Vector<Conference>();
    Conference conference;
    int timeTicks;
    public ConferenceService(Conference conference) {
        super("Conf");
        this.conference =conference;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(FinishBroadcast.class, a -> {
            terminate();
            Thread.currentThread().interrupt();
        });

        subscribeEvent(PublishResultsEvent.class, (PublishResultsEvent pre) -> {
            conference.addModel(pre.getModel());
        });
        subscribeBroadcast(TickBroadcast.class, (TickBroadcast tb) -> {
            timeTicks++;
            if(timeTicks == conference.getDate()){
                conferences.add(conference);
                sendBroadcast(new PublishConferenceBroadcast(conference));
            }
        });

    }
}
