package org.piaa;
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
                System.out.println("Проверяем что можно вставить город : " + city);
                // Проверяем все возможные позиции для вставки
                for (int pos = 1; pos <= path.size(); pos++) {
                    System.out.println("-Проверяем позицию " + pos + " для города " + city);
                    // Определяем предыдущий и следующий города в текущем пути
                    int prev = (pos == 0) ? -1 : path.get(pos - 1);
                    System.out.println("Пытаемся вычислить prev " + prev);
                    int next = (pos == path.size()) ? -1 : path.get(pos);
                    System.out.println("Пытаемся вычислить next " + next);

                    // Вычисляем стоимости
                    int I = (prev == -1) ? 0 : cost[prev][city]; // Входящее ребро
                    int O = (next == -1) ? 0 : cost[city][next]; // Исходящее ребро
                    int R = (prev != -1 && next != -1) ? cost[prev][next] : 0; // Заменяемое ребро
                    // Пропускаем недопустимые ребра (стоимость -1)

                    if (I == -1 || O == -1 || R == -1) continue;
                    // Вычисляем приращение стоимости
                    double increase = I + O - R;
                    // Обновляем лучший выбор
                    System.out.println("Пытаемся обновить лучший выбор - increase: "+ increase + " mincrease: " + minIncrease);
                    if (increase < minIncrease ) {
                        System.out.println("Обновленные данные: mincrease " + minIncrease  + " bestCity: " + bestCity + " bestPos: " + bestPos);
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
        System.out.println("Оптимальный путь: " + path);
        System.out.println("Лучшая цена: "  + calculateCost(path));
        return path;
    }
    public int calculateCost(List<Integer> path) {
        int total = 0;
        int size = (path == null) ? 1: path.size();

        if (path.size() > 1) {

            for (int i = 0; i < path.size() - 1; i++) {
                total += cost[path.get(i)][path.get(i + 1)];
            }
            total += cost[0][path.size() - 1];
        }


        return total;
    }
    }