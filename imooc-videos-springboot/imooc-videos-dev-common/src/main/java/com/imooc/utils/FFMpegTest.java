package com.imooc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: mate_J
 * @Date: 2018/12/17 10:25
 * @Version 1.0
 */
public class FFMpegTest {
    private String ffmpegEXE;

    public FFMpegTest(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,String videoOutputPath) throws IOException {

        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutputPath);

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();

        InputStream inputStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ((line = br.readLine()) != null){
            System.out.println("------");
        }
        if(br != null){
            br.close();
        }
        if(inputStreamReader != null){
            inputStreamReader.close();
        }
        if(inputStream != null){
            inputStream.close();
        }
    }
    public static void main(String[] args) {
        FFMpegTest ffMpegTest = new FFMpegTest("F:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("F:\\workspace\\大雪.mp4","F:\\workspace\\snow2.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
