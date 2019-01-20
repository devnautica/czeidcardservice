package com.devnautica.czeidcardservice.utils;

import java.io.*;

public class backup_client {
    public static void main(String[] args) throws IOException {
        String s = FileEncoder.fileToString("/home/wenza/projects/devnautica/czeidcardverifier/short.crt");
        FileEncoder.base64StringToFile(s,"/home/wenza/ahoj.crt");
        System.out.println(s);
    }
}