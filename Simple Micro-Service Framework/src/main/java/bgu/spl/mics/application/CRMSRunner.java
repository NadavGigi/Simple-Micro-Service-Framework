package bgu.spl.mics.application;

import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;


import java.io.*;
import java.util.Vector;

/** This is the Main class of Compute Resources Management System application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output a text file.
 */
public class CRMSRunner {

    public static void main(String[] args) {
        Vector<Student> allStudents = new Vector<Student>();
        Vector<Conference> allConferences = new Vector<Conference>();
        Cluster cluster = Cluster.getInstance();
        Vector<Thread> threads = new Vector<Thread>();
        File input = new File("example_input.json");
        try {
            System.out.println("lalala1");
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray students = (JsonArray) fileObject.get("Students");
            JsonArray GPUS = (JsonArray) fileObject.get("GPUS");
            for (JsonElement gpu : GPUS) {
                threads.add(new Thread(new GPUService(gpu.getAsString())));
            }
            JsonArray conferences = (JsonArray) fileObject.get("Conferences");
            for (JsonElement conference : conferences) {
                JsonObject conferenceObject = conference.getAsJsonObject();
                String nameConferences = conferenceObject.get("name").getAsString();
                int date = conferenceObject.get("date").getAsInt();
                Conference conferenceToAdd = new Conference(nameConferences, date);
                threads.add(new Thread(new ConferenceService(conferenceToAdd)));
                allConferences.add(conferenceToAdd);
            }
            JsonArray CPUS = (JsonArray) fileObject.get("CPUS");
            for (JsonElement cpu : CPUS) {
                CPU cpu_ = new CPU(cpu.getAsInt());
                threads.add(new Thread(new CPUService(cpu_)));
                cluster.addCPU(cpu_);
            }
            for (JsonElement student : students) {
                Vector<Model> vectorModels = new Vector<Model>();
                JsonObject studentObject = student.getAsJsonObject();
                String nameStudent = studentObject.get("name").getAsString();
                String department = studentObject.get("department").getAsString();
                String status = studentObject.get("status").getAsString();
                JsonArray models = (JsonArray) student.getAsJsonObject().get("models");
                for (JsonElement model : models) {
                    JsonObject modelObject = model.getAsJsonObject();
                    String nameModel = modelObject.get("name").getAsString();
                    String type = modelObject.get("type").getAsString();
                    int size = modelObject.get("size").getAsInt();
                    Data data = new Data(type, size);
                    Model m = new Model(nameModel, data);
                    vectorModels.add(m);
                }
                Student studentToAdd = new Student(nameStudent, department, status, vectorModels);
                threads.add(new Thread(new StudentService(studentToAdd)));
                allStudents.add(studentToAdd);
            }
            int tickTime = fileObject.get("TickTime").getAsInt();
            int duration = fileObject.get("Duration").getAsInt();
            threads.add(new Thread(new TimeService(tickTime, duration)));

            for (Thread thread : threads){
                thread.start();
            }
            for (Thread thread : threads){
                thread.join();
            }
        } catch (FileNotFoundException | InterruptedException e) {
            System.out.println("lalala2");

            e.printStackTrace();
        }
/**-------------------------OUTPUT-------------------------------**/
        try {
            Gson gson1 = new GsonBuilder().setPrettyPrinting().create();
            Writer writer = new FileWriter("output.json");
            gson1.toJson(new Output(allStudents, allConferences, cluster.getCpuTime(), cluster.getGPUTime(), cluster.getTotalFromCPU()), writer);
            writer.flush(); //flush data to file
            writer.close(); //close write
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
