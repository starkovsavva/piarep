package org.piaa;
import javax.sound.midi.Soundbank;
import java.util.*;

public class TSP {


    private int[][] cost; // Матрица стоимостей перемещения между городами
    private int n; // Количество городов
    private List<Integer> path; // Текущий построенный путь
    private boolean[] visited; // Посещенные города

    public TSP(int[][] costMatrix) {
        this.cost = costMatrix;
        this.n = costMatrix.length;
        this.path = new ArrayList<>();
        this.visited = new boolean[n];
    }

    /**
     * Улучшенный алгоритм вставки (АВБГ) для построения начального пути.
     * Выбирает город и позицию вставки, минимизируя значение I + O - R,
     * где:
     * I - стоимость входящего ребра,
     * O - стоимость исходящего ребра,
     * R - стоимость заменяемого ребра.
     */
    public List<Integer> improvedAVBG() {
        path.add(0); // Начинаем с города 0 (по условию варианта)
        visited[0] = true;
        // Пока не все города добавлены в путь
        while (path.size() < n) {
            double minIncrease = Double.MAX_VALUE;
            int bestCity = -1;
            int bestPos = -1;

            // Перебираем все города
            for (int city = 0; city < n; city++) {
                if (visited[city]) continue; // Пропускаем уже посещенные

                // Проверяем все возможные позиции для вставки
                for (int pos = 1; pos <= path.size(); pos++) {

                    // Определяем предыдущий и следующий города в текущем пути
                    int prev = (pos == 0) ? -1 : path.get(pos - 1);
                    int next = (pos == path.size()) ? -1 : path.get(pos);

                    // Вычисляем стоимости
                    int I = (prev == -1) ? 0 : cost[prev][city]; // Входящее ребро
                    int O = (next == -1) ? 0 : cost[city][next]; // Исходящее ребро
                    int R = (prev != -1 && next != -1) ? cost[prev][next] : 0; // Заменяемое ребро
                    // Пропускаем недопустимые ребра (стоимость -1)

                    if (I == -1 || O == -1 || R == -1) continue;
                    // Вычисляем приращение стоимости
                    double increase = I + O - R;
                    // Обновляем лучший выбор

                    if (increase < minIncrease ) {
                        minIncrease = increase;
                        bestCity = city;
                        bestPos = pos;
                    }
                }
            }
            // Вставляем город в оптимальную позицию
            if (bestCity != -1) {
                path.add(bestPos, bestCity);
                visited[bestCity] = true;
            }
        }
        return path;
    }

    /**
     * Вычисляет общую стоимость пути, включая возврат в начальный город.
     */
//    public double calculateCost(List<Integer> path) {
//        double total = 0;
//        int size = path.size()  == null ? 0:;
//
//        for (int i = 0; i < path.size() - 1; i++) {
//            total += cost[path.get(i)][path.get(i + 1)];
//        }
//        total += cost[path.get(path.size() - 1)][path.get(0)]; // Возврат в начало
//        return total;
//    }

//    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        int n = scanner.nextInt();
//        int[][] cost = new int[n][n];
//        for (int i = 0; i < n; i++) {
//            for (int j = 0; j < n; j++) {
//                cost[i][j] = scanner.nextInt();
//            }
//        }
//
//        TSP solver = new TSP(cost);
//        List<Integer> path = solver.improvedAVBG();
//        // Форматируем вывод пути
//        System.out.println(path.toString().replaceAll("[\\[\\],]", ""));
//        System.out.println(solver.calculateCost(path));
    }
//}