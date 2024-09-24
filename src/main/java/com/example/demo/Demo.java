package com.example.demo;

import cn.hutool.http.HttpUtil;

public class Demo {

    public static void main(String[] args) {
        new Thread(() ->{
            while (true) {
                String s = HttpUtil.get("http://localhost:8080/rateLimit");
                System.out.println(s);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

    }
}
