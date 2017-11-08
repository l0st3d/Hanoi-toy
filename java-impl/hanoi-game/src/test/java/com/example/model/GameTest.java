package com.example.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class GameTest extends TestCase {

    public void testGameShouldCreateAndToStringImplementation() {
        assertEquals("\n1 | | \n2 | | \n3 | | \n", Game.create(3).toString());
    }

    public void testValidMove() {
        Game g = Game.create(3);
        assertEquals(true, g.isValidMove(0, 1));
        assertEquals(false, g.isValidMove(1, 0));
    }

    public void testMakeMove() {
        Game g = Game.create(3);
        g.makeMove(0, 1);
        g.makeMove(0, 2);
        assertEquals("\n| | | \n| | | \n3 1 2 \n", g.toString());
    }
}
