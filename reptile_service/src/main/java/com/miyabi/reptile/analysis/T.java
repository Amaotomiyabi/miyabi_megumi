package com.miyabi.reptile.analysis;

import java.sql.SQLException;

public class T {
    public static void main(String[] args) throws SQLException {
        var s = "1girl\n" +
                "a-plug\n" +
                "ahegao\n" +
                "animal_ear_fluff\n" +
                "animal_ears\n" +
                "anus\n" +
                "ass\n" +
                "bent_over\n" +
                "blush\n" +
                "bottomless\n" +
                "brown_eyes\n" +
                "brown_hair\n" +
                "cat_ears\n" +
                "cat_tail\n" +
                "chen\n" +
                "commentary_request\n" +
                "dutch_angle\n" +
                "eyebrows_visible_through_hair\n" +
                "feet_out_of_frame\n" +
                "from_behind\n" +
                "gaping\n" +
                "gold_trim\n" +
                "hat\n" +
                "highres\n" +
                "looking_back\n" +
                "mob_cap\n" +
                "multiple_tails\n" +
                "nekomata\n" +
                "nose_blush\n" +
                "profile\n" +
                "pussy\n" +
                "red_vest\n" +
                "saliva\n" +
                "saliva_trail\n" +
                "short_hair\n" +
                "solo\n" +
                "spread_anus\n" +
                "sweat\n" +
                "tail\n" +
                "touhou\n" +
                "two_tails\n" +
                "vest\n" +
                "watermark\n";
        var s1 = s.split(" ");
        for (String s2 : s1) {
            System.out.println(s2.replaceAll("_", " "));
        }
    }
}
