package com.email;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.Properties;

public class Gmailimap {
    public static void main(String argv[]) throws Exception {

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

// Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
//        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
//        props.setProperty("mail.pop3.socketFactory.fallback", "false");
//        props.setProperty("mail.pop3.port", "995");
//        props.setProperty("mail.pop3.socketFactory.port", "995");


        props.setProperty("mail.imaps.host", "imap.gmail.com");
        props.setProperty("mail.imaps.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.imaps.socketFactory.fallback", "false");
        //props.put("mail.imap.connectiontimeout", ConfigKeys.IMAP_CONNECTIONTIMEOUT);


        props.setProperty("mail.imaps.port", "993");
        props.setProperty("mail.imaps.socketFactory.port", "993");


//以下步骤跟一般的JavaMail操作相同
        Session session = Session.getDefaultInstance(props, null);

//        Store store = session.getStore("imaps");
//        try {
//            store.connect("imap.gmail.com", "chenxing3987@gmail.com", "password$1");
//        }catch (Exception e){
//            System.out.println(e.getMessage());
//        }
//请将红色部分对应替换成你的邮箱帐号和密码
//        URLName urlName = new URLName("imap://chenxing3987@gmail.com:password$1@imap.gmail.com");
//        Store store = session.getStore(urlName);
        URLName urln = new URLName("imaps", "imap.gmail.com", 993, null,
                "chenxing@ecquaria.com", "kuangyun398");
        System.out.println(urln);
        //Transport store =session.getTransport(urln);
        Store store = session.getStore(urln);

//        Session session = Session.getInstance(props,auth);
        session.setDebug(true);

        Folder inbox = null;
        try {
            store.connect();
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            FetchProfile profile = new FetchProfile();
            profile.add(FetchProfile.Item.ENVELOPE);
            Message[] messages = inbox.getMessages();
            inbox.fetch(messages, profile);
            System.out.println("收件箱的邮件数：" + messages.length);
            for (int i = 0; i < messages.length; i++) {
//邮件发送者
                String from = decodeText(messages[i].getFrom()[0].toString());
                InternetAddress ia = new InternetAddress(from);
                System.out.println("FROM:" + ia.getPersonal() + '(' + ia.getAddress() + ')');
//邮件标题
                System.out.println("TITLE:" + messages[i].getSubject());
//邮件大小
                System.out.println("SIZE:" + messages[i].getSize());
//邮件发送时间
                System.out.println("DATE:" + messages[i].getSentDate());
            }
        } finally {
            try {
                inbox.close(false);
            } catch (Exception e) {
            }
            try {
                store.close();
            } catch (Exception e) {
            }
        }
    }

    protected static String decodeText(String text)
            throws UnsupportedEncodingException {
        if (text == null)
            return null;
        if (text.startsWith("=?GB") || text.startsWith("=?gb"))
            text = MimeUtility.decodeText(text);
        else
            text = new String(text.getBytes("ISO8859_1"));
        return text;
    }
}
