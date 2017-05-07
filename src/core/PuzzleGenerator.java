package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PuzzleGenerator {

    private static final boolean DEBUG = false;

    private int rowColSize;
    private ConstraintMatrix cm;

    private boolean[] rowsCovered;
    private boolean[] colsCovered;
    private List<Integer> selectedRows;

    private Set<Board> validPuzzles;

    void initialize(int rowColCount, int constraintNum) {
        rowColSize = rowColCount;
        cm = new ConstraintMatrix(rowColCount, constraintNum);
        cm.printMatrix();
        validPuzzles = new HashSet<>();
    }

    void generatePuzzles(int[] puzzle) {
        long startTime = getMilliseconds();

        // Check if given array is of valid size
        if (puzzle.length != (rowColSize * rowColSize)) {
            return;
        }

        // Initialize
        resetCovering();

        // Check all filled positions in puzzle
        for (int index = 0; index < puzzle.length; index++) {
            if (puzzle[index] == 0) {
                continue;
            }

            int row = index / 9;
            int col = index % 9;

            Item item = new Item(row, col, puzzle[index]);
            int cmRow = cm.cmRowFromItem(item);
            if (!coverRow(cmRow, "")) {
                // Invalid starting puzzle
                return;
            }
        }

        // Start processing
        try {
            recursiveProcessing("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        long completedTime = getMilliseconds();
        long timeTaken = completedTime - startTime;

        System.out.println("Completed! Found puzzles: " + validPuzzles.size());
        for (Board b : validPuzzles) {
            System.out.println(b.toString());
        }

        System.out.println("Time Taken: " + timeTaken + "ms.");
    }

    Set<Board> getValidPuzzles() {
        return validPuzzles;
    }

    private void resetCovering() {
        rowsCovered = new boolean[cm.getRowsCount()];
        colsCovered = new boolean[cm.getColumnsCount()];

        Arrays.fill(rowsCovered, false);
        Arrays.fill(colsCovered, false);

        selectedRows = new ArrayList<>();
    }

    private boolean coverRow(int rowIndex, String prefix) {
        // If the row is already covered, there is an error
        if (rowsCovered[rowIndex]) {
            return false;
        }

        // Find all constraints met by this row
        for (int col = 0; col < cm.getColumnsCount(); col++) {
            if (!cm.getMatrix()[rowIndex][col]) {
                continue;
            }

            // Constraint is met, so cover the corresponding column
            colsCovered[col] = true;

            if (DEBUG) System.out.println(prefix + "Met Col: " + col);

            // Find other rows that meet this constraint, cover them i.e. remove them from future selection
            for (int row = 0; row < cm.getRowsCount(); row++) {
                if (row == rowIndex || !cm.getMatrix()[row][col]) {
                    continue;
                }

                if (!rowsCovered[row]) {
                    rowsCovered[row] = true;

                    if (DEBUG) System.out.println(prefix + "Deleting row: " + row);
                }
            }
        }

        rowsCovered[rowIndex] = true;
        selectedRows.add(rowIndex);
        return true;
    }

    private void uncoverRow(int rowIndex, String prefix) {

        // If the row is already uncovered, nothing to do
        if (!rowsCovered[rowIndex]) {
            return;
        }

        // Find all constraints met by this row
        for (int col = 0; col < cm.getColumnsCount(); col++) {
            if (!cm.getMatrix()[rowIndex][col]) {
                continue;
            }

            // Constraint was met, so uncover the corresponding column
            colsCovered[col] = false;

            if (DEBUG) System.out.println(prefix + "Uncovering Col: " + col);

            // Find other rows that meet this constraint, uncover them
            for (int row = 0; row < cm.getRowsCount(); row++) {
                if (row == rowIndex || !cm.getMatrix()[row][col]) {
                    continue;
                }

                // Verify if they meet an already covered constraint
                boolean covered = false;
                for (int col2 = 0; col2 < cm.getColumnsCount(); col2++) {
                    if (colsCovered[col2] && cm.getMatrix()[row][col2]) {
                        covered = true;
                        break;
                    }
                }

                if (!covered) {
                    rowsCovered[row] = false;

                    if (DEBUG) System.out.println(prefix + "Un-deleting row: " + row);
                }
            }
        }

        rowsCovered[rowIndex] = false;
        selectedRows.remove(selectedRows.indexOf(rowIndex));
    }

    private void recursiveProcessing(String prefix) throws Exception {
        if (DEBUG) System.out.println(prefix + "Recursive");
        if (isComplete()) {
            validPuzzles.add(createdBoard());
            if (DEBUG) System.out.println(prefix + "Found a solution! Count: " + validPuzzles.size());
            return;
        }

        // Select an uncovered column i.e. unsatisfied constraint
        for (int col = 0; col < cm.getColumnsCount(); col++) {
            if (colsCovered[col]) {
                continue;
            }

            if (DEBUG) System.out.println(prefix + "Solving Column: " + col);

            // Select an uncovered row satisfying the selected constraint
            boolean foundSomeRow = false;
            for (int row = 0; row < cm.getRowsCount(); row++) {
                if (rowsCovered[row] || !cm.getMatrix()[row][col]) {
                    continue;
                }

                foundSomeRow = true;
                if (DEBUG) System.out.println(prefix + "Covering Row: " + row + " for column: " + col);

                if (coverRow(row, prefix)) {
                    recursiveProcessing(prefix + "  ");
                }

                if (DEBUG) System.out.println(prefix + "Uncovering Row: " + row);

                uncoverRow(row, prefix);
            }

            if (!foundSomeRow) {
                if (DEBUG) System.out.println(prefix + "NO ROW FOUND. Abort here.");
                return;
            }
        }
    }

    private boolean isComplete() throws Exception {
        boolean rowIndicator = selectedRows.size() == rowColSize * rowColSize;
        boolean colIndicator = true;
        for (boolean colCovered : colsCovered) {
            if (!colCovered) {
                colIndicator = false;
                break;
            }
        }

        if (rowIndicator && colIndicator) {
            return true;
        }

        if (rowIndicator || colIndicator) {
            throw new Exception("Error! One of completion indicators satisfied");
        }

        return false;
    }

    private Board createdBoard() {
        int[] newPuzzle = new int[(rowColSize * rowColSize)];
        for (int cmRow : selectedRows) {
            Item item = cm.itemFromCmRow(cmRow);
            int puzzleIndex = (item.getRow() - 1) * rowColSize + (item.getCol() - 1);
            newPuzzle[puzzleIndex] = item.getNum();
        }

        Board board = new Board();
        board.setValues(newPuzzle);
        return board;
    }

    private static long getMilliseconds() {
        return new Date().getTime();
    }
}
