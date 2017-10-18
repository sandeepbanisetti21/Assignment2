package com.sandeep.homework2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Looper {
    public static void main(String[] args) throws InterruptedException{
        GamePlayer gp = new GamePlayer();
//        2, 3, 5, 8, 10, 13, 15,
        ArrayList<Integer> sizes = new ArrayList<>(Arrays.asList(1, 2, 3, 5, 8, 10, 16, 19, 23));
        ArrayList<Integer> fruits = new ArrayList<>(Arrays.asList(1, 2, 5, 8));
        Integer s=0, f=0;
        while(s<sizes.size() && f < fruits.size()){
            if(fruits.get(f) <= sizes.get(s)) {
//                System.out.println(s + " " + f);
                gp.run(sizes.get(s), fruits.get(f));
                System.out.println("GAME OVER, GAME OVER, GAME OVER, GAME OVER");
                System.out.println();
                System.out.println();
                TimeUnit.SECONDS.sleep(3L);
            }
            f=(f+1)%fruits.size();
            if(f==0)
                s=s+1;
        }
    }
}
