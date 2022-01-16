package model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;

public class DatabaseConnector {

    private Connection conn;

    /**
     * Opens the Database connection and returns an connector object
     */
    public DatabaseConnector() {
        try {
            this.conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/doit_markovchainserializeddb?user=root&password=");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     *
     * @param k Korpus to save
     * @return true if the korpus was successfully saved, false if an error occurs
     */
    public boolean saveKorpus(Korpus k) {
        try {
            PreparedStatement saveCstmt = conn.prepareStatement("INSERT INTO tblkorpus (k_Titel, k_Data) values ( ?, ?);");
            saveCstmt.setString(1, k.getTitle());
            saveCstmt.setObject(2, k);
            saveCstmt.executeUpdate();
            saveCstmt.close();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    /**
     * @param name Title of the Korpus
     * @return Returns true if no duplicates where found
     */
    public boolean checkForDuplicate(String name, String type) {
        boolean hasNoDuplicates = false;
        try {
            PreparedStatement checkDuplicatesStmt;
            switch (type) {
                case "chain":
                    checkDuplicatesStmt = conn.prepareStatement("SELECT COUNT(c_Name) AS Duplicates FROM tblChain WHERE c_Name = ?;");
                    break;
                case "korpus":
                    checkDuplicatesStmt = conn.prepareStatement("SELECT COUNT(k_Titel) AS Duplicates FROM tblKorpus WHERE k_Titel = ?;");
                    break;
                case "text":
                    checkDuplicatesStmt = conn.prepareStatement("SELECT COUNT(t_Titel) AS Duplicates FROM tblText WHERE t_Titel = ?;");
                    break;
                default:
                    return false;
            }
            checkDuplicatesStmt.setString(1, name);
            ResultSet rs = checkDuplicatesStmt.executeQuery();
            int results = 1;
            while (rs.next()) {
                results = rs.getInt("Duplicates");
            }
            if (results == 0) {
                hasNoDuplicates = true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return hasNoDuplicates;
    }

    /**
     *
     * @param title Title of the Korpus to load
     * @return a Korpus object if it was sucessfully loaded, returns null if an error occured
     */
    public Korpus loadKorpus(String title){
        try{
            PreparedStatement loadChainStmt = conn.prepareStatement("SELECT k_Data FROM tblKorpus WHERE k_Titel = ?;");
            loadChainStmt.setString(1, title);
            ResultSet rs = loadChainStmt.executeQuery();
            rs.next();
            byte[] buffer = rs.getBytes("k_Data");
            ObjectInputStream objectIn;
            if (buffer != null){
                objectIn = new ObjectInputStream(new ByteArrayInputStream(buffer));
                return (Korpus) objectIn.readObject();
            }else{
                return null;
            }

        }catch (SQLException | IOException | ClassNotFoundException throwables){
            throwables.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param title Title of the korpus to check
     * @return returns true if a chain exists that depends on this korpus
     */
    public boolean checkIfChainExistsThatDependsOnKorpus(String title){
        try{
            PreparedStatement checkChainsStmt = conn.prepareStatement("SELECT COUNT(c_Key) AS Duplicates FROM tblchain INNER JOIN tblKorpus ON tblKorpus.k_Key = tblchain.k_Key WHERE k_Titel = ?;");
            checkChainsStmt.setString(1, title);
            ResultSet rs = checkChainsStmt.executeQuery();
            rs.next();
            int results = rs.getInt("Duplicates");
            return results > 0;
        }catch (SQLException throwables){
            throwables.printStackTrace();
            return true;
        }

    }

    /**
     *
     * @param title Title of the Korpus to delete from the Database
     * @return returns true if the korpus was sucessfully deleted, false if an error occured
     */
    public boolean deleteKorpus(String title) {
        try {
            PreparedStatement deleteKStmt = conn.prepareStatement("DELETE FROM tblkorpus WHERE k_Titel=?;");
            deleteKStmt.setString(1, title);
            deleteKStmt.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param name name of the chain to delete
     * @return returns true if the chain was successfully deleted, false if an error occured
     */
    public boolean deleteChain(String name){
        try {
            PreparedStatement deleteCStmt = conn.prepareStatement("DELETE FROM tblChain WHERE c_Name =?;");
            deleteCStmt.setString(1, name);
            deleteCStmt.executeUpdate();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    /**
     * closes the database connection
     */
    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     *
     * @param c MarkovChain to save
     * @return true if save was sucessful, false if not
     */
    public boolean saveChain(MarkovChain c) {
            try{
                    int k_Key = 0;
                    PreparedStatement getkKeyStmt = conn.prepareStatement("SELECT k_Key FROM tblKorpus WHERE k_Titel = ?");
                    getkKeyStmt.setString(1, c.getCorpusName());
                    ResultSet getkKeyRS = getkKeyStmt.executeQuery();
                    while (getkKeyRS.next()) {
                        k_Key = getkKeyRS.getInt("k_Key");
                    }
                    PreparedStatement saveChainStmt = conn.prepareStatement("INSERT INTO tblChain (k_Key, c_Name, c_Data) VALUES (?,?,?)");
                    saveChainStmt.setInt(1, k_Key);
                    saveChainStmt.setString(2, c.getName());
                    saveChainStmt.setObject(3, c);
                    saveChainStmt.execute();
                    return true;
            }catch (SQLException throwables){
                throwables.printStackTrace();
                return false;
            }

    }

    /**
     *
     * @param name Name of the Chain to load
     * @return returns a MarkovChain object if it was successfully loaded, returns null if an error occurred
     */
    public MarkovChain loadChain(String name){
       try{
           PreparedStatement loadChainStmt = conn.prepareStatement("SELECT c_Data FROM tblChain WHERE c_Name = ?;");
           loadChainStmt.setString(1, name);
           ResultSet rs = loadChainStmt.executeQuery();
           rs.next();
           byte[] buffer = rs.getBytes("c_Data");
           ObjectInputStream objectIn;
           if (buffer != null){
               objectIn = new ObjectInputStream(new ByteArrayInputStream(buffer));
               return (MarkovChain) objectIn.readObject();
           }else{
               return null;
           }
       }catch (SQLException | IOException | ClassNotFoundException throwables){
           throwables.printStackTrace();
           return null;
       }

    }

    /**
     *
     * @param text The text object to save
     * @return returns true if the text was successfully saved, false if not
     */
    public boolean saveText(Text text){
            try{
                PreparedStatement saveTextStmt = conn.prepareStatement("INSERT INTO tbltext (t_Titel, t_Text) VALUES (?, ?);");
                saveTextStmt.setString(1, text.getTitle());
                saveTextStmt.setString(2, text.getText());
                saveTextStmt.execute();
            }catch(SQLException throwables){
                throwables.printStackTrace();
                return false;
            }
            return true;
    }

    /**
     *
     * @param title The title of the text to load
     * @return returns a Text object if it was successfully loaded or null if an error occurred
     */
    public Text loadText(String title){
        try{
            PreparedStatement loadTextStmt = conn.prepareStatement("SELECT * FROM tblText WHERE t_Titel = ?;");
            loadTextStmt.setString(1, title);
            ResultSet rs = loadTextStmt.executeQuery();
            rs.next();
            String text = rs.getString("t_Text");
            return new Text(title.toUpperCase(), text);
        }catch (SQLException throwables){
            throwables.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param title title of the text to delete
     * @return  returns true if deleting the text was successful, false if an error occurred
     */
    public boolean deleteText(String title){
        try {
            PreparedStatement deleteTextStmt = conn.prepareStatement("DELETE FROM tblText WHERE t_Titel = ?");
            deleteTextStmt.setString(1, title);
            deleteTextStmt.execute();
            return true;
        }catch (SQLException throwables){
            throwables.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @return returns a String containing every available Korpus in the format Title/nTitle/n
     */
    public String listAvailableKorpusses(){
        StringBuilder results = new StringBuilder();
        try{
            PreparedStatement listKorpussesStmt = conn.prepareStatement("SELECT k_Titel FROM tblkorpus;");
            ResultSet rs = listKorpussesStmt.executeQuery();
            while(rs.next()){
                results.append(rs.getString("k_Titel")).append("\n");
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return results.toString();
    }
    /**
     *
     * @return returns a String containing every available Chain in the format Name/nName/n
     */
    public String listAvailableChains(){
        StringBuilder results = new StringBuilder();
        try{
            PreparedStatement listChainsStmt = conn.prepareStatement("SELECT c_Name FROM tblchain;");
            ResultSet rs = listChainsStmt.executeQuery();
            while(rs.next()){
                results.append(rs.getString("c_Name")).append("\n");
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return results.toString();
    }
    /**
     *
     * @return returns a String containing every available Text in the format Title/nTitle/n
     */
    public String listAvailableTexts(){
        StringBuilder results = new StringBuilder();
        try{
            PreparedStatement listTextsStmt = conn.prepareStatement("SELECT t_Titel FROM tblText;");
            ResultSet rs = listTextsStmt.executeQuery();
            while(rs.next()){
                results.append(rs.getString("t_Titel")).append("\n");
            }
        }catch (SQLException throwables){
            throwables.printStackTrace();
        }
        return results.toString();
    }

}
