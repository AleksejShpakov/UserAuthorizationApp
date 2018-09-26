package helpers;

import constants.DBTables;
import entity.User;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DBHelper {
    private static String subprotocol = "";
    private static String ip = "";
    private static int port;
    private static String dbName = "";
    private static String userName = "";
    private static String userPassword = "";
    private static String connectionURI = "";

    private DBHelper(){}

    static{
        initDBConfig();
        loadDBDriver();
        if(dbName == null || dbName.equals("")){
            createDB();
        }
        createTables();
    }

    private static void initDBConfig(){
        File file;

        try {
            file = ConfigHelper.getConfigFile("dbconfig.xml");
        }catch(FileNotFoundException | NullPointerException ex){
            file = null;
        }

        if(file == null){
            System.err.println("Init DB failed!");
            return;
        }

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document;
            document = documentBuilder.parse(file);
            Element configurationNode = document.getDocumentElement();

            Node tmpNode = configurationNode.getElementsByTagName("connection").item(0);

            //<Get db settings>
            subprotocol = XMLHelper.getTagValue("subprotocol", tmpNode);
            if (subprotocol == null || subprotocol.equals("")){
                throw new NullPointerException("subprotocol in db config file is null");
            }

            ip = XMLHelper.getTagValue("ip", tmpNode);
            if (ip == null || ip.equals("")){
                throw new NullPointerException("ip in db config file is null");
            }

            String tmpString = XMLHelper.getTagValue("port", tmpNode);
            if (tmpString == null || tmpString.equals("")){
                throw new NullPointerException("port in db config file is null");
            }else{
                try {
                    port = Integer.parseInt(tmpString);
                }catch(NumberFormatException ex){
                    throw new IllegalArgumentException("port in db config file is not number");
                }
            }

            dbName = XMLHelper.getTagValue("name", tmpNode);
            //</Get db settings>

            //<Get user name and password>
            tmpNode = configurationNode.getElementsByTagName("user").item(0);
            userName = XMLHelper.getTagValue("name", tmpNode);
            if (userName == null || userName.equals("")){
                throw new NullPointerException("user name in db config file is null");
            }
            userPassword = XMLHelper.getTagValue("password", tmpNode);
            //</Get user name and password>

            connectionURI = "jdbc:" + subprotocol + "://" + ip + ":" + port + "/" + dbName;
        } catch (SAXException | ParserConfigurationException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadDBDriver(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void createDB(){
        Connection connection = null;
        Statement statement;
        File file = null;

        dbName = "user_authorization_app";

        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.executeUpdate("CREATE DATABASE " + dbName);
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if(connection != null){
                    connection.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            file = ConfigHelper.getConfigFile("dbconfig.xml");
        }catch(FileNotFoundException | NullPointerException ex){
            ex.printStackTrace();
        }

        if(file == null){
            System.err.println("Create DB failed! dbconfig.xml not found");
            return;
        }

        try{
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            Element configurationNode = document.getDocumentElement();

            Node tmpNode = configurationNode.getElementsByTagName("connection").item(0);
            XMLHelper.setTagValue("name", dbName, tmpNode);

            //<Save new settings to file>
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            Result result = new StreamResult(file);
            transformer.transform(domSource, result);
            //</Save new settings to file>
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }

        connectionURI += dbName;
    }

    private static void createTables(){
        Map<DBTables, Boolean> tableExistsMap = new HashMap<>();
        Connection connection = null;
        Statement statement;
        DatabaseMetaData databaseMetaData;
        ResultSet resultSet = null;
        DBTables dbTable;

        for(DBTables table : DBTables.values()){
            tableExistsMap.put(table, false);
        }

        try {
            connection = getConnection();
            databaseMetaData = connection.getMetaData();
            resultSet = databaseMetaData.getTables(null, null, null, new String[]{"TABLE"});
            while(resultSet.next()){

                try {
                    dbTable = DBTables.valueOf(resultSet.getString("TABLE_NAME").toUpperCase());
                }catch(IllegalArgumentException ex){
                    continue;
                }

                if(tableExistsMap.containsKey(dbTable)){
                    tableExistsMap.put(dbTable, true);
                }
            }

            statement = connection.createStatement();
            for(Map.Entry<DBTables, Boolean> table : tableExistsMap.entrySet()){
                if(!table.getValue()){
                    statement.executeUpdate(table.getKey().getCreateTableSQL());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if(resultSet != null){
                    resultSet.close();
                }

                if(connection != null){
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionURI, userName, userPassword);
    }

    public static int addUser (User user) throws SQLException{
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        int res = statement.executeUpdate("INSERT INTO " + DBTables.USERS.getTableName() +
                                            " (email, registration_date, password) " +
                                            "VALUES ('" + user.getMail() + "', " + user.getRegistrationDate().getTime() + ", '" + user.getPassword() + "')");

        connection.close();

        return res;
    }

    public static User getUser (String email) throws SQLException{
        User user = null;
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + DBTables.USERS + " WHERE email='" + email + "';");
        String userEmail = "";
        String userPassword = "";
        long userRegistrationDate = 0L;

        if(resultSet.next()){
            userEmail = resultSet.getString("email");
            userPassword = resultSet.getString("password");
            userRegistrationDate = resultSet.getBigDecimal("registration_date").longValue();
        }

        resultSet.close();
        connection.close();

        if(!userEmail.equals("") && !userPassword.equals("") && userRegistrationDate != 0L){
            return new User(userEmail, userPassword, userRegistrationDate);
        }

        return null;
    }

}
