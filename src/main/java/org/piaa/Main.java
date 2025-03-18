package org.piaa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Scanner;


public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        logger.info("Enter length:");
        String[] inputValues = scanner.nextLine().split(" ");
        int length = Integer.parseInt(inputValues[0]);
        int width = (inputValues.length > 1) ? Integer.parseInt(inputValues[1]) : length;
        logger.info("Created field of size {}", length);

        Table table = new Table(length,width);
        long startTime = System.currentTimeMillis();
        table.solve();
        printBestSolution(table.getBestSolution());
        System.out.println( "  ---------- The best solution! -----------  \n"+ table.getBestSolution());


        long endTime = System.currentTimeMillis();
        logger.info("Execution time: {} ms", endTime - startTime);
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

    public static void printBestSolution(List<Square> bestSolution) {

        for (Square solution : bestSolution) {
            System.out.println(solution.getX() + " " + solution.getY() + " " + solution.getLength());
        }
    }


}