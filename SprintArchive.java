package model;
import java.util.ArrayList;
/**
 * A collection of old Sprints. Sprints in the collection have an inherent order.
 *
 * @author Thomas Breimer
 * @version 5/5/23
 */
public class SprintArchive
{
    private ArrayList<Sprint> sprints;

    private int userID;

    /**
     * Makes an empty Sprint archive
     */
    public SprintArchive(int userID)
    {
        this.userID = userID;
        sprints = new ArrayList<Sprint>();
    }

    /**
     * Adds a new Sprint to the archive
     *
     * @param newSprint a new Sprint to add
     */
    public void addSprint(Sprint newSprint)
    {
        sprints.add(newSprint);
    }

    /**
     * Get the Sprint at the given position
     * @param position
     */
    public Sprint getSprint(int position) {
        return sprints.get(position);
    }

    public ArrayList<Sprint> getArchive(){
        return sprints;
    }

    /**
     * Clears the archive and sets a new userID
     * @param userID new userID
     */
    public void clear(int userID) {
        this.userID = userID;
        this.sprints = new ArrayList<Sprint>();
    }

}
