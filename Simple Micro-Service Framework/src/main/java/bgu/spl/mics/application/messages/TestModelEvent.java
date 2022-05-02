package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

public class TestModelEvent implements Event<Model.Result> {
    Student.Degree studentDegree;
    Model model;
    public TestModelEvent(Student.Degree studentdegree,Model model) {
        this.studentDegree = studentdegree;
        this.model=model;
    }

    public Model getModel() {
        return model;
    }

    public Student.Degree getStudentdegree() {
        return studentDegree;
    }


}