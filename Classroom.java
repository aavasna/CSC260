package model;

import java.util.ArrayList;

/**
 * A classroom holds a collection of groups
 *
 * @author Thomas Breimer
 * @verison 5/29/23
 */

public class Classroom {

    private ArrayList<Group> groups;

    /**
     * Default constructor. Makes an empty group.
     */
    public Classroom(){
        groups = new ArrayList();
    }

    /**
     * Add a new group
     * @param group group to add
     */
    public void addGroup(Group group){
        groups.add(group);
    }

    /**
     * Gets the number of groups in the class
     */
    public int size(){
        return groups.size();
    }

    /**
     * Gets the group at the specified position
     * @param position position to get the group at
     * @return the group to get, null if out of bounds
     */
    public Group getGroup(int position) {
        if (position >= 0 && position < size()){
            return groups.get(position);
        }
        return null;
    }

    /**
     * Removes group at a specific position
     * @param position position of group to remove
     */
    public void removeGroup(int position) {
        if (position >= 0 && position < size()){
            groups.remove(position);
        }
    }

    /**
     * Resets the classroom object
     */
    public void reset() {
        this.groups = new ArrayList();
    }
}
