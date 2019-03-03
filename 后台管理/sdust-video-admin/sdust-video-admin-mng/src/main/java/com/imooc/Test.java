package com.imooc;

import org.n3r.idworker.utils.JsonUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: mate_J
 * @Date: 2019/2/17 0:43
 * @Version 1.0
 */
public class Test {
    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        String path = "/顶顶顶顶/ssss";
        System.out.println(path.getBytes());
        try {
            System.out.println(new String(path.getBytes(),"UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        map.put("path","/顶顶顶顶/ssss");
        String s = JsonUtils.objectToJson(map);
        System.out.println(s);

        Map map1 = JsonUtils.jsonToPojo(s, Map.class);
        System.out.println(map1.get("path"));
    }
}
