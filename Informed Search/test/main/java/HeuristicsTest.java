package main.java;

import org.junit.Test;

import static org.junit.Assert.*;

public class HeuristicsTest {

    @Test
    public void heuristic1() {
        Heuristics hue = new Heuristics();
        hue.heuristic1("134862_75","1238_4765");
        hue.heuristic2("134862_75","1238_4765");
        hue.heuristic3("134862_75","1238_4765");
    }

}