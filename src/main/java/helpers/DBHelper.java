package helpers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        init();
    }

    private static void init(){
        File file = null;

        try {
            file = ConfigHelper.getConfigFile("dbconfig.xml");
        }catch(FileNotFoundException | NullPointerException ex){
            file = null;
        }

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = null;
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

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        System.out.println("subprotocol -> " + subprotocol);
        System.out.println("ip -> " + ip);
        System.out.println("port -> " + port);
        System.out.println("dbName -> " + dbName);
        System.out.println("userName -> " + userName);
        System.out.println("userPassword -> " + userPassword);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        connectionURI = "jdbc:" + subprotocol + "://" + ip + ":" + port + "/" + dbName;
        try {
            Connection connection = DriverManager.getConnection(connectionURI, userName, userPassword);
            System.out.println("connection -> " + connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //TODO: check db name and if the db name is null then create it
        //TODO: check tables in db. If tables has not exists then create it
    }

    public static Connection getConnection(){
        return null;
    }
}
