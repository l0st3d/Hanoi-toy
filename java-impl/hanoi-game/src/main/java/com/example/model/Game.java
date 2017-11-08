package com.example.model;

import java.util.*;

public class Game {
    final private List<List<Tile>> pillars;
    final private int maxTileSize;
    final private int numberOfPillars = 3;

    private Game(int maxTileSize) {
        this.maxTileSize = maxTileSize;
        pillars = new ArrayList(numberOfPillars + 1);
        ArrayList<Tile> firstPillar = new ArrayList(maxTileSize + 1);
        for (int c = maxTileSize ; c > 0 ; c--)
            firstPillar.add(new Tile(c));
        pillars.add(firstPillar);
        for (int c = 1 ; c < numberOfPillars ; c++)
            pillars.add(new ArrayList(maxTileSize + 1));
    }
        
    public static Game create(int maxTileSize) {
        return new Game(maxTileSize);
    }

    public int getMaxTileSize() {
        return maxTileSize;
    }
    
    public boolean isValidMove(int from, int to) {
        // NOTE: debugging code needed to find a typo.
        
        // System.out.println("checking valid move > " + from + " -> " + to + " size " + pillars.size());
        // System.out.println("game : " + this);
        // if (pillars.size() > from) {
        //     System.out.println(" from >> " + pillars.get(from) + " - " + pillars.get(from).size());
        //     if (pillars.size() > to) {
        //         System.out.println("   to >> " + pillars.get(to) + " - " + pillars.get(to).size());
        //         if (pillars.get(from).size() > 0) {
        //             System.out.println(" --> " + (pillars.get(from).get(pillars.get(from).size() - 1).getSize()));
        //         }
        //         if (pillars.get(to).size() > 0) {
        //             System.out.println(" > " + (pillars.get(to).get(pillars.get(to).size() - 1).getSize()));
        //         }
        //     }
        // }
        
        return (pillars.size() > from)
            && (pillars.get(from).size() > 0)
            && ((pillars.get(to).size() == 0)
                || ((pillars.get(from).get(pillars.get(from).size() - 1).getSize())
                    < (pillars.get(to).get(pillars.get(to).size() - 1).getSize())));
    }

    public void makeMove(int from, int to) {
        if (isValidMove(from, to)) {
            List<Tile> fromPillar = pillars.get(from);
            List<Tile> toPillar = pillars.get(to);
            Tile tileToMove = fromPillar.get(fromPillar.size() - 1);
            toPillar.add(tileToMove);
            fromPillar.remove(tileToMove);
        } else
            throw new IllegalArgumentException("Invalid move " + from + " > " + to + " " + this.toString());
    }
    
    public String toString() {
        String s = "\n";
        for (int row = maxTileSize - 1 ; row >= 0 ; row--) {
            for (int col = 0 ; col < 3 ; col++) {
                if ((pillars.size() > col) && (pillars.get(col).size() > row))
                    s = s + pillars.get(col).get(row).getSize();
                else
                    s = s + "|";
                s = s + " ";
            }
            s = s + "\n";
        }
        return s;
    }

    public static class Tile {
        
        static private Map<Integer, Tile> cache = new HashMap();
            
        final int size;
        
        private Tile(int size) {
            this.size = size;
        }

        int getSize() {
            return size;
        }

        public String toString() {
            return "Tile <" + size + ">";
        }
    }
}
