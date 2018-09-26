package helpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;

public class ConfigHelper {

    private ConfigHelper(){}

    public static synchronized File getConfigFile(String filePath) throws FileNotFoundException {
        File file;
        ClassLoader classLoader;
        URL url;

        if (filePath == null || filePath.equals("")){
            throw new NullPointerException("file path is null");
        }

        classLoader = Thread.currentThread().getContextClassLoader();
        url = classLoader.getResource(filePath);

        if(url == null){
            System.err.println("Can't get url of config db file!");
            return null;
        }

        file = new File(url.getPath());
        if (!file.exists() || !file.isFile()){
            throw new FileNotFoundException();
        }

        return file;
    }
}
