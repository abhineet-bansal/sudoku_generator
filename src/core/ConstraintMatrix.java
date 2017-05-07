package core;

import java.util.Arrays;

class ConstraintMatrix {

    private static final int CONSTRAINT_COUNT = 3;

    private int rowColSize;
    private int constraintCount;

    private int cmRowCount;
    private int cmColumnCount;
    private boolean[][] cm;
    private Constraint[] constraints;

    ConstraintMatrix(int rowColCount, int constraintNum) {
        rowColSize = rowColCount;
        constraintCount = constraintNum;

        cmRowCount =
                rowColSize * rowColSize *       // number of cells to fill
                        rowColSize;             // possibilities in each cell

        cmColumnCount =
                constraintCount *               // number of constraints
                        rowColSize * rowColSize;// each constraint has this many possibilities

        cm = new boolean[cmRowCount][cmColumnCount];
        for (boolean[] row : cm) {
            Arrays.fill(row, false);
        }

        constraints = new Constraint[CONSTRAINT_COUNT];
        constraints[0] = new CellConstraint();
        constraints[1] = new RowConstraint();
        constraints[2] = new ColumnConstraint();

        fillConstraintMatrix();
    }

    boolean[][] getMatrix() {
        return cm;
    }

    int getRowsCount() {
        return cmRowCount;
    }

    int getColumnsCount() {
        return cmColumnCount;
    }

    int cmRowFromItem(Item item) {
        return ((item.getRow() - 1) * rowColSize * rowColSize) +
                ((item.getCol() - 1) * rowColSize) +
                (item.getNum() - 1);
    }

    Item itemFromCmRow(int cmRow) {
        int r = cmRow / (rowColSize * rowColSize);
        int c = (cmRow / rowColSize) % rowColSize;
        int n = cmRow % rowColSize;

        return new Item(r + 1, c + 1, n + 1);
    }

    void printMatrix() {
        for (int row = 0; row < getRowsCount(); row++) {
            String line = "";
            for (int col = 0; col < getColumnsCount(); col++) {
                line += cm[row][col] ? "0," : "-,";
            }
            System.out.println(line);
        }
    }

    private void fillConstraintMatrix() {
        int cmRowIndex = 0;
        for (int row = 1; row <= rowColSize; row++) {
            for (int col = 1; col <= rowColSize; col++) {
                for (int num = 1; num <= rowColSize; num++) {
                    Item item = new Item(row, col, num);
                    int cmColumnSection = 0;
                    for (int constraint = 0; constraint < constraintCount; constraint++) {
                        int constraintIndex = constraints[constraint].getConstraintMatrixColumnIndex(item);
                        constraintIndex += cmColumnSection;
                        cmColumnSection += (rowColSize * rowColSize);

                        cm[cmRowIndex][constraintIndex] = true;
                    }

                    cmRowIndex++;
                }
            }
        }
    }

    private class CellConstraint implements Constraint {
        @Override
        public int getConstraintMatrixColumnIndex(final Item item) {
            // Constraint:
            //      Some number must be filled in every cell
            //      Column number is the higher order constraint
            //      Row number is the lower order constraint

            return (item.getCol() - 1) * rowColSize + (item.getRow() - 1);
        }
    }

    private class RowConstraint implements Constraint {
        @Override
        public int getConstraintMatrixColumnIndex(final Item item) {
            // Constraint:
            //      Each number must be filled somewhere in every row
            //      The number is the higher order constraint
            //      Row number is the lower order constraint

            return (item.getNum() - 1) * rowColSize + (item.getRow() - 1);
        }
    }

    private class ColumnConstraint implements Constraint {
        @Override
        public int getConstraintMatrixColumnIndex(final Item item) {
            // Constraint:
            //      Each number must be filled somewhere in every column
            //      The number is the higher order constraint
            //      Column number is the lower order constraint

            return (item.getNum() - 1) * rowColSize + (item.getCol() - 1);
        }
    }
}
