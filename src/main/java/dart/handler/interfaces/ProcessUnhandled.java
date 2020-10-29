/* Interface for processing unhandled files */

package dart.handler.interfaces;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public interface ProcessUnhandled {
    default void processUnhandledFile(File file) {
        String filePath = file.getAbsolutePath();
        String unhandledDir = filePath.replace("Обработанное", "Необработанное");
        try {
            FileUtils.copyFile(new File(filePath), new File(unhandledDir));
            FileUtils.deleteQuietly(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
