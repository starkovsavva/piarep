package org.piaa;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;


public class TSPBranchAndBound {
    private static final Logger log = LogManager.getLogger(TSPBranchAndBound.class);
    private int n; // Количество городов
    private int[][] costMatrix;
    private PriorityQueue<State> statePriorityQueue = new PriorityQueue<>();
    private double bestCost = Double.MAX_VALUE;
    private List<Integer> bestPath;

    public TSPBranchAndBound() {
    }


    public void solve(int[][] cost) {
        // Инициализация начального состояния (город 0)
        TSP tsp = new TSP(cost);
        tsp.improvedAVBG();
        bestCost = calculateCost(tsp.improvedAVBG());
        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(0);
        boolean[] visited = new boolean[cost.length];
        visited[0] = true;
        statePriorityQueue.add(new State(costMatrix,initialPath, visited));


        while (!statePriorityQueue.isEmpty()) {
            State current = statePriorityQueue.poll();

            log.info("state priority queue : " + statePriorityQueue.toString());
            log.info("current state : " + current.toString());
            System.out.println("Current lowerbound : " + current.lowerBound);
            System.out.println("Current cost : " + current.cost);
            if( current.lowerBound > bestCost || current.cost > bestCost ) {
                log.info("- skipped current.lowerBound : " + current.lowerBound);
                log.info("- skipped current.cost : " + current.cost);
                continue;
            }

            log.info("- NONskipped current.lowerBound : " + current.lowerBound);
            log.info("- NONskipped current.cost : " + current.cost);
            // Если путь завершен, проверяем его стоимость
            if (current.path.size() == costMatrix.length) {
                int last = current.path.get(current.path.size() - 1);
                if (costMatrix[last][0] != Integer.MAX_VALUE || costMatrix[last][0] != -1) {
                    double total = current.cost + costMatrix[last][0];
                    log.info("-- total current.cost : " + total);
                    log.info("-- current.cost new : " + current.cost);
                    log.info("-- total current costMatrix : " + costMatrix[last][0]);

                    if (total <= bestCost) {
                        log.info("--- total that lower  : " + total);

                        bestCost = total;
                        bestPath = new ArrayList<>(current.path);
                    }
                }
                continue;
            }

            // Перебираем все возможные следующие города
            for (int next = 0; next < cost.length; next++) {
                if (!current.visited[next]) {
                    List<Integer> newPath = new ArrayList<>(current.path);
                    newPath.add(next);
                    boolean[] newVisited = Arrays.copyOf(current.visited, current.visited.length);
                    newVisited[next] = true;
                    double newCost = current.cost + cost[current.path.get(current.path.size()-1)][next];

                    State newState = new State(costMatrix,newPath, newVisited);

                    log.info("---- newState : " + newState.toString());
                    log.info("---- lowerBound : " + newState.lowerBound);
                    log.info("---- bestCost : " + bestCost);
                    // Отсекаем ветви, если оценка хуже текущего лучшего решения
                    if (newState.lowerBound <= bestCost) {
                        statePriorityQueue.add(newState);
                    }
                }
            }
        }
        System.out.println("Optimal path: " + bestPath);
        System.out.println("Best cost: " + bestCost);

    }

    private int calculatePathCost(int[][] matrix, List<Integer> path) {
        int cost = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            int from = path.get(i);
            int to = path.get(i + 1);

            // Проверка на недостижимость
            if (matrix[from][to] == Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }

            cost += matrix[from][to];
        }
        return cost;
    }



    public void setNandMatrix(int n, int[][] costMatrix) {
        this.n = n;
        this.costMatrix = costMatrix;
    }



    public double calculateCost(List<Integer> path) {
        double total = 0;
        int size = (path == null) ? 1: path.size();

        for (int i = 0; i < path.size() - 1; i++) {
            total += costMatrix[path.get(i)][path.get(i + 1)];
        }
        return total;
    }
}