package model;
import java.util.ArrayList;

/**
 * A collection of Students. Students have an order in the collection.
 *
 * @author Thomas Breimer, Guy Tallent
 * @version 5/22/23, 6/2/23
 */

public class Group {
    private ArrayList<Student> students;
    private String name;

    private int id;

    /**
     * Default constructor. Makes a Group with no students.
     */
    public Group(String name, int id){
        students = new ArrayList<Student>();
        this.name = name;
        this.id = id;
    }

    /**
     * Default constructor with student as parameter
     */
    public Group(Student s){
        students = new ArrayList<Student>();
        name = s.getName();
        id = s.getId();
    }

    /**
     * Add a new student to the end of the group
     * @param newStudent a student object to add
     */
    public void addStudent(Student newStudent){
        students.add(newStudent);
    }

    /**
     * Gets a student at the specified position
     * @param position the position of the student to get
     * @return the student to get. Returns null if out of bounds
     */
    public Student getStudent(int position){
        if (position >= 0 && position < getSize()){
            return students.get(position);
        }

        return null;
    }

    /**
     * Deletes the student at the specified position
     * @param position the position of the student to delete
     * @return the student to get. Returns null if out of bounds
     */
    public void deleteStudent(int position){
        if (position >= 0 && position < getSize()){
            students.remove(position);
        }
    }

    /**
     *
     * @return the size of the group
     */
    public int getSize(){
        return students.size();
    }

    /**
     * @return The name of the group
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return The id of the group
     */
    public int getID() {
        return id;
    }

    /**
     * Sets the name of the group
     * @param name name of the group
     */
    public void setName(String name) {
        this.name = name;
    }
}
