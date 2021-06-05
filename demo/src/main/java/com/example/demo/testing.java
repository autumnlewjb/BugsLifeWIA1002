package com.example.demo;

import java.util.*;

public class testing {

    public static void main(String[] args) {
        HashMap<Integer, String> map = new HashMap<>();
        map.put(1, "A");
        map.put(2, "B");
        map.put(1, "C");
        System.out.println(map);
    }
}
