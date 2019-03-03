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
public class MergeVideoMp3 {
    private String ffmpegEXE;

    public MergeVideoMp3(String ffmpegEXE) {
        this.ffmpegEXE = ffmpegEXE;
    }

    public void convertor(String videoInputPath,
                          String mp3InputPath,Double seconds,String videoOutputPath) throws IOException {

        ////将原有视频的音频消除
        //ffmpeg.exe -i video.mp4 -vcodec copy -an video2.mp4
        ////再把另外的音频加入****这里输入的视频必须是已经把音频消音的
        //ffmpeg.exe -i 1.mp4 -i 1.mp3 -t 5 -y 1.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegEXE);
        command.add("-i");
        command.add(videoInputPath);
        command.add("-i");
        command.add(mp3InputPath);
        command.add("-t");
        command.add(String.valueOf(seconds));
        command.add("-y");
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
        MergeVideoMp3 ffMpegTest = new MergeVideoMp3("F:\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffMpegTest.convertor("F:\\workspace\\大雪.mp4","F:\\workspace\\bgm.mp3",11.0,"F:\\workspace\\snowjava2.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
