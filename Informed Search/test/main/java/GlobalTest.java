package main.java;

import org.junit.Test;

import java.util.Comparator;
import java.util.PriorityQueue;

import static org.junit.Assert.*;

public class GlobalTest {

    @Test
    public void main() {


        String [] arr1B = {"1348627_5","1238_4765","BFS"};
        String [] arr2B = {"281_43765","1238_4765","BFS"};
        String [] arr3B = {"281463_75","1238_4765","BFS"};

        String [] arr1D = {"1348627_5","1238_4765","DFS"};
        String [] arr2D = {"281_43765","1238_4765","DFS"};
        String [] arr3D = {"281463_75","1238_4765","DFS"};

        String [] arr1S = {"1348627_5","1238_4765","AStar"};
        String [] arr2S = {"281_43765","1238_4765","AStar"};
        String [] arr3S = {"281463_75","1238_4765","AStar"};


        Object [] arrSet = {arr1S, arr2S, arr3S};


        for (Object arr: arrSet
             ) {
             Global.main((String [])arr);
            System.out.println("==============================\n");
        }

//        Global.main(arr1S);

//        Comparator<StateCost> comparator = new StateCompare();
//        PriorityQueue<StateCost> prQueue = new PriorityQueue<>(10, comparator );
//
//        StateCost polledState = prQueue.poll();




    }
}