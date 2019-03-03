package com.imooc;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @Author: mate_J
 * @Date: 2019/2/19 17:03
 * @Version 1.0
 */
public class IOTest {

    public static void main(String[] args) throws Exception {
        FileOutputStream fileOutputStream = new FileOutputStream("");
        FileInputStream fileInputStream = new FileInputStream("");
        byte[] buf = new byte[1024];
        int hasRead = 0;
        while ( (hasRead = fileInputStream.read(buf)) > 0){
            fileOutputStream.write(buf,0,hasRead);
        }
    }
}
