package com.email.email.Helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DeleteEmptyFile {
    List<File> list = new ArrayList<File>();

    // 得到某一目录下的所有文件夹
    public List<File> visitAll(File root) {
        File[] dirs = root.listFiles();
        if (dirs != null) {
            for (int i = 0; i < dirs.length; i++) {
                if (dirs[i].isDirectory()) {
                    System.out.println("name:" + dirs[i].getPath());
                    list.add(dirs[i]);
                }
                visitAll(dirs[i]);
            }
        }
        return list;
    }

    /**
     * 删除空的文件夹
     *
     * @param list
     */
    public void removeNullFile(List<File> list) {
        for (int i = 0; i < list.size(); i++) {
            File temp = list.get(i);
            // 是目录且为空
            if (temp.isDirectory() && temp.listFiles().length <= 0) {
                temp.delete();
            }
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DeleteEmptyFile m = new DeleteEmptyFile();
        List<File> list = m.visitAll(new File("g:/迅雷下载"));
        System.out.println(list.size());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).getPath());
        }
        m.removeNullFile(list);
        System.out.println("ok");
    }

}
