package model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
/**
 * A Sprint, has a name and a collection of Lists
 *
 * @author Thomas Breimer
 * @version 5/5/23
 */
public class Sprint implements ListContainer
{
    private String name;
    private String startDate;
    private String review;
    private boolean finished;
    private String finishDate;
    private ArrayList<List> lists;

    private int userID;

    /**
     * Constructs a Sprint given a name
     * 
     * @param name the name of the Sprint
     */
    public Sprint(String name, int userID)
    {
        this.name = name;
        this.review = "";
        this.lists = new ArrayList<List>();
        this.finished = false;
        this.userID = userID;
        DateTimeFormatter date = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();
        startDate = date.format(now);
    }
    
    /**
     * Adds a new List to the Sprint. Can only happen if the Sprint has not already been finished.
     * 
     * @param newList the new List we want to add to the Sprint
     */
    public void addList(List newList){
        lists.add(newList);
    }
    
    /**
     * Remove the List at a specific position. Can only happen if the Sprint has not already been finished.
     * 
     * @param position the index to remove the List at
     */
    public void removeList(int position){
        if (position < lists.size() && position > -1){
            lists.remove(position);
        }
    }

    /**
     * Gets the position of the List by a name
     * @param name the name of the List
     * @return the position of the List, -1 if it does not exist
     */
    public int getListPosition(String name){
        for(int i = 0; i < lists.size(); i++){
            if (name.equals(lists.get(i).getName())){
                return i;
            }
        }

        return -1;
    }
    
    /**
     * Gets the List at that position. Returns no if it does not exist
     */
    public List getList(int position){
        if (position < lists.size() && position > -1){
            return lists.get(position);
        }

        return null;
    }

    /**
     * Complete a Sprint
     * @param review a user created review for the Sprint
     */
    public void finishSprint(String review){
        this.review = review;
        DateTimeFormatter date = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();
        finishDate = date.format(now);
    }

    /**
     *
     * @return the sprint name
     */
    public String getName(){
        return this.name;
    }

    /**
     *
     * @return the sprint reflection
     */
    public String getReflection(){
        return review;
    }

    /**
     *
     * @return the sprint finish date
     */
    public String getFinishDate() {
        return finishDate;
    }

    /**
     *
     * @return the sprint start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Changes the name of the sprint
     * @param newName
     */
    public void changeName(String newName){
        name = newName;
    }

    /**
     *
     * @return true if the sprint has no lists, false otherwise
     */
    public boolean isEmpty(){
        return lists.isEmpty();
    }

    /**
     *
     * @return an arrayList of all the lists in the sprint
     */
    public ArrayList<List> getLists(){
        return lists;
    }

    /**
     *
     * @return the number of lists in the sprint
     */
    public int getLength(){
        return lists.size();
    }

    /**
     * Clears the sprint with a new studentID
     * @param userID new studentID
     */
    public void clear(int userID) {
        this.userID = userID;
    }

    /**
     *
     * @return string representation of the object
     */
    public String toString() {
        String toReturn = "Sprint - Name: " + getName() + "UserID: " + userID;

        for (int i = 0; i < lists.size(); i++) {
            toReturn += "List1: " + lists.get(i).toString();
        }

        return toReturn;
    }

}
