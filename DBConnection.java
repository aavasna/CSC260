package com.runtimeTerror.scrumApp;

import java.sql.*;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;

/**
 * SQL Connection
 * @author: Colby Ryan, Thomas Breimer
 **/

public class DBConnection {

    /***
     * This opens the connection to the database
     * @return connection
     */
    public static Connection connectDB(){
        String url = "jdbc:mysql://127.0.0.1:3333/gonzalez_db";
        String username = "gonzalez";
        String password = "Un!on2668931";
        ArrayList<String> tasks = new ArrayList<String>();
        Connection connection;
        try{
            connection = DriverManager.getConnection(url, username, password);
        }catch (SQLException e) {
            throw new IllegalStateException("Cannot connect!", e);
        }
        return connection;
    }
    /***
     * Closes the connection to the database
     * @param connection
     */
    public static void closeDB(Connection connection){
        try{
            connection.close();
        }catch(SQLException e){
            throw new RuntimeException("Cannot close database", e);
        }
    }

//------------------------ ENCRYPTION ----------------------------

    /**
     * Implements BCrypt hashing function to encrypt plain text passsword. Returns
     * encrypted version of the password.
     * @param password_plaintext plain text password to be encrypted
     * @return encrypted password
     */
    public static String hashPassword(String password_plaintext) {
        String salt = BCrypt.gensalt(12);
        return BCrypt.hashpw(password_plaintext, salt);
    }

    /**
     * Implements BCrypt hashing function to verify if the input plain text password
     * is equal to the encrypted password stored in the database. Returns true if the 
     * passwords match, false otherwise.
     * @param inputPass plain text password input by user
     * @param hashPass encrypted password stored in the database
     * @return true if the passwords match (through verficiation function), else otherwise
     */
    public static boolean verifyPassword(String inputPass, String hashPass) {

        if (hashPass == null || !hashPass.startsWith("$2a$")){
            throw new IllegalArgumentException("Invalid hash provided for comparison");
        }

        return BCrypt.checkpw(inputPass, hashPass);
    }

//------------------------ CHECK INFO -----------------------------

    /**
     * Check if the given usename exists in the database
     * @param username the username we need to check for
     * @return true if the username exists in the database, else false
     */
    public static boolean usernameExists(String username) {
        Connection connection = connectDB();
        boolean exists = false;

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) FROM users WHERE username = '" + username + "'"; // was "SELECT COUNT(*) FROM users WHERE name = '" + username + "'"
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }

        closeDB(connection);
        return exists;
    }

   /**
   * Checks if the provided password matches any password of a professor
   *
   * @param password password to check.
   * @return true if the password matches the password of a professor
   *         in the database, otherwise return false
   */
    public static boolean professorExists(){
        Connection connection = connectDB();
        boolean exists = false;
        try {
            Statement statement = connection.createStatement();
            String query = "SELECT COUNT(*) FROM users WHERE role = false";  //is not case sensitive
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }

        closeDB(connection);
        return exists;
    }


    /**
     * Checks if a student username is unique
     *
     * @param username The student username to check for uniqueness.
     * @return true if the student unique is unique, false otherwise.
     */
    public static boolean validUsername(String username) {
        Connection connection = connectDB();
        boolean isUnique = true;
        ArrayList<String> usernames = new ArrayList<String>();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select username from users;");
            while(rs.next()){
                String user = rs.getString("username");
                usernames.add(user);
            }
            rs.close();
            statement.close();
            if(usernames.contains(username)){
            isUnique = false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return isUnique;
    }
    
    /***
    * Returns a boolean if a subtask is checked or not
    * @param subtaskid
    * @return boolean isChecked
    */
    public static boolean isCheckedSubTask(int subtaskid){
        boolean isChecked = false;
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select checked from Subtasks where subtaskid=" + subtaskid + ";");
            rs.next();
            int b = rs.getInt("checked");
            if(b==1){
                isChecked = true;
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return isChecked;
    }


//------------------------ GET INFO ------------------------------

    /**
     * Get the password for the student with the provided username
     * @param userid the username of the student we need password for
     * @return the password string
     */
    public static String getPasswordByUsername(String username) {
        return getPasswordByID(getUserIDbyUsername(username));
    }

    /**
     * Get the password for the student with the provided id
     * @param userid the id of the student we need password for
     * @return the password string
     */
    public static String getPasswordByID(int userid) {
        Connection connection = connectDB();
        String password = null;

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT password FROM users WHERE userid = " + userid + ";");

            if (rs.next()) {
                password = rs.getString("password");
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        
        closeDB(connection);
        return password;
    }

    /***
     * This returns an arraylist of integers of all the userids in a group
     * @param groupid given groupid
     * @return ids
     */
    public static ArrayList<Integer> getIDsByGroup(int groupid){
        ArrayList<Integer> ids = new ArrayList<Integer>();
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select userid from users where groupid = " + groupid + ";");
            while(rs.next()){
                int id = rs.getInt("userid");
                ids.add(id);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return ids;
    }

    /***
     * Returns a user's name based on their id
     * @param userid id of the user
     * @return String name
     */
    public static String getNameByID(int userid){
        String name = "";
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select name from users where userid = " + userid + ";");

            rs.next();
            name = rs.getString("name");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return name;
    }

    /***
     * This returns the userid of a user based on the given name
     * @param name name of the user
     * @return int userid
     */
    public static int getUserIDByName(String name){
        int userid;
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT userid FROM users WHERE name=\""+name+"\";");

            //rs.next() must be performed here because otherwise you get an SQLException Error. This still returns the first instance of "name"

            rs.next();
            userid= rs.getInt("userid");
            rs.close();
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return userid;
    }

    /**
     * Retrieves the username associated with the given user ID from the database.
     * @param userID The ID of the user whose username is to be retrieved.
     * @return The username associated with the given user ID, or null if no user with the specified ID is found.
     */
    public static String getUsernameByID(int userID){
        String name;
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT username FROM users WHERE userid=\""+userID+"\";");

            //rs.next() must be performed here because otherwise you get an SQLException Error. This still returns the first instance of "name"

            rs.next();
            name= rs.getString("username");
            rs.close();
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return name;
    }

    /**
     * Gets the user ID associated with the given username from the database.
     * @param username  username to retrieve the user ID.
     * @return user ID associated with the given username, or -1 if the username is not found.
     */
     public static int getUserIDbyUsername(String username){
         int userID = -1;
         Connection connection = connectDB();
         try{
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT userid FROM users WHERE username = '" + username + "'");
             if (rs.next()) {
                 userID = rs.getInt("userid");
             }
             rs.close();
             statement.close();
         } catch(SQLException e){
             throw new RuntimeException("Problems with database", e);
         } finally {
             closeDB(connection);
         }
         return userID;
     }

    /***
     * Returns an arraylist of strings of all the task names from one user
     * @param userid
     * @return ArrayList<String> of task names
     */
    public static ArrayList<String> getTasks(int userid){

        ArrayList<String> tasks = new ArrayList<String>();
        Connection connection = connectDB();

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Tasks WHERE userid = "+userid);
            while(rs.next()){
                //TASKS ADDED HERE. RIGHT NOW I ONLY ADD NAME TO ARRAYLIST. THIS CAN BE CHANGED.
                String name = rs.getString("name");
                tasks.add(name);
            }
            rs.close();
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return tasks;
    }
    /***
     * Returns the taskname of a single taskid
     * @param taskid
     * @return String name
     */
    public static String getTaskNameByID(int taskid){
        String tname = "";
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select name from Tasks where taskid = " + taskid + ";");

            rs.next();
            tname = rs.getString("name");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return tname;
    }
    /***
     * returns an arraylist of all the taskids in a users backlog
     * @param userid
     * @return ArrayList<Integer>
     */
    public static ArrayList<Integer> getTaskIdsInBacklog(int userid){
        ArrayList<Integer> taskids = new ArrayList<Integer>();
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select taskid from Tasks where userid = " + userid + " and backlog = 1;");
            while(rs.next()){
                //TASKS ADDED HERE. RIGHT NOW I ONLY ADD NAME TO ARRAYLIST. THIS CAN BE CHANGED.
                int taskid = rs.getInt("taskid");
                taskids.add(taskid);
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return taskids;
    }
    /***
     * Gets all the task id's in a single users' sprint
     * @param userid
     * @return arraylist integer of taskids
     */
    public static ArrayList<Integer> getTaskIdsInSprint(int userid){
        ArrayList<Integer> taskids = new ArrayList<Integer>();
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select taskid from Tasks where userid = " + userid + " and sprint = 1;");
            while(rs.next()){
                //TASKS ADDED HERE. RIGHT NOW I ONLY ADD NAME TO ARRAYLIST. THIS CAN BE CHANGED.
                int taskid = rs.getInt("taskid");
                taskids.add(taskid);
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return taskids;
    }
    /***
     * Returns the reflection for a single sprint archive by sprintarchiveid
     * @param sprintarchiveid
     * @return String reflection
     */
    public static String getReflectionFromSprintArchiveID(int sprintarchiveid){
        String reflection = "";
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select reflection from SprintArchive where sprintarchiveid = " + sprintarchiveid + ";");

            rs.next();
            reflection = rs.getString("reflection");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return reflection;
    }
    /***
     * returns all the sprintarchiveids for one user
     * @param userid
     * @return arraylist<integer> sprintarchiveids
     */
    public static ArrayList<Integer> getSprintArchiveIdsByUserid(int userid){
        ArrayList<Integer> sprintarchiveids = new ArrayList<Integer>();
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select sprintarchiveid from SprintArchive where userid = "+userid+";");
            while(rs.next()){
                //TASKS ADDED HERE. RIGHT NOW I ONLY ADD NAME TO ARRAYLIST. THIS CAN BE CHANGED.
                int id = rs.getInt("sprintarchiveid");
                sprintarchiveids.add(id);
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return sprintarchiveids;
    }
    /***
     * returns an arraylist of integers of all the distinct groupids
     * @return arraylist<integer> all distinct groupids
     */
    public static ArrayList<Integer> getAllGroupIds(){
        ArrayList<Integer> groupids = new ArrayList<Integer>();
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select distinct groupsid from team;");
            while(rs.next()){
                //TASKS ADDED HERE. RIGHT NOW I ONLY ADD NAME TO ARRAYLIST. THIS CAN BE CHANGED.
                int id = rs.getInt("groupsid");
                groupids.add(id);
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return groupids;
    }
    /***
     * Returns subtask name for a subtaskid
     * @param subtaskid
     * @return string subtaskname
     */
    public static String getSubtaskNameByID(int subtaskid){
        String subtaskname = "";
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select name from Subtasks where subtaskid=" + subtaskid + ";");
            rs.next();
            subtaskname = rs.getString("name");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return subtaskname;
    }
    

    /***
     * returns an arraylist of integers for all subtasks for a task
     * @param taskid
     * @return arraylist<integer> of all subtaskids for a task
     */
    public static ArrayList<Integer> getSubTaskidsforTask(int taskid){
        ArrayList<Integer> subtaskids = new ArrayList<Integer>();
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select subtaskid from Subtasks where listid="+taskid+";");
            while(rs.next()){
                //TASKS ADDED HERE. RIGHT NOW I ONLY ADD NAME TO ARRAYLIST. THIS CAN BE CHANGED.
                int id = rs.getInt("subtaskid");
                subtaskids.add(id);
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return subtaskids;
    }

    /**
     * 
     * @param username
     * @return
     */
    public static int getUseridByusername(String username){
        int userID = -1;
        Connection connection = connectDB();
  
        try{
          Statement statement = connection.createStatement();
          ResultSet rs = statement.executeQuery("select userid from users where username = " + username + ";");
  
          rs.next();
          userID = rs.getInt("userid");
          rs.close();
          statement.close();
  
        }catch(SQLException e){
            throw new RuntimeException("Problem with database", e);
        }
        closeDB(connection);
        return userID;
    }

    /***
     * Changes the group of user userid to group groupid
     * @param sprintarchiveid id of sprint archive
     */
    public static String getSprintArchiveNameByID(int sprintarchiveid){
        String name = "";
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select name from SprintArchive where sprintarchiveid = " + sprintarchiveid + ";");

            rs.next();
            name = rs.getString("name");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return name;
    }

    /***
     * Gets all the task id's in one user's sprint in their archive
     * @param userid
     * @param sprintid
     * @return arraylist integer of taskids
     */
    public static ArrayList<Integer> getTaskIdsInSprintArchive(int userid, int sprintid){
        ArrayList<Integer> taskids = new ArrayList<Integer>();
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select taskid from Tasks where userid = " + userid + " and sprintarchiveid = " + sprintid + ";");
            while(rs.next()){
                int taskid = rs.getInt("taskid");
                taskids.add(taskid);
            }
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return taskids;
    }

    /***
     * Returns the users current sprint name by their userid
     * @param userid
     * @return String name of current sprint
     */
    public static String getCurrentSprintNameByUserID(int userid){
        String sprintName = "";
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select currentSprintName from users where userid = " + userid + ";");

            rs.next();
            sprintName = rs.getString("currentSprintName");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return sprintName;
    }

    /***
     * Returns the group name given the group ID
     * @param groupID
     * @return String name of current sprint
     */
    public static String getGroupNameByID(int groupID){
        String groupName = "";
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select gname from team where groupsid = " + groupID + ";");

            rs.next();
            groupName = rs.getString("gname");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return groupName;
    }

    /***
     * Returns the group ID a given student is in
     * @param studentID id of student
     * @return id of the group the student is in
     */
    public static int getGroupIDbyStudentID(int studentID){
        int groupID = -1;
        System.out.println(studentID);
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("select groupid from users where userid = " + studentID + ";");

            rs.next();
            groupID = rs.getInt("groupid");
            rs.close();
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
        return groupID;
    }

//------------------------------ DO SOMETHING -------------------------------

    /***
     * Deletes all users in group groupid
     * @param groupid
     */
    public static void deleteGroupById(int groupid){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from users where groupid = "+groupid+";");
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);

        connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from team where groupsid = "+groupid+";");
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Deletes student by userid userid
     * @param userid
     */
    public static void deleteStudentById(int userid){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from users where userid = "+userid+";");
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Deletes a task by it's taskid
     * @param taskid
     */
    public static void deleteTaskById(int taskid){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from Tasks where taskid = "+taskid+";");
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Deletes a subtask by it's subtaskid
     * @param subtaskid
     */
    public static void deleteSubtaskById(int subtaskid){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("delete from Subtasks where subtaskid = "+subtaskid+";");
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Adds a new user to the user database with the given details
     * @param role role of the user 0 if professor, 1 if student
     * @param name name of the user
     * @param username username of the user
     * @param currentSprintName sprint name of the user
     * @param password password of the user
     * @param groupid groupID of the user
     */
    public static void makeUser(int role, String name, String username, String currentSprintName, String password, int groupid){
      Connection connection = connectDB();
      try{
          Statement statement = connection.createStatement();
          statement.executeUpdate("insert into users(role, name, username, password, currentSprintName, groupid) values ('"+role+"', '"+name+"', '"+username+"', '"+password+"', '"+currentSprintName+"', +"+groupid+");");
          statement.close();
      }catch(SQLException e){
          throw new RuntimeException("Problem with database", e);
      }
      closeDB(connection);
    }

    /***
     * Creates a group with groupname groupname
     * @param groupName
     * @return groupid
     */
    public static int makeGroup(String groupName){
        int groupid = 0;
        Connection connection = connectDB();
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate("insert into team(gname) values ('"+groupName+"');");
            ResultSet rs = statement.executeQuery("select groupsid from team where gname = '"+groupName+"';");
            rs.next();
            groupid = rs.getInt("groupsid");
            statement.close();

        }catch(SQLException e){
            throw new RuntimeException("Problem with database", e);
        }
        closeDB(connection);
        return groupid;
    }

    /***
     * Makes a new task and adds to database with given parameters
     * @param userid
     * @param name
     * @param backlog
     * @param sprint
     * @param sprintArchiveID
     */
    public static int makeTask(int userid, String name, boolean backlog, boolean sprint, int sprintArchiveID){
        Connection connection = connectDB();
        int taskid = 0;
        try{
            Statement statement = connection.createStatement();
            String sql = "insert into Tasks(userid, name, backlog, sprint, sprintarchiveid) values("+userid+", '"+name+"', "+backlog+", "+sprint+", "+sprintArchiveID+");";
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                taskid = resultSet.getInt(1);
                System.out.println("Generated primary key: " + taskid);
            }

            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem with database", e);
        }
        closeDB(connection);
        return taskid;
    }

    /***
     * Makes a new subtask and adds to database with given parameters
     * @param taskid
     * @param name
     * @param checked
     * @return
     */
    public static int makeSubtask(int taskid, String name, boolean checked){
        int subtaskID = -1;
        Connection connection = connectDB();
        try{
            Statement statement = connection.createStatement();
            String sql = "insert into Subtasks(listid, name, checked) values("+taskid+", '"+name+"',"+checked+");";
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                subtaskID = resultSet.getInt(1);
                System.out.println("Generated primary key: " + subtaskID);
            }
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem with database", e);
        }
        closeDB(connection);

        return subtaskID;
    }

    /***
     * Changes the group of user userid to group groupid
     * @param userid
     * @param groupid
     */
    public static void changeGroup(int userid, int groupid){
        Connection connection = connectDB();
        try{
            Statement statement = connection.createStatement();
            statement.executeUpdate("update users set groupid="+groupid+" where userid="+userid+";");
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem with database", e);
        }
        closeDB(connection);
    }

    /***
     * Change task name
     * @param taskID id of the task in the DB
     * @param newName new name of the task
     */
    public static void changeTaskName(int taskID, String newName){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            int rs = statement.executeUpdate("UPDATE Tasks SET name = '" + newName + "' WHERE taskid = " + taskID + ";");
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Change subtask name
     * @param taskID id of the subtask in the DB
     * @param newName new name of the subtask
     */
    public static void changeSubtaskName(int taskID, String newName){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            int rs = statement.executeUpdate("UPDATE Subtasks SET name = '" + newName + "' WHERE subtaskid = " + taskID + ";");
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Sets a task to the sprint
     * @param taskID id of the task in the DB
     */
    public static void moveTaskToSprint(int taskID){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            int rs1 = statement.executeUpdate("UPDATE Tasks SET backlog = 0 WHERE taskid = " + taskID + ";");
            int rs2 = statement.executeUpdate("UPDATE Tasks SET sprint = 1 WHERE taskid = " + taskID + ";");
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Change subtask name
     * @param subTaskID id of the subtask in the DB
     * @param value 0 if unchecked and 1 if checked
     */
    public static void toggleSetSubtaskChecked(int subTaskID, int value){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            int rs = statement.executeUpdate("UPDATE Subtasks SET checked = '" + value + "' WHERE subtaskid = " + subTaskID + ";");
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /**
     * Changes the name of a users sprint
     * @param userID ID of user
     * @param newName new name of sprint
     */
    public static void changeSprintName(int userID, String newName) {
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            int rs = statement.executeUpdate("UPDATE users SET currentSprintName = '" + newName + "' WHERE userid = " + userID + ";");
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

    /***
     * Archives a sprint and adds to database with given parameters
     * @param userID ID of user for new arhive
     * @param name name of sprint
     * @param reflection reflection of sprint
     * @return
     */
    public static int archiveSprint(int userID, String name, String reflection){
        int id = -1;
        Connection connection = connectDB();
        try{
            Statement statement = connection.createStatement();
            String sql = "insert into SprintArchive(userid, name, reflection) values("+userID+", '"+name+"','"+reflection+"');";
            statement.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
            statement.close();
        }catch(SQLException e){
            throw new RuntimeException("Problem with database", e);
        }
        closeDB(connection);

        moveSprintTasksToArchive(userID, id);

        return id;
    }

    /***
     * Moves all tasks in the sprint of a user to the given sprint archive
     * @param userID id of the subtask in the DB
     * @param sprintArchiveID 0 if unchecked and 1 if checked
     */
    public static void moveSprintTasksToArchive(int userID, int sprintArchiveID){
        Connection connection = connectDB();
        try {
            Statement statement = connection.createStatement();
            System.out.println("UPDATE Tasks SET sprint = 0, sprintarhiveid = " + sprintArchiveID + " WHERE userid = " + userID + ", sprint = 1;");
            int rs = statement.executeUpdate("UPDATE Tasks SET sprint = 0, sprintarchiveid = " + sprintArchiveID + " WHERE userid = " + userID + " AND sprint = 1;");
            statement.close();

        } catch (SQLException e) {
            throw new RuntimeException("Problem querying database", e);
        }
        closeDB(connection);
    }

}
