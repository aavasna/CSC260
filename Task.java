package model;
/**
 * A Task is an object which is held in a List. It has name and can be checked or unchecked
 *
 * @author Thomas Breimer
 * @version 5/4/23
 */
public class Task
{
    private String name;
    private boolean checked;

    private int id;

    /**
     * Creates a new item. Items always start off unchecked.
     *
     * @param name   The name of the Task
     * @param id task id in DB
     */
    public Task(String name, int id)
    {
        this.name = name;
        this.id = id;
    }

    /**
     * Check off the Task
     */
    public void check(){
        checked = true;    
    }
    
    /**
     * Uncheck the Task
     */
    public void unCheck(){ 
        checked = false;
    }
    
    /**
     * Toggle whether the Task is checked or unchecked
     */
    public void toggle(){
        checked = !checked;
    }

    /**
     * Set the task checked value to a value
     * @param checked true or false, what to set the task checked to
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
    
    /**
     * Change the name of the Task
     * 
     * @param newName new name of the Task
     */
    public void changeName(String newName){
        name = newName;
    }
    
    /**
     * Get the name of the Task
     * 
     * @return the name of the Task
     */
    public String getName(){
        return name;
    }
    
    /**
     * Gets whether this Task is checked or unchecked. True if checked, False if unchecked.
     * 
     * @return whether the task is checked or unchecked
     */
    public boolean isChecked(){
        return checked;
    }

    /**
     * Gets ID of the task

     * @return
     */
    public int getID() {
        return id;
    }

    public String toString() {
        return getName() + ", Checked: " + isChecked();
    }
}
