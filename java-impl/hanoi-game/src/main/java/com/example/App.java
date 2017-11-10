package com.example;

import com.example.model.*;

public class App {
    public static void main( String[] args ) {
        Game game = Game.create(5);
        System.out.println(game);
        Solver.solve(game);
        System.out.println(game);
    }
}
