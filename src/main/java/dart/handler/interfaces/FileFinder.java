/* Interface for getting files */
package dart.handler.interfaces;

import java.io.File;
import java.util.ArrayList;

public interface FileFinder {

    default void getFiles(ArrayList<File> fileList, File workingDir) {
        for (File file : workingDir.listFiles()) {
            if (file.isFile()) {
                fileList.add(file);
            } else if (file.isDirectory()) {
                getFiles(fileList, file);
            }
        }
    }

}