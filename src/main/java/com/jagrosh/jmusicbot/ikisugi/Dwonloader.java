package com.jagrosh.jmusicbot.ikisugi;

public class Dwonloader {
    private static Dwonloader INSTANS;

    public static void init() {
        INSTANS = new Dwonloader();
    }

    public static Dwonloader instans() {
        return INSTANS;
    }

}
