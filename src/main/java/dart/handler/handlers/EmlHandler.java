package dart.handler.handlers;

import dart.handler.ControllerMain;
import dart.handler.interfaces.FileFinder;
import org.apache.commons.mail.util.MimeMessageParser;
import org.jsoup.Jsoup;

import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

public class EmlHandler implements FileFinder {
    private final File workingDir = ControllerMain.getEmlDir();

    public void handleEml() {
        ArrayList<File> emls = new ArrayList<>();
        getFiles(emls,workingDir);

        //Some defaults for eml handling
        Properties props = System.getProperties();
        props.put("mail.host", "smtp.dummydomain.com");
        props.put("mail.transport.protocol", "smtp");
        Session mailSession = Session.getDefaultInstance(props, null);

        //Counter for creating dirs
        int count = 1;

        for (File eml : emls) {
            //Creating saveDir and defining txt name
            String saveDir = getSaveDir(eml);
            String fileName = eml.getName();
            File outputDir = new File(saveDir + count);
            count++;
            String txtName = outputDir + File.separator + fileName.substring(0, fileName.length() - 3) + "txt";
            createSaveDir(outputDir);

            try (FileWriter writer = new FileWriter(txtName)) {
                InputStream source = new FileInputStream(eml);
                MimeMessage message = new MimeMessage(mailSession, source);
                MimeMessageParser messageParser = new MimeMessageParser(message);
                messageParser.parse();
                writeHeaders(writer, messageParser);
                String text = getText(messageParser);
                if (text != null) {
                    writer.write(text);
                }
                if (messageParser.hasAttachments()) {
                    getAttachments(messageParser, outputDir);
                }
            } catch (FileNotFoundException | MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

        //Method to create saveDir and sub dirs
    private void createSaveDir(File saveDir) {
        if (!saveDir.exists()) {
            try {
                Files.createDirectories(saveDir.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Method to get directory to save output file
    private String getSaveDir(File file) {
        String initialDir = file.getParent();
        String outputDir = ControllerMain.getHandledDir().getAbsolutePath();
        String splitRegex = Pattern.quote(System.getProperty("file.separator"));
        String[] folders = initialDir.split(splitRegex);
        return outputDir + File.separator + folders[folders.length - 3] + File.separator + folders[folders.length - 1] + File.separator;
    }

    //Method to write default headers to txt
    private void writeHeaders(FileWriter writer, MimeMessageParser parser) throws Exception {
        writer.write("From :" + parser.getFrom() + "\r\n");
        writer.write("To:" + parser.getTo().toString() + "\r\n");
        writer.write("Subject：" + parser.getSubject() + "\r\n");
        writer.write("Message:" + "\r\n" + "\r\n");
    }

    //Method to get text body from eml
    private String getText(MimeMessageParser messageParser) {
        if (messageParser.hasPlainContent()) {
            return messageParser.getPlainContent();
        } else if (messageParser.hasHtmlContent()) {
            return Jsoup.parse(messageParser.getHtmlContent()).text();
        }
        return null;
    }

    //Method to get attachments from eml
    private void getAttachments(MimeMessageParser messageParser, File saveDir) {
        for (DataSource dataSource : messageParser.getAttachmentList()) {
            try (InputStream is = dataSource.getInputStream()) {
                File save = new File(saveDir + File.separator + dataSource.getName());
                FileOutputStream fos = new FileOutputStream(save);
                byte[] buf = new byte[4096];
                int bytesRead;
                while ((bytesRead = is.read(buf)) != -1) {
                    fos.write(buf, 0, bytesRead);
                }
                fos.close();
                if (save.getName().endsWith("eml")) {
                    handleEml();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
