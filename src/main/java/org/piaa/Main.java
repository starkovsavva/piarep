package org.piaa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;


public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);


    private static TSPBranchAndBound tspBranchAndBound = new TSPBranchAndBound();
    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);
        log.info("Enter length:");
        int n = scanner.nextInt();
        int[][] matrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = scanner.nextInt();
            }
        }
        long startTime = System.currentTimeMillis();
        tspBranchAndBound.setNandMatrix(n, matrix);
        tspBranchAndBound.solve(matrix);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Время выполнения: " + duration  + "мс");
    }


//    public static void main(String[] args) {
//        // field sizes
//        int[] sizes = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
//        int measuredRuns = 5; // runs
//
//        try (FileWriter writer = new FileWriter("time_results.csv")) {
//            // csv file write table
//            writer.write("Size,AvgTime(ms),Tiles\n");
//
//            for (int length : sizes) {
//
//
//                long totalTime = 0;
//                int tiles = 0;
//
//                for (int i = 0; i < measuredRuns; i++) {
//                    Field field = new Field(length);
//                    long start = System.nanoTime();
//                    field.solve();
//                    long end = System.nanoTime();
//                    totalTime += (end - start) / 1_000_000; // milliseconds>>>
//                    tiles = field.getBestSolution().size(); // get tile count
//                }
//
//                long avgTime = totalTime / measuredRuns;
//                writer.write(String.format("%d,%d,%d%n", length, avgTime, tiles));
//                logger.info("Size: {} - Avg Time: {} ms", length, avgTime);
//            }
//        } catch (IOException e) {
//            logger.error("Error writing results", e);
//        }
//    }

//    public static void printBestSolution(List<Square> bestSolution) {
//
//        for (Square solution : bestSolution) {
//            System.out.println(solution.getX() + " " + solution.getY() + " " + solution.getLength());
//        }
//    }
//

}