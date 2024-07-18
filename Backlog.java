package model;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The Backlog is a collection of Lists
 *
 * @author Thomas Breimer
 * @version 5/5/23
 */
public class Backlog implements ListContainer
{
    private ArrayList<List> lists;
    private int length;

    private int userID;

    /**
     * Constructs a Backlog
     */
    public Backlog(int userID)
    {
        this.userID = userID;
        lists = new ArrayList<List>();
        length = 0;
    }
    
    /**
     * Adds a new List to the Backlog
     * 
     * @param newList the new List we want to add to the backlog
     */
    public void addList(List newList){
        lists.add(newList);
        length += 1;
    }
    
    /**
     * Remove the List at a specific position
     * 
     * @param position the index to remove the List at
     */
    public void removeList(int position){
        lists.remove(position);
        length -= 1;
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
     * Returns the length of the Backlog, how many Lists are in it currently.
     */
    public int getLength() {
        return length;
    }
    
    /**
     * Gets the List at that position
     * 
     * @return the List at that position
     */
    public List getList(int position){
        return lists.get(position);
    }
    
    /**
     * Swaps the Lists at specified positions
     * 
     * @param pos1 the position of the first List to be swapped
     * @param pos2 the position of the second List to be swapped
     */
    public void swap(int pos1, int pos2){
        Collections.swap(lists, pos1, pos2);
    }

    /**
     * Clears the backlog.
     * @param userID new userID
     */
    public void clear(int userID) {
        this.userID = userID;
        lists = new ArrayList<List>();
        length = 0;
    }

    /**
     *
     * @return string representation of the object
     */
    public String toString() {
        String toReturn = "Backlog - " + "UserID: " + userID + "\n";

        for (int i = 0; i < lists.size(); i++) {
            toReturn += "List1: " + lists.get(i).toString() + "\n";
        }

        return toReturn;
    }
}
