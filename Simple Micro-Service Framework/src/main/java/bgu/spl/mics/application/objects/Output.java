package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.objects.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Output {
    private Vector<Student> Students;
    private Vector<Conference> Conferences;
    private int CPUTimeUsed;
    private int GPUTimeUsed;
    private int AmountOfProcessedDataBatch;
    public Output(Vector<Student> students, Vector <Conference> conferences, int CPUTimeUsed, int GPUTimeUsed, int AmountOfProcessedDataBatch) {
        Students = students;
        Conferences = conferences;
        this.CPUTimeUsed = CPUTimeUsed;
        this.GPUTimeUsed = GPUTimeUsed;
        this.AmountOfProcessedDataBatch=AmountOfProcessedDataBatch;
    }
}