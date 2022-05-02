package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Conference;

public class PublishConferenceBroadcast implements Broadcast {
    Conference conference;

    public PublishConferenceBroadcast(Conference conference){
        this.conference = conference;
    }
    
    public Conference getConference(){
        return this.conference;
    }
}
