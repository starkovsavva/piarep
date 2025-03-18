package org.piaa;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Table {
    private static final Logger log = LogManager.getLogger(Table.class);
    ArrayList<ArrayList<Square>> placed = new ArrayList<>();
    private List<Square> bestSolution = new ArrayList<>();
    private final int width;
    private final int length;
    private int filledSquares;
    private boolean engagedPlaces[][];
    private int squaresMin = Integer.MAX_VALUE;

    public int getSquaresMin() {
        return squaresMin;
    }

    public List<Square> getBestSolution() {
        return bestSolution;
    }

    public int getLength() {
        return length;
    }


    public Table(int length, int width) {
        this.length = length;
        this.width = width;
        this.engagedPlaces = new boolean[length][width];
        this.filledSquares = 0;
    }




    private boolean placeOppotunity(int x, int y, int size) {
        if (x + size > length || y + size > width) return false;
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                if (engagedPlaces[x + dx][y + dy]) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] findEmpty() {
        for (int i = 0; i < length; i++) {
            for (int j = 0; j < width; j++) {
                if (!engagedPlaces[i][j]){
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private void place(int x, int y, int size, boolean state) {
        for (int dx = 0; dx < size; dx++) {
            for (int dy = 0; dy < size; dy++) {
                engagedPlaces[x + dx][y + dy] = state;
            }
        }
        filledSquares += (state ? size * size : -size * size);
    }

    public void solve() {
        log.info("Starting field solution...");
        backtrack(new ArrayList<>(), 0,0);
        log.info("Min square count: {}", squaresMin);

    }


    private void backtrack(List<Square> alreadyPlaced, int alreadyUsed, int depthOfRecursion) {
        if (alreadyUsed >= squaresMin) {
            log.debug(">>> {}{} The way is not optimal, should go back.", depthOfRecursion, "-".repeat(depthOfRecursion));
            return;
        }

        int[] pos = findEmpty();
        if (pos == null) {
            if (alreadyUsed < squaresMin) {
                log.info(">>> {}{} Finded new bestway with {} squares.", depthOfRecursion, "-".repeat(depthOfRecursion), alreadyUsed);
                squaresMin = alreadyUsed;
                bestSolution = new ArrayList<>(alreadyPlaced);
            }
            return;
        }

        int x = pos[0], y = pos[1];
        int maxSize = Math.min(length - x, width - y);
        maxSize = Math.min(maxSize, Math.min(length, width) - 1);
        int areaThatRemaining = length * width - filledSquares;
        int possibleSizeMax = maxSize;
        int possibleMinSize = (int) Math.ceil((double) areaThatRemaining / (possibleSizeMax * possibleSizeMax));


        //predict
        if (alreadyUsed + possibleMinSize >= squaresMin) {
            return;
        }

        log.debug(">>> {}{} Attempt to place square on coordinates ({}, {}) *****", depthOfRecursion, "-".repeat(depthOfRecursion), x + 1, y + 1);

        for (int size = maxSize; size >= 1; size--) {
            if (placeOppotunity(x, y, size)) {
                log.debug(">>> {}{} Place a square of size {}x{} at position  + ({}, {})", depthOfRecursion, "-".repeat(depthOfRecursion), size, size, x + 1, y + 1);
                place(x, y, size, true);
                alreadyPlaced.add(new Square(x + 1, y + 1, size));
                backtrack(alreadyPlaced, alreadyUsed + 1, depthOfRecursion + 1);
                alreadyPlaced.remove(alreadyPlaced.size() - 1);
                place(x, y, size, false);
                log.debug(">>> {}{} Remove a square of size {}x{} from position ({}, {})", depthOfRecursion, "-".repeat(depthOfRecursion), size, size, x + 1, y + 1);
            } else {
                log.debug(">>> {}{} A square of size {}x{} cannot be placed in position ({}, {})", depthOfRecursion, "-".repeat(depthOfRecursion), size, size, x + 1, y + 1);
            }
        }
    }
}
