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


    public void solve(int[][] costMatrix) {
        // Инициализация начального состояния (город 0)
        TSP tsp = new TSP(costMatrix);
        tsp.improvedAVBG();
        bestCost = calculateCost(tsp.improvedAVBG());
        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(0);
        boolean[] visited = new boolean[costMatrix.length];
        visited[0] = true;
        statePriorityQueue.add(new State(costMatrix,initialPath, visited));


        while (!statePriorityQueue.isEmpty()) {
            if (statePriorityQueue.size() < 500) {
            State current = statePriorityQueue.poll();

            log.info("state priority queue : " + statePriorityQueue.toString());
            log.info("current state : " + current.toString());
//            System.out.println("Current lowerbound : " + current.lowerBound);
//            System.out.println("Current costMatrix : " + current.costMatrix);
            ArrayList<Integer> check = new ArrayList<>(Arrays.asList(0,6,14,1,13,4,8));
            if (current.path.equals(check)){
                log.info("--- CHECKCHECKCHECKCHECKCHECKCHECKCHECKCHECK new path : " + current);
            }
            if( current.lowerBound >= bestCost ) {
                log.info("- skipped current.lowerBound : " + current.lowerBound);
                log.info("- skipped current.costMatrix : " + current.cost);
                continue;
            }

            log.info("- NONskipped current.lowerBound : " + current.lowerBound);
            log.info("- NONskipped current.costMatrix : " + current.cost);
            // Если путь завершен, проверяем его стоимость
            if (current.path.size() == costMatrix.length) {
                int last = current.path.get(current.path.size() - 1);
                if (costMatrix[last][0] != Integer.MAX_VALUE || costMatrix[last][0] != -1) {
                    double total = current.cost + costMatrix[last][0];
                    log.info("-- total current.costMatrix : " + total);
                    log.info("-- current.costMatrix new : " + current.cost);
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
            for (int next = 0; next < costMatrix.length; next++) {
                if (!current.visited[next]) {
                    List<Integer> newPath = new ArrayList<>(current.path);
                    newPath.add(next);
                    boolean[] newVisited = Arrays.copyOf(current.visited, current.visited.length);
                    newVisited[next] = true;
                    ArrayList<Integer> check2 = new ArrayList<>(Arrays.asList(0,6,14,1,13,4,8));
                    ArrayList<Integer> check4 = new ArrayList<>(Arrays.asList(0,6,14,1,13,4,8));
                    if (newPath.equals(check2) ) {
                            log.info("--- CHECKCHECKCHECKCHECKCHECKCHECKCHECKCHECK new path : " + newPath);
                    }
                    if (current.path.equals(check4)) {
                        log.info("--- CHECKCHECKCHECKCHECKCHECKCHECKCHECK new path : " + current.path);
                    }

                    State newState = new State(costMatrix, newPath, newVisited);

                    log.info("---- newState : " + newState.toString());
                    log.info("---- lowerBound : " + newState.lowerBound);
                    log.info("---- bestCost : " + bestCost);
                    // Отсекаем ветви, если оценка хуже текущего лучшего решения
                    if (newState.lowerBound <= bestCost ) {
                        statePriorityQueue.add(newState);
                    }
                }
            }
            }
            else {
                // Удаляем 50% худших элементов
                int newSize = statePriorityQueue.size() / 2;
                PriorityQueue<State> newQueue = new PriorityQueue<>();
                for (int i = 0; i < newSize; i++) {
                    State current3 = statePriorityQueue.poll();
                    ArrayList<Integer> check3 = new ArrayList<>(Arrays.asList(0,6,14,1,13));
                    if (current3.path.equals(check3)){
                        log.info("--- CHECKCHECKCHECKCHECKCHECKCHECKCHECKCHECK new path : " + current3);
                    }
                    newQueue.add(current3);
                }
                statePriorityQueue = newQueue;

            }
        }
        System.out.println("Optimal path: " + bestPath);
        System.out.println("Best costMatrix: " + bestCost);

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