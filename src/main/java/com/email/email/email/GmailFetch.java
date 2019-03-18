package com.email.email.email;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

/**
 * 用于收取Gmail邮件
 *
 */
public class GmailFetch {

    public static void main(String argv[]) throws Exception {
        String username="***";
        String password="**";

        Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

        // Get a Properties object
        Properties props = System.getProperties();
        props.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.pop3.socketFactory.fallback", "false");
        props.setProperty("mail.pop3.port", "995");
        props.setProperty("mail.pop3.socketFactory.port", "995");

        // 以下步骤跟一般的JavaMail操作相同
        Session session = Session.getDefaultInstance(props, null);

        // 请将红色部分对应替换成你的邮箱帐号和密码
        URLName urln = new URLName("pop3", "pop.gmail.com", 995, null,
                username, password);
        Store store = session.getStore(urln);


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
