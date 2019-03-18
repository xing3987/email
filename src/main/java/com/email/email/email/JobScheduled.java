package com.email.email.email;

import com.email.email.email2.AuthenticatorGenerator;
import com.email.email.email2.HostType;
import com.email.email.email2.SimpleMailReceiver;
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
public class JobScheduled {
    private static final String EMAIL_SUBJECT_PREFIX = "xxx-";
    private static String SAVE_ATTACH_PATH = "D:\\csvfiles\\getEmails\\"; // attach save path
    private static Logger logger = LoggerFactory.getLogger(JobScheduled.class);
    @Value("${email.toaddress")
    private String emailToAddress;
    @Value("${email.host}")
    private String emailhost;
    @Value("${email.username}")
    private String emailusername;
    @Value("${email.password}")
    private String emailpassword;
    @Value("${email.port}")
    private String emailport;

    @Scheduled(cron = "${ird.getemail.cron}")
    public void getEmail() throws Exception {
        //String host = emailhost;
        //String username = emailusername;
        //String password = emailpassword;
        //String port = emailport;
        //String host = "pop.qq.com";
        //String username = "413619412@qq.com";
        //String password = "wwmssadvifjjcaaj";
        //String port="995";
        String type="tencent";
        String username = "413619412@qq.com";
        String password = "wwmssadvifjjcaaj";

        //connect to email inbox
        Authenticator authenticator=AuthenticatorGenerator.getAuthenticator(username,password);
        Message message[] =SimpleMailReceiver.fetchInbox(HostType.TENCENT.getProperties(),authenticator);
        EmailReader re = null;
        for (int i = 0; i < message.length; i++) {
            re = new EmailReader((MimeMessage) message[i]);
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
