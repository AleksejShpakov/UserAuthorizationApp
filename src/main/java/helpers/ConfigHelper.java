package helpers;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class ConfigHelper {

    private ConfigHelper(){}

    public static synchronized File getConfigFile(String filePath) throws FileNotFoundException {
        File file = null;
        ClassLoader classLoader = null;
        URL url = null;

        if (filePath == null || filePath.equals("")){
            throw new NullPointerException("file path is null");
        }

        classLoader = Thread.currentThread().getContextClassLoader();
        url = classLoader.getResource(filePath);

        file = new File(url.getPath());
        if (!file.exists() || !file.isFile()){
            throw new FileNotFoundException();
        }

        return file;
    }
}
