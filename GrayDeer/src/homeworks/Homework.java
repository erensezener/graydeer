package homeworks;

import homeworks.configs.Config;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Executer;
import server.Utils;
import server.student.Student;

public abstract class Homework implements Serializable {

    protected double maxGrade = 5.0;
    protected boolean caseSensitive = false; // If capsSensitive is false, Eren and eren is the same.
    protected boolean whitespaceSensitive = false; // If whitespaceSensitive is false, Eren Sezener and Eren   Sezener is the same.
    //All of the build rules will be here
    private Config homeworkConfig = null;
    private String homeworkName = "";
    private String status = "";
    private String actions = "";
    private String grade = "";
    public String studentNumber = "";
    
    private String objection = "";
    private HashSet<String> inputHashSet = new HashSet<String>();
    private ArrayList<Case> cases = new ArrayList<Case>(); // FIXME convert to private

    public ArrayList<Case> getCases() {
        return cases;
    }

    public void setCases(ArrayList<Case> cases) {
        this.cases = cases;
    }
    private FileStorage fileStorage;
    private String homeworkSource = "";//string of the file
    //may be it can be deleted
    private boolean graded = false;

    /* inputToOutput Hashmap
     * Key: Inputs of a homework
     * Value: A class which maps outputs to grades
     * 
     * i.e Key: "10 4", Value gradeMap for input "10 4"
     */
    protected Map<String, GradeMap> inputToOutputMap = new HashMap<String, GradeMap>();

    /* gradeMap Clas, Inner Class of Homework
     * Key: Outputs of a student for a specific homework
     * Value: Grade of the student
     * 
     * i.e. Key: "14", Value: "5.0"
     */
    protected class GradeMap implements Serializable {

        protected Map<String, Double> outputToGrade = new HashMap<String, Double>();

        public GradeMap() {
            // TODO Auto-generated constructor stub
        }

        public void setOutputGradePair(String key, Double value) {
            outputToGrade.put(key, value);
        }

        public Double getGrade(String key) {
            Double value = outputToGrade.get(key);
            return value;
        }

        public Set<String> getKeySet() {
            return outputToGrade.keySet();
        }
    }

    public class Case implements Serializable {

        protected String input = null;
        protected String output = null;
        protected String answer = null;
        protected double grade = 0.0;

        public Case(String input, String output, String answer, double grade) {
            super();
            this.input = input;
            this.output = output;
            this.answer = answer;
            this.grade = grade;
            System.out.println("Created a case with input: " + input + " output: " + output + "\n  - answer: " + answer + " grade: " + grade);
        }

        // Getters and Setters
        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public double getGrade() {
            return grade;
        }

        public void setGrade(double grade) {
            this.grade = grade;
        }
    }

    public final void setBuildReady() {
        try {
            this.fileStorage = new FileStorage(this, this.homeworkSource);
            this.homeworkConfig.setArgs(this.fileStorage.writtenHomeworkFile, this.fileStorage.studentFolder, this.homeworkName);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(Homework.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //must be called in the constructor
    public abstract void setResults();

    // Constructors
    public Homework(String homeworkName, String homeworkSource, Config homeworkConfig) throws FileNotFoundException {
        this.homeworkName = homeworkName;
        this.homeworkSource = homeworkSource;
        this.homeworkConfig = homeworkConfig;
        if (!homeworkSource.equals("")) {
            this.setBuildReady();
        }

        this.setResults();

        // Transfer set contents to hashset
        Set<String> inputSet = this.getInputToOutputMap().keySet();
        for (String input : inputSet) {
            inputHashSet.add(input);
        }
    }

    public void finalizeHomework() {
        try {
            System.out.println("Finalizing...");
            this.fileStorage.buildFile();

            double totalGrade = 0;
            // Loop tests the homeworks for all potential inputs
            for (String key : this.inputToOutputMap.keySet()) {
                System.out.println("Trying this case: " + key);
                ArrayList<String> inputs = new ArrayList<String>();
                inputs.add(key);
                String output = this.fileStorage.runFile(inputs);
                System.out.println("Got this output: " + output);
                //for this input get the gradeMap
                GradeMap draft = this.inputToOutputMap.get(key);
                try {


                    // If students output is matching results, student gets full or partial credit
                    if (draft != null) {
                        //draft.outputToGrade.
                        //regex for whitspaces
                        double grade = draft.getGrade(output.replaceAll("\\s", ""));
                        totalGrade += grade;
                        System.out.println("Good Job!");

                        //Same with 1
                        Set<String> keySet = inputToOutputMap.get(key).getKeySet();
                        System.out.println("Key is " + key);
                        for (String k : keySet) {
                            System.out.println("AAA: " + k);

                            if(output.trim().equalsIgnoreCase(k.trim())){
                                     Case c = new Case(key, output, k, grade);
                            cases.add(c);

                            }
                       

                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    // If students output is not matching any result, student gets no credit
                    System.out.println("Bad Output! It must be " + draft.outputToGrade.keySet().toArray()[0]);

                    double grade = 0.0;

                    Set<String> keySet = inputToOutputMap.get(key).getKeySet();
                    for (String k : keySet) {
                        System.out.println("AAA: " + k);

                        Case c = new Case(key, output, k, grade);
                        cases.add(c);

                    }
                }
                System.out.println("");
            }

            //setting the final grade
            this.grade = "" + totalGrade;
            System.out.println("TotalGrade: " + totalGrade);
            //this.grade = (int) totalGrade;
            //if it is not catched
            this.graded = true;

        } catch (NullPointerException e) {
            //this means that your output is completely wrong and not in 
            // the gradeMap
            System.out.println("Bad Output!");
        } catch (Exception e) {
            System.out.println("Unable to finalize homework!:");
            e.printStackTrace();
        }

    }

    // Getters and Setters
    public double getMaxGrade() {
        if (graded) {
            return maxGrade;
        } else {
            return -1;
        }
    }

    public void setMaxGrade(double max) {
        maxGrade = max;
    }

    public void transformResults() { //TODO To be tested.

        // If caseSensitive is false, creates lower case copies of all gradeMap elements
        if (!caseSensitive) {
            Homework.GradeMap gm = this.new GradeMap();
            Set<String> keys = gm.getKeySet();
            for (String key : keys) {
                String newKey = key.toLowerCase();
                double newValue = gm.getGrade(key);
                gm.setOutputGradePair(newKey, newValue);
            }
        }
        if (!whitespaceSensitive) {
            //TODO 
        }

    }

    public final class FileStorage implements Serializable {
        //homeworkName

        public String homeworkName; //Homework name: ie "Square", "Echo"
        public String homeworkFileString; //This is not a path, it is the source
        public Student student;
        public String homeworkStoragePath; //Let's say in /Users/tdgunes/homeworks/
        public String studentFolder;
        public String writtenHomeworkFile;
        //this will be created in constructor
        //the last path of students homework and it is the path
        public Config config;
        public boolean isBuild = false;

        public FileStorage(Homework homework, String homeworkFileString) throws FileNotFoundException {

            this.homeworkFileString = homeworkFileString;
            //this.student = InformationParser.parse(homeworkFileString);
            this.homeworkName = homework.homeworkName;
            this.config = homework.homeworkConfig;

            // homeworksStoragePath -> /Users/tdgunes/homeworks/

            this.homeworkStoragePath = Utils.combine(homework.homeworkConfig.getStoragePath(), this.homeworkName);
            //Users/tdgunes/homeworks/MonteCarlo/
            Utils.createTheDir(this.homeworkStoragePath); //homework dir is created

            //FIXME SEVERE PROBLEM HERE
            this.studentFolder = Utils.combine(this.homeworkStoragePath, studentNumber);
            Utils.createTheDir(this.studentFolder);
            //Users/tdgunes/homeworks/MonteCarlo/S002423

            this.writtenHomeworkFile = Utils.combine(this.studentFolder, homeworkName + homework.homeworkConfig.conf.get("Extension"));
            //Users/tdgunes/homework/MonteCarlo/s002423/MonteCarlo.java
            Utils.writeAFile(this.writtenHomeworkFile, homeworkFileString); //writing source to the file


            //initiation is completed :)



        }

        public void buildFile() {//FIXME make this work with a config class
            //javac ./Homework1.java -d ./build/


            //FIXME we can change it
            //FIXME we can put them into Config.java
            System.out.println("Building... " + config.buildArgs.toString() + "\"");
            try {
                Executer executer = new Executer(config.buildArgs);
                executer.executeWithoutInputs();
                this.isBuild = true;
            } catch (Exception e) {
                //build problem
                throw new UnsupportedOperationException("BUILD FAILED:" + this.writtenHomeworkFile
                        + "\n" + e.getMessage());
            }

        }

        public String runFile(ArrayList<String> inputs) {


            System.out.println("Running... " + config.runArgs.toString() + "\"");

            Executer executer = new Executer(config.runArgs);
            try {

                String output = executer.execute(inputs); //FIXME inputs must be corrected
                System.out.println("OUTPUT:****\n" + output);

                return output;
            } catch (Exception e) {
                throw new UnsupportedOperationException("BUILD FAILED FOR:" + this.writtenHomeworkFile
                        + "\n" + e.getMessage());
            }
        }

        public Student getStudent() {
            return student;
        }
    }

    // Getter and Setters are here
    public String getHomeworkName() {
        return homeworkName;
    }

    public void setHomeworkName(String homeworkName) {
        this.homeworkName = homeworkName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getHomeworkSource() {
        return homeworkSource;
    }

    public void setHomeworkSource(String homeworkSource) {
        this.homeworkSource = homeworkSource;
    }

    public Map<String, GradeMap> getInputToOutputMap() {
        return inputToOutputMap;
    }

    public void setInputToOutputMap(Map<String, GradeMap> inputToOutputMap) {
        this.inputToOutputMap = inputToOutputMap;
    }
    //	public Set<String> getInputSet() {
    //		return inputSet;
    //	}
    //
    //	public void setInputSet(Set<String> inputSet) {
    //		this.inputSet = inputSet;
    //	}
    
    
    public String getObjection() {
        return objection;
    }

    public void setObjection(String objection) {
        this.objection = objection;
    }
}
