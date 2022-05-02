package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

import java.util.Vector;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 *     Callback<TrainModelEvent> cb = new Callback<TrainModelEvent>() {
 *         @Override
 *         public void call(TrainModelEvent c) {
 *             System.out.println("I wish");
 *         }
 *     };
 */
public class StudentService extends MicroService {

    Student student;

    Vector<Model> modelsToPublish = new Vector<Model>();
    private Model.Status Trained;

    public StudentService(Student student) {
        super("Student");
        this.student = student;
    }


    @Override
    protected synchronized void initialize() {
        subscribeBroadcast(FinishBroadcast.class, a -> {
            Thread.currentThread().interrupt();
            terminate();
        });
        subscribeBroadcast(PublishConferenceBroadcast.class, (PublishConferenceBroadcast pcb) -> {
            this.student.setPapersRead(Math.max(0,pcb.getConference().getModels().size()-this.student.getPublications()));
        });
        if (student.getModels() == null) {
            return;
        }
        Thread t = new Thread(() -> {
            for (Model m : student.getModels()) {
                TrainModelEvent trainModelEvent = new TrainModelEvent(m);
                TestModelEvent testModelEvent = new TestModelEvent(this.student.getStatus(), m);
                Future<Model.Status> futureTrain = sendEvent(trainModelEvent);
                if (futureTrain.get() == Model.Status.Trained) {
                    this.student.getTrainedModels().add(m);
                    Future<Model.Result> futureTest = sendEvent(testModelEvent);
                    if (futureTest.get() == Model.Result.Good) {
                        this.student.upgradePublications();
                        sendEvent(new PublishResultsEvent(m));
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}