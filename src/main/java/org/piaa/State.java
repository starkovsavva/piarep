package org.piaa;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class State implements Comparable<State> {
    private static final Logger log = LogManager.getLogger(State.class);
    int[][] costMatrix;

    public ArrayList<Integer> path; // Текущий частичный путь
    boolean[] visited; // Посещенные города
    int n; // количество дорог?
    int cost; // Текущая стоимость пути
    int lowerBound; // Нижняя оценка стоимости
    double priority;

    public State(int [][] costMatrix,List<Integer> path, boolean[] visited) {
        this.costMatrix = costMatrix;
        this.path = new ArrayList<>(path);
        this.visited = Arrays.copyOf(visited, visited.length);
        this.cost = calculateCost(path);
        this.n = costMatrix.length - 1; // кол-во городов
        this.lowerBound =  cost + calculateLowerBound(); // Вычисляем нижнюю оценку
        this.priority = calculatePriority();
    }
    private double calculatePriority() {
        int k = path.size() - 1; // Количество дуг (рёбер)
        int N = n; // Общее количество городов

        // S - текущая стоимость пути
        double S = cost;

        // L - нижняя оценка остатка пути (lowerBound - S)
        double L = lowerBound - S;

        // Формула антиприоритета из варианта 4
        double denominator = 0.5 * N + k;
        double priority = (S + L) / denominator;

        return priority;
    }
    public int calculateCost(List<Integer> path) {
        int total = 0;
        int size = (path == null) ? 1: path.size();

        if (path.size() > 1) {

            for (int i = 0; i < path.size() - 1; i++) {
                total += costMatrix[path.get(i)][path.get(i + 1)];
            }
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


    private int calculateLowerBound() {



        int sumMinEdges = calculateHalfSumMinEdges() ; // Полусумма ребер
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
        int startVertex = path.size() > 1 ?  0: path.get(0) ; // null
        int endVertex = path.size() > 1 ? 0 : path.get(path.size() - 1) ; // null
        TreeSet<Integer> chunksIn = new TreeSet<>();// [0 , 1] (0, 1)
        TreeSet<Integer> chunksOut = new TreeSet<>();// [0 , 1] (0, 1)

        int minIn = 0;
        int minOut = 0;
        if (path.size() > 1) {

            for (int j = 0; j < n + 1; j++) {
                if(visited[j] != true && costMatrix[endVertex][j] != -1) {
                    chunksOut.add(costMatrix[endVertex][j]);
                }
            }
            for (int j = 0; j < n + 1; j++) {

                if(costMatrix[startVertex][j] != -1){
                    chunksIn.add(costMatrix[startVertex][j]);
                }
            }

        }
        else{
            for ( int j = 0; j < n + 1  ; j++) {
                if(costMatrix[0][j] != -1){
                    chunksOut.add(costMatrix[0][j]);

                }
            }
            for (int j = 0; j < n + 1; j++) {
                if (costMatrix[j][0] != -1){
                    chunksIn.add(costMatrix[j][0]);

                }
            }
        }
        Integer minOutInt1 = chunksOut.size() > 0 ? chunksOut.first(): 0;
        Integer minOutInt2 = chunksOut.size() >= 2 ?  chunksOut.higher(minOutInt1): 0;
        Integer minInInt1 = chunksIn.size() > 0? chunksIn.first() : 0;
        Integer minInInt2 =  chunksIn.size()  >= 2 ?  chunksIn.higher(minInInt1): 0;
        sum = ((minInInt1 + minInInt2) + (minOutInt2 + minOutInt1))/2;

        return sum;
    }

    @Override
    public int compareTo(State other) {
        return Double.compare(this.priority, other.priority);
    }
}