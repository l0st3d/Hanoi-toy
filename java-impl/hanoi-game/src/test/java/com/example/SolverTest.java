package com.example;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.example.model.*;

public class SolverTest extends TestCase {

    public void testSolver() {
        Game g = Game.create(3);
        Solver.solve(g);
        assertEquals("\n| | 1 \n| | 2 \n| | 3 \n", g.toString());
    }
}
