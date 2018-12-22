package com.email.email.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class NaTaxJob {
    private static final String EMAIL_SUBJECT_PREFIX = "ICSF-BIZREG2IRD-";
    private static String SAVE_ATTACH_PATH = "D:\\csvfiles\\getEmails\\"; // attach save path
    private static Logger logger = LoggerFactory.getLogger(NaTaxJob.class);
    @Value("${ird.email.toaddress")
    private String emailToAddress;
    @Value("${ird.email.host}")
    private String emailhost;
    @Value("${ird.email.username}")
    private String emailusername;
    @Value("${ird.email.password}")
    private String emailpassword;
    @Value("${ird.email.port}")
    private String emailport;

    @Scheduled(cron = "${ird.getemail.cron}")
    public void getEmail() throws Exception {
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        Class factoryClass = loader.loadClass("javax.net.SSLSocketFactory");
//        System.out.println(factoryClass.getName());

        String host = emailhost;
        String username = emailusername;
        String password = emailpassword;
        String port = emailport;
        //String host = "pop.qq.com";
        //String username = "413619412@qq.com";
        //String password = "wwmssadvifjjcaaj";
        //String port="995";
        System.out.println(host + username + password + port);
        Properties p = new Properties();
        p.setProperty("mail.pop3.host", host);
        p.setProperty("mail.pop3.port", port);
        // SSL
        p.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.setProperty("mail.pop3.socketFactory.fallback", "true");
        p.setProperty("mail.pop3.socketFactory.port", port);

        Session session = Session.getInstance(p);

        URLName url = new URLName("pop3", host, 995, null, username, password);
        //Store store = session.getStore("pop3");
        //store.connect(host, username, password);
        Store store = session.getStore(url);
        store.connect();

        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_WRITE);
        Message message[] = folder.getMessages();
        System.out.println("email count:　" + message.length);
        NaEmailReader re = null;

        for (int i = 0; i < message.length; i++) {
            re = new NaEmailReader((MimeMessage) message[i]);
            System.out.println("email　" + i + "　subject:　" + re.getSubject());
            System.out.println("email　" + i + "　send data:　" + re.getSentDate());
            System.out.println("email　" + i + "　need reply:　" + re.getReplySign());
            System.out.println("email　" + i + "　is new:　" + re.isNew());
            System.out.println("email　" + i + "　has attach:　" + re.isContainAttach((Part) message[i]));
            System.out.println("email　" + i + "　send address:　" + re.getFrom());
            System.out.println("email　" + i + "　receive address:　" + re.getMailAddress("to"));
            System.out.println("email　" + i + "　copy to:　" + re.getMailAddress("cc"));
            System.out.println("email　" + i + "　bcc to:　" + re.getMailAddress("bcc"));
            re.setDateFormat("yyMMdd　HH:mm");
            System.out.println("email　" + i + "　send data:　" + re.getSentDate());
            System.out.println("email　" + i + "　emailID:　" + re.getMessageId());
            re.getMailContent((Part) message[i]);
            System.out.println("email　" + i + " body:　\r\n" + re.getBodyText());
            File file = new File(SAVE_ATTACH_PATH);
            if (!file.exists()) {
                file.mkdirs();
            }
            re.setAttachPath(SAVE_ATTACH_PATH);
            re.saveAttachMent((Part) message[i]);
        }
        store.close();
    }


    private static String getSubject() {
        SimpleDateFormat format = new SimpleDateFormat(
                "yyyyMMddHH", Locale.US);
        return EMAIL_SUBJECT_PREFIX + format.format(new Date());
    }

    private static String getFileFullPath(String basePath, String subject) {
        return basePath + subject + ".csv";
    }

    /**
     * get all csvfile path from base File
     *
     * @param folder
     */
    public static List<String> getAllCsvfilePath(String folder) {
        File file = new File(folder);
        List<String> list = new ArrayList<>();
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (!file2.isDirectory()) {
                    list.add(file2.getAbsolutePath());
                }
            }
        }
        return list;
    }

    public static void main(String[] args) throws Exception {

    }
}
