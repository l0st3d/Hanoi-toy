package com.example;

import com.example.model.*;

class Solver {

    private final Game game;

    private Solver(Game game) {
        this.game = game;
    }
       
    public static void solve(Game game) {
        Solver s = new Solver(game);
        s.run(game.getMaxTileSize(), 0, 1, 2);
    }

    private void run(int n, int pillar1, int pillar2, int pillar3) {
        if (n > 0) {
            run(n - 1, pillar1, pillar3, pillar2);
            game.makeMove(pillar1, pillar3);
            run(n - 1, pillar2, pillar1, pillar3);
        }
    }
    
}
