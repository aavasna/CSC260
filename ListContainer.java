package model;
/**
 * A container of Lists. The Lists have an inherent order in the collection.
 *
 * @author Thomas Breimer
 * @version 4/5/23
 */
public interface ListContainer
{
    /**
     * Adds a new List to the container
     * 
     * @param newList the new List to add
     */
    public void addList(List newList);
    
    /**
     * Removes the list at a position
     * 
     * @param position the position to remove the List, if one exists at that position
     */
    public void removeList(int position);
    
    /**
     * Gets the List at a position
     */
    public List getList(int position);

    public void clear(int studentID);
}
