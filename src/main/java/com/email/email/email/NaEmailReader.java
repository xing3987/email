package com.email.email.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class NaEmailReader {
    private MimeMessage mimeMessage = null;
    private static String saveAttachPath = "D:\\"; // attach save path
    private StringBuffer bodyText = new StringBuffer(); // email body
    private String dateFormat = "yy-MM-dd HH:mm";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public NaEmailReader() { }
 
    public NaEmailReader(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }
 
    public void setMimeMessage(MimeMessage mimeMessage) {
        this.mimeMessage = mimeMessage;
    }

    /**
     * Get the address and name of the sender
     * @return
     * @throws Exception
     */
    public String getFrom() throws Exception {
        InternetAddress address[] = (InternetAddress[]) mimeMessage.getFrom();
        String from = address[0].getAddress();
        if (from == null) {
            from = "";
           logger.debug("can not get sender.");
        }
        String personal = address[0].getPersonal();
 
        if (personal == null) {
            personal = "";
            logger.debug("can not get sender name.");
        }
 
        String fromAddr = null;
        if (personal != null || from != null) {
            fromAddr = personal + "<" + from + ">";
            logger.debug("sender：" + fromAddr);
        } else {
            logger.debug("can not get sender details.");
        }
        return fromAddr;
    }

    /**
     *  Get the recipient of the email
     *  "to"----email to　"cc"---email copy to　"bcc"---BCC address 　
     */
    public String getMailAddress(String type) throws Exception {
        String mailAddr = "";
        String addType = type.toUpperCase();
 
        InternetAddress[] address = null;
        if (addType.equals("TO") || addType.equals("CC")
                || addType.equals("BCC")) {
 
            if (addType.equals("TO")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.TO);
            } else if (addType.equals("CC")) {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.CC);
            } else {
                address = (InternetAddress[]) mimeMessage
                        .getRecipients(Message.RecipientType.BCC);
            }
 
            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String emailAddr = address[i].getAddress();
                    if (emailAddr == null) {
                        emailAddr = "";
                    } else {
                        logger.debug("from emailAddr: " + emailAddr);
                        emailAddr = MimeUtility.decodeText(emailAddr);
                        logger.debug("to emailAddr: " + emailAddr);
                    }
                    String personal = address[i].getPersonal();
                    if (personal == null) {
                        personal = "";
                    } else {
                        logger.debug("from personal: " + personal);
                        personal = MimeUtility.decodeText(personal);
                        logger.debug("to personal: " + personal);
                    }
                    String compositeto = personal + "<" + emailAddr + ">";
                    logger.debug("email address detail ：" + compositeto);
                    mailAddr += "," + compositeto;
                }
                mailAddr = mailAddr.substring(1);
            }
        } else {
            throw new Exception("email type error!");
        }
        return mailAddr;
    }

    /**
     * get email subject
     * @return
     * @throws MessagingException
     */
    public String getSubject() throws MessagingException {
        String subject = "";
        try {
            logger.debug("from subject：" + mimeMessage.getSubject());
            subject = MimeUtility.decodeText(mimeMessage.getSubject());
            logger.debug("to subject: " + mimeMessage.getSubject());
            if (subject == null) {
                subject = "";
            }
        } catch (Exception exce) {
            exce.printStackTrace();
        }
        return subject;
    }
 
    /**
     * get email send data
     */
    public String getSentDate() throws Exception {
        Date sentDate = mimeMessage.getSentDate();
        logger.debug("send data: " + dateFormat);
        SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        String strSentDate = format.format(sentDate);
        logger.debug("send data: " + strSentDate);
        return strSentDate;
    }

    public String getBodyText() {
        return bodyText.toString();
    }
         
    public void getMailContent(Part part) throws Exception {
 
        String contentType = part.getContentType();
        // email MimeType
        logger.debug("email MimeType: " + contentType);
 
        int nameIndex = contentType.indexOf("name");
 
        boolean conName = false;
 
        if (nameIndex != -1) {
            conName = true;
        }
 
        logger.debug("email content:　" + contentType);
 
        if (part.isMimeType("text/plain") && conName == false) {
            // text/plain
            bodyText.append((String) part.getContent());
        } else if (part.isMimeType("text/html") && conName == false) {
            // text/html
            bodyText.append((String) part.getContent());
        } else if (part.isMimeType("multipart/*")) {
            // multipart/*
            Multipart multipart = (Multipart) part.getContent();
            int counts = multipart.getCount();
            for (int i = 0; i < counts; i++) {
                getMailContent(multipart.getBodyPart(i));
            }
        } else if (part.isMimeType("message/rfc822")) {
            // message/rfc822
            getMailContent((Part) part.getContent());
        } else {
 
        }
    }
 
    /**
     * is the email have to reply
     */
    public boolean getReplySign() throws MessagingException {
 
        boolean replySign = false;
 
        String needReply[] = mimeMessage
                .getHeader("Disposition-Notification-To");
 
        if (needReply != null) {
            replySign = true;
        }
        if (replySign) {
            logger.debug("email need reply");
        } else {
            logger.debug("email with no need for reply");
        }
        return replySign;
    }

    /**
     * get email message id
     * @return
     * @throws MessagingException
     */
    public String getMessageId() throws MessagingException {
        String messageID = mimeMessage.getMessageID();
        logger.debug("email ID: " + messageID);
        return messageID;
    }

    public boolean isNew() throws MessagingException {
        boolean isNew = false;
        Flags flags = ((Message) mimeMessage).getFlags();
        Flags.Flag[] flag = flags.getSystemFlags();
        logger.debug("flags length:　" + flag.length);
        for (int i = 0; i < flag.length; i++) {
            if (flag[i] == Flags.Flag.SEEN) {
                isNew = true;
                logger.debug("seen email...");
                // break;
            }
        }
        return isNew;
    }

    /**
     * @param part
     * @return
     * @throws Exception
     */
    public boolean isContainAttach(Part part) throws Exception {
        boolean attachFlag = false;
        // String contentType = part.getContentType();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mPart = mp.getBodyPart(i);
                String disposition = mPart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                                .equals(Part.INLINE))))
                    attachFlag = true;
                else if (mPart.isMimeType("multipart/*")) {
                    attachFlag = isContainAttach((Part) mPart);
                } else {
                    String conType = mPart.getContentType();
 
                    if (conType.toLowerCase().indexOf("application") != -1)
                        attachFlag = true;
                    if (conType.toLowerCase().indexOf("name") != -1)
                        attachFlag = true;
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachFlag = isContainAttach((Part) part.getContent());
        }
        return attachFlag;
    }

    /**
     * save AttachMent
      * @param part
     * @throws Exception
     */
    public void saveAttachMent(Part part) throws Exception {
        String fileName = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mPart = mp.getBodyPart(i);
                String disposition = mPart.getDisposition();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                                .equals(Part.INLINE)))) {
                    fileName = mPart.getFileName();
                    if (fileName.toLowerCase().indexOf("gb2312") != -1) {
                        fileName = MimeUtility.decodeText(fileName);
                    }
                    saveFile(fileName, mPart.getInputStream());
                } else if (mPart.isMimeType("multipart/*")) {
                    saveAttachMent(mPart);
                } else {
                    fileName = mPart.getFileName();
                    if ((fileName != null)
                            && (fileName.toLowerCase().indexOf("GB2312") != -1)) {
                        fileName = MimeUtility.decodeText(fileName);
                        saveFile(fileName, mPart.getInputStream());
                    }
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            saveAttachMent((Part) part.getContent());
        }
    }

    /**
     * set file path
     * @param attachPath
     */
    public void setAttachPath(String attachPath) {
        this.saveAttachPath = attachPath;
    }

    public void setDateFormat(String format) throws Exception {
        this.dateFormat = format;
    }

    /**
     * get file path
     * @return
     */
    public String getAttachPath() {
        return saveAttachPath;
    }

    /**
     * save file
     * @param fileName
     * @param in
     * @throws Exception
     */
    private void saveFile(String fileName, InputStream in) throws Exception {
        String osName = System.getProperty("os.name");
        String storeDir = getAttachPath();
        String separator = "";
        if (osName == null) {
            osName = "";
        }
        if (osName.toLowerCase().indexOf("win") != -1) {
            separator = "\\";
            if (storeDir == null || storeDir.equals(""))
                storeDir = "c:\\tmp";
        } else {
            separator = "/";
            storeDir = "/tmp";
        }
        File storeFile = new File(storeDir + separator + fileName);
        logger.debug("the store path of Attach:　" + storeFile.toString());

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
 
        try {
            bos = new BufferedOutputStream(new FileOutputStream(storeFile));
            bis = new BufferedInputStream(in);
            int c;
            while ((c = bis.read()) != -1) {
                bos.write(c);
                bos.flush();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new Exception("save file failed!");
        } finally {
            bos.close();
            bis.close();
        }
    }

    /**
     * test
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
        String host = "pop.qq.com";
        String username = "413619412@qq.com";
        String password = "wwmssadvifjjcaaj";

        Properties p = new Properties();
        p.setProperty("mail.pop3.host", "smtp.qq.com"); // 按需要更改
        p.setProperty("mail.pop3.port", "995");
        // SSL安全连接参数
        p.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        p.setProperty("mail.pop3.socketFactory.fallback", "false");
        p.setProperty("mail.pop3.socketFactory.port", "995");
        p.setProperty("mail.pop3.auth", "true");
        Session session = Session.getDefaultInstance(p, null);

        URLName url = new URLName("pop3", host, 995, null, username, password);
        //Store store = session.getStore("pop3");
        //store.connect(host, username, password);
        Store store=session.getStore(url);
        store.connect();
        Folder folder = store.getFolder("INBOX");
        folder.open(Folder.READ_ONLY);
        Message message[] = folder.getMessages();
        System.out.println("邮件数量:　" + message.length);
        NaEmailReader re = null;

        for (int i = 0; i < message.length; i++) {
            re = new  NaEmailReader((MimeMessage) message[i]);
            System.out.println("邮件　" + i + "　主题:　" + re.getSubject());
            System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
            System.out.println("邮件　" + i + "　是否需要回复:　" + re.getReplySign());
            System.out.println("邮件　" + i + "　是否已读:　" + re.isNew());
            System.out.println("邮件　" + i + "　是否包含附件:　"
                    + re.isContainAttach((Part) message[i]));
            System.out.println("邮件　" + i + "　发送人地址:　" + re.getFrom());
            System.out
                    .println("邮件　" + i + "　收信人地址:　" + re.getMailAddress("to"));
            System.out.println("邮件　" + i + "　抄送:　" + re.getMailAddress("cc"));
            System.out.println("邮件　" + i + "　暗抄:　" + re.getMailAddress("bcc"));
            re.setDateFormat("yy年MM月dd日　HH:mm");
            System.out.println("邮件　" + i + "　发送时间:　" + re.getSentDate());
            System.out.println("邮件　" + i + "　邮件ID:　" + re.getMessageId());
            re.getMailContent((Part) message[i]);
            System.out.println("邮件　" + i + "　正文内容:　\r\n" + re.getBodyText());
            re.setAttachPath("d:\\");
            re.saveAttachMent((Part) message[i]);
        }
        //NaTaxJob job=new NaTaxJob();
        //job.emaildetail();
    }
}
