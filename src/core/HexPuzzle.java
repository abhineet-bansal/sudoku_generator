package core;

import java.util.Arrays;
import java.util.Set;

public class HexPuzzle {
    private static final int ROW_COL_SIZE = 3;

    public void generatePuzzle() {
        int[] startingPuzzle = new int[ROW_COL_SIZE * ROW_COL_SIZE];
        Arrays.fill(startingPuzzle, 0);

        PuzzleGenerator generator = new PuzzleGenerator();
        generator.initialize(ROW_COL_SIZE, 3);
        generator.generatePuzzles(startingPuzzle);
        Set<Board> validPuzzles = generator.getValidPuzzles();

        System.out.println("Valid Puzzles: " + validPuzzles.size());
    }
}
