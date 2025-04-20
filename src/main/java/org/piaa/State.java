package org.piaa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class State implements Comparable<State> {
    private static final Logger log = LogManager.getLogger(State.class);
    int[][] costMatrix;

    List<Integer> path; // Текущий частичный путь
    boolean[] visited; // Посещенные города
    int n; // количество дорог?
    int cost; // Текущая стоимость пути
    int lowerBound; // Нижняя оценка стоимости
    int priority;

    public State(int [][] costMatrix,List<Integer> path, boolean[] visited) {
        this.costMatrix = costMatrix;
        this.path = new ArrayList<>(path);
        this.visited = Arrays.copyOf(visited, visited.length);
        this.cost = calculateCost(path);
        this.n = costMatrix.length - 1; // кол-во городов
        this.lowerBound = cost + calculateLowerBound(); // Вычисляем нижнюю оценку
        this.priority = calculatePriority();
    }
    private int calculatePriority() {
        int k = Math.max(path.size() - 1, 1);
        int L = path.size();
        return (cost / k +  L / n) * (4 * n / (3 * n + k));
    }
    public int calculateCost(List<Integer> path) {
        int total = 0;
        int size = (path == null) ? 1: path.size();

        for (int i = 0; i < path.size() - 1; i++) {
            total += costMatrix[path.get(i)][path.get(i + 1)];
        }
        return total;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n--------- state ---------\n");
        sb.append( "path : " + path.toString() + "\n");
        sb.append("visited : ");
        for (int i = 0; i < visited.length; i++) {
            sb.append(visited[i] + " ");

        }
        sb.append("\n");

        sb.append( "n: "+ n + "\n");
        sb.append( "cost : " + cost + "\n");
        sb.append( "lowerBound : " + lowerBound + "\n");
        sb.append( "priority : " + priority + "\n");
        sb.append("--------- state ---------\n");
        return sb.toString();



    }


    /**
     * Вычисляет нижнюю оценку как максимум из двух значений:
     * 1. Полусумма минимальных входящих/исходящих ребер для всех кусков.
     * 2. Вес минимального остовного дерева (МОД) для допустимых ребер.
     */
    private int calculateLowerBound() {



        // TODO: Реализовать вычисление оценок согласно варианту 4
        int sumMinEdges = (path.size() > 1) ? calculateHalfSumMinEdges() : 0; // Полусумма ребер
        log.info("--- sumMinEdges : " + sumMinEdges);

        int mstWeight = calculateMST(); // Вес МОД
        log.info("--- mstWeight : " + mstWeight);
        return Math.max(sumMinEdges, mstWeight);
    }

    private int calculateMST() {
        int startVertex = path.get(0);
        int currentVertex = path.get(path.size() - 1);
        Set<Integer> visited = new HashSet<>(path);
        if (visited.size() == n) {
            return 0;
        }
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        Set<Integer> inMST = new HashSet<>();
        int weight = 0;
        pq.add(new int[]{currentVertex, 0});
        while (inMST.size() < n - visited.size() + 1 && !pq.isEmpty()) {
            int[] entry = pq.poll();
            int vertex = entry[0];
            int edgeWeight = entry[1];

            if (inMST.contains(vertex)) {
                continue;
            }
            inMST.add(vertex);
            weight += edgeWeight;
            for (int v = 0; v < n; v++) {
                if(v != vertex && (!visited.contains(v) || v == startVertex)
                        && (costMatrix[vertex][v] != -1 || costMatrix[vertex][v] != Integer.MAX_VALUE)) {
                    pq.add(new int[]{v, costMatrix[vertex][v]});
                }
            }

        }

        return inMST.size() == n - visited.size() + 1 ? weight : Integer.MAX_VALUE;

    }

    public int calculateHalfSumMinEdges() {
        int sum = 0;
        int startVertex = path.get(0);
        Set<Integer> visited = new HashSet<>(path);
        for (int i = 0; i < n; i++) {
            if (visited.contains(i) && i != startVertex) {
                continue;
            }

            // Фильтрация допустимых рёбер
            List<Integer> edges = new ArrayList<>();
            for (int j = 0; j < n; j++) {

                if (costMatrix[i][j] != -1) {

                    edges.add(costMatrix[i][j]);
                    log.info("edge " + i + " " + j + " : " + costMatrix[i][j]);
                }
            }

            // Сортировка и вычисление суммы
            Collections.sort(edges);
            if (edges.isEmpty()) {
                return Integer.MAX_VALUE;
            } else if (edges.size() == 1) {
                sum += edges.get(0);
            } else {
                sum += (edges.get(0) + edges.get(1)) / 2;
            }
        }
        return sum;
    }

    private int calculateHalfSum() {
        int sum = 0;
        int startVertex = path.get(0);
        Set<Integer> visited = new HashSet<>(path);
        Set<Integer> components = getComponents(visited);

        for (int comp : components) {
            List<Integer> nodes = getNodesInComponent(comp);
            sum += calculateMinEdgesForComponent(nodes, visited);
        }
        return sum / 2;
    }

    private Set<Integer> getComponents(Set<Integer> visited) {
        Set<Integer> comps = new HashSet<>();
        comps.add(0); // Текущий путь как компонента -1
        for (int i = 0; i < n; i++) {
            if (!visited.contains(i)) comps.add(i);
        }
        return comps;
    }

    private List<Integer> getNodesInComponent(int comp) {
        return (comp == -1) ? path : Collections.singletonList(comp);
    }

    private double calculateMinEdgesForComponent(List<Integer> nodes, Set<Integer> visited) {
        int minIn = Integer.MAX_VALUE;
        int minOut = Integer.MAX_VALUE;
        int startVertex = path.get(0);

        for (int node : nodes) {
            // Минимальное входящее ребро
            for (int i = 0; i < n; i++) {
                if (!visited.contains(i) && costMatrix[i][node] != -1) {
                    minIn = Math.min(minIn, costMatrix[i][node]);
                }
            }
            // Минимальное исходящее ребро
            for (int j = 0; j < n; j++) {
                if (!visited.contains(j) && costMatrix[node][j] != -1) {
                    minOut = Math.min(minOut, costMatrix[node][j]);
                }
            }
        }
        return (minIn + minOut);
    }

    @Override
    public int compareTo(State other) {
        return Double.compare(this.priority, other.priority);
    }
}