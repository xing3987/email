package com.email.email.email2;

public class testController {
    public static void main(String[] args) {
        //MessageParser.parse(SimpleMailReceiver.fetchInbox(HostType.NETEASE.getProperties(),
        //        AuthenticatorGenerator.getAuthenticator("chenxing@859414.hzqiye.ntesmail.com", "password$1")));
        MessageParser.parse(SimpleMailReceiver.fetchInbox(HostType.TENCENT.getProperties(),
                AuthenticatorGenerator.getAuthenticator("413619412@qq.com", "wwmssadvifjjcaaj")));
        //MessageParser.parse(SimpleMailReceiver.fetchInbox(HostType.GMAIL.getProperties(),
        //        AuthenticatorGenerator.getAuthenticator("chenxing@859414.hzqiye.ntesmail.com", "password$1")));
    }
}
