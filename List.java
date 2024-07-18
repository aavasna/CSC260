 package model;
 import java.util.ArrayList;

/**
 * A collection of Tasks. Has a name and a description. Tasks also have an inherent order in the collection.
 *
 * @author Thomas Breimer
 * @version 5/4/23
 */
public class List
{
    private ArrayList<Task> tasks;
    private String name;

    private int length;

    private int id;

    /**
     * Creates a new List.
     *
     * @param name the name of the List
     */
    public List(String name, int id){
        this.name = name;
        this.tasks = new ArrayList<Task>();
        this.length = 0;
        this.id = id;

    }   

    /**
     * Gets the name of the List
     * 
     * @return the name of the List
     */
    public String getName(){
        return name;
    }


    /**
     * @return The length of the List, how many Tasks are in it
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Change the name of the List
     * 
     * @param newName the new name of the List
     */
    public void changeName(String newName){
        name = newName;
    }

    
    /**
     * Adds a task to the List. Tasks always start off unchecked.
     * 
     * @param name the name of the new Task object to be added
     */
    public void addTask(String name, int taskID){
        tasks.add(new Task(name, taskID));
        this.length += 1;
    }
    
    /**
     * Get a Task's position in the List (0 to (Task.getLength - 1)) via its name. 
     * Returns -1 if there is no Task of that name. If there are multiple Tasks of that name, gets the first one.
     * 
     * @return position of the specified Task in the List
     */
    public int getTaskPosition(String name){
        for(int i = 0; i < tasks.size(); i++){
            if (name.equals(tasks.get(i).getName())){
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * Remove the Task at the specified position
     * 
     * @param position the position of the Task in the List
     */
    public void removeTask(int position){
        if (getLength() > position){
            tasks.remove(position);
            this.length -= 1;
        }
    }
    
    /**
     * Returns the name of the Task at that position.
     * 
     * @param position the position of the Task we want to get
     * @return the name of the Task at that position. null if position out of bounds
     */
    public String getTaskName(int position){
        if (getLength() > position) {
            return tasks.get(position).getName();
        }

        return null;
    }
    
    /**
     * Returns whether the Task at that position is checked
     * 
     * @param position the position of the Task we want to get
     * @return True if the Task is checked, False otherwise. False if the position is out of bounds
     */
    public boolean taskIsChecked(int position){
        if (getLength() > position) {
            return tasks.get(position).isChecked();
        }
        return false;
    }
    
    /**
     * Toggle whether the Task at a position is checked
     */
    public void toggleTaskChecked(int position){
        if (getLength() > position) {
            tasks.get(position).toggle();
        }
    }

    /**
     * Change the name of a task at the specified position
     * @param position the position of the task
     * @param newName the new name of the task
     */
    public void changeTaskName(int position, String newName){
        if (getLength() > position) {
            tasks.get(position).changeName(newName);
        }
    }

    /**
     * Returns the list id in the database
     */
    public int getID() {
        return id;
    }


    /**
     *
     * @param taskIndex
     * @return the ID of the task at that positon
     */
    public int getTaskID(int taskIndex) {
        if (getLength() > taskIndex) {
            return tasks.get(taskIndex).getID();
        }

        return -1;
    }

    /**
     *
     * @return string representation of the object
     */
    public String toString() {
        String toReturn = "List - Name: " + getName() + "\n";

        for (int i = 0; i < getLength(); i++) {
            toReturn += tasks.get(i).toString() + "\n";
        }

        return toReturn;
    }
}
