package com.jagrosh.jmusicbot.ikisugi;

import java.io.IOException;
import java.nio.file.Paths;

public class Dwonloader {
    private static Dwonloader INSTANS;

    public static void init() {
        INSTANS = new Dwonloader();
        try {
            Paths.get("cash").toFile().createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("114514");
    }

    public static Dwonloader instans() {
        return INSTANS;
    }

}
