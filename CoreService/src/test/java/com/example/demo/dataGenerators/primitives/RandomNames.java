package com.example.demo.dataGenerators.primitives;

import com.example.demo.model.constants.Type;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
public class RandomNames {

    private List<String> list;
    private Random random=new Random();


    public static RandomNames getInstance(){
        return new RandomNames();
    }


    public RandomNames(){

        list=new ArrayList<>();

        for(int i = 0; i<40; i++){
            list.add("Anketa " +  getType() + " " + year());
        }

    }

    public int getSize(){
        return list.size();
    }

    public String getFromPos(int pos){
        return new String(list.get(pos));
    }

    public String getFromRandom(){
        return new String(list.get(Math.abs(random.nextInt())%getSize()));
    }

    private String getType(){
        return Type.values()[Math.abs(random.nextInt())%Type.values().length].name();
    }

    private int year(){
        return 2015 + Math.abs(random.nextInt())%(10);
    }

}
