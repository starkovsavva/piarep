package org.piaa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


public class Main {
    private static final Logger log = LogManager.getLogger(Main.class);



    private static TSPBranchAndBound tspBranchAndBound = new TSPBranchAndBound();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите опцию:");
        System.out.println("1. Сгенерировать матрицу:");
        System.out.println("2. Выбрать матрицу из файла матрицу:");
        System.out.printf("Ваш выбор: ");
        int choice = scanner.nextInt();
        switch (choice) {
            case 1:
                System.out.printf("Введите размер матрицы: ");
                int size = scanner.nextInt();
                System.out.printf("Выберите тип матрицы 1 ( - симметричная, 2 - несимметричная): ");
                int isSymmetric = scanner.nextInt();
                int[][] matrix = generateMatrix(size, isSymmetric);
                System.out.println("Какой метод вы хотите использовать? ");
                System.out.println("1. Метод ветвей и границ");
                System.out.println("2. АВБГ");
                int choice2 = scanner.nextInt();
                switch (choice2){
                    case 1:
                        printMatrix(matrix);
                        tspBranchAndBound.solve(matrix);
                        break;
                    case 2:
                        printMatrix(matrix);
                        TSP tsp = new TSP(matrix);
                        tsp.improvedAVBG();
                        break;
                }



                break;
            case 2:
                System.out.printf("Введите путь до файла: ");
                String pathToFile = scanner.next();
                try {
                    int[][] matrixToSolve = readMatrixFromFile(pathToFile);
                    System.out.println("Какой метод вы хотите использовать? ");
                    System.out.println("1. Метод ветвей и границ");
                    System.out.println("2. АВБГ");
                    int choice3 = scanner.nextInt();
                    switch (choice3){
                        case 1:
                            printMatrix(matrixToSolve);
                            tspBranchAndBound.solve(matrixToSolve);
                            break;
                        case 2:
                            printMatrix(matrixToSolve);
                            TSP tsp = new TSP(matrixToSolve);
                            tsp.improvedAVBG();
                            break;
                    }
                } catch (IOException e) {
                    System.out.println("Неверное имя файла");
                }
                break;
        }

    }

    private static void printMatrix(int[][] matrix) {
        System.out.println("Ваша матрица:");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static int[][] generateMatrix(int size, int isSymmetric) {
        int[][] matrix = new int[size][size];
        Random random = new Random();

        if (isSymmetric == 1) {
            // Генерация симметричной матрицы с диагональю -1
            for (int i = 0; i < size; i++) {
                for (int j = i; j < size; j++) {
                    if (i == j) {
                        matrix[i][j] = -1;  // Диагональный элемент
                    } else {
                        int value = random.nextInt(100);
                        matrix[i][j] = value;
                        matrix[j][i] = value;  // Зеркальное отражение
                    }
                }
            }
        } else {
            // Генерация обычной матрицы с диагональю -1
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = (i == j) ? -1 : random.nextInt(100);
                }
            }
        }

        return matrix;
    }


    public static int[][] readMatrixFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Читаем размер матрицы
            String sizeLine = reader.readLine();
            if (sizeLine == null) {
                throw new IOException("Файл пустой");
            }

            int size;
            try {
                size = Integer.parseInt(sizeLine.trim());
            } catch (NumberFormatException e) {
                throw new IOException("Некорректный размер матрицы: " + sizeLine, e);
            }

            if (size <= 0) {
                throw new IOException("Размер матрицы должен быть положительным числом: " + size);
            }

            // Инициализируем матрицу
            int[][] matrix = new int[size][size];

            // Читаем строки матрицы
            for (int i = 0; i < size; i++) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IOException("Недостаточно строк в файле. Ожидалось: " + size + ", получено: " + i);
                }

                String[] elements = line.trim().split("\\s+");
                if (elements.length != size) {
                    throw new IOException("Неправильное количество элементов в строке " + (i + 1) +
                            ". Ожидалось: " + size + ", получено: " + elements.length);
                }

                for (int j = 0; j < size; j++) {
                    try {
                        matrix[i][j] = Integer.parseInt(elements[j]);
                    } catch (NumberFormatException e) {
                        throw new IOException("Некорректное число в строке " + (i + 1) + ": " + elements[j], e);
                    }
                }
            }

            return matrix;
        }
    }
}