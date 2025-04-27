package org.piaa;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;

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
        List<Integer> initialPath = new ArrayList<>();
        initialPath.add(0);
        boolean[] visited = new boolean[costMatrix.length];
        visited[0] = true;
        statePriorityQueue.add(new State(costMatrix,initialPath, visited));


        while (!statePriorityQueue.isEmpty()) {
            State current = statePriorityQueue.poll();
            System.out.println("Достаем часть пути из очереди: " + current.path + " с нижней оценкой " + current.lowerBound);
            log.info("state priority queue : " + statePriorityQueue.toString());
            log.info("current state : " + current.toString());

            if( current.cost >= bestCost ) {
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
                    System.out.println("Проверяем завершенный путь и считаем его стоимость: " + current.path);
                    log.info("-- total current.costMatrix : " + total);
                    log.info("-- current.costMatrix new : " + current.cost);
                    log.info("-- total current costMatrix : " + costMatrix[last][0]);

                    System.out.println("Лучшая стоимость пути: " +total);
                    if (total <= bestCost) {
                        System.out.println("Путь оказался лучшим обновляем стоимость");
                        log.info("--- total that lower  : " + total);

                        bestCost = total;
                        bestPath = new ArrayList<>(current.path);
                    }
                    else {
                        System.out.println("Путь не самый лучший, идем дальше");
                    }
                }
                continue;
            }

            // Перебираем все возможные следующие города
            for (int next = 0; next < costMatrix.length; next++) {
                if (!current.visited[next]) {
                    System.out.println("Перебираем лучшие пути: " + current.path + " + новый город " + next);
                    List<Integer> newPath = new ArrayList<>(current.path);
                    newPath.add(next);
                    System.out.println("Создаем маршрут " + newPath);
                    boolean[] newVisited = Arrays.copyOf(current.visited, current.visited.length);
                    newVisited[next] = true;

                    State newState = new State(costMatrix, newPath, newVisited);

                    log.info("---- newState : " + newState.toString());
                    log.info("---- lowerBound : " + newState.lowerBound);
                    log.info("---- bestCost : " + bestCost);
                    // Отсекаем ветви, если оценка хуже текущего лучшего решения
                    System.out.println("Проверяем нижнюю оценку - если плохая отсекаем путь и не добавляем его в очередь");
                    if (newState.lowerBound <= bestCost ) {
                        System.out.println("Нижняя оценка хорошая, добавляем в очередь");
                        statePriorityQueue.add(newState);
                    }
                    else {
                        System.out.println("Нижняя оценка плохая - не добавляем");
                    }
                }
            }
        }
        System.out.println("Лучший путь: " + bestPath);
        System.out.println("Лучшая стоимость: " + bestCost);

    }


}