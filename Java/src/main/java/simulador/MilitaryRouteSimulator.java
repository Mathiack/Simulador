package simulador;

import java.util.*;

class Node implements Comparable<Node> {
    int id;
    int distance;
    int risk;
    int time;
    String terrain;

    Node(int id, int distance, int risk, int time, String terrain) {
        this.id = id;
        this.distance = distance;
        this.risk = risk;
        this.time = time;
        this.terrain = terrain;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance + this.risk + this.time, other.distance + other.risk + other.time);
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id +
                ", distance=" + distance +
                ", risk=" + risk +
                ", time=" + time +
                ", terrain='" + terrain + '\'' +
                '}';
    }
}

class MilitaryRouteSimulator {
    private Map<Integer, List<Node>> map;
    private List<String> missionHistory;
    private int riskWeight = 1;
    private int distanceWeight = 1;
    private int timeWeight = 1;

    MilitaryRouteSimulator() {
        map = new HashMap<>();
        missionHistory = new ArrayList<>();
    }

    void addRoute(int source, int destination, int distance, int risk, int time, String terrain) {
        map.computeIfAbsent(source, k -> new ArrayList<>()).add(new Node(destination, distance, risk, time, terrain));
        map.computeIfAbsent(destination, k -> new ArrayList<>()).add(new Node(source, distance, risk, time, terrain));
    }

    void setWeights(int riskWeight, int distanceWeight, int timeWeight) {
        this.riskWeight = riskWeight;
        this.distanceWeight = distanceWeight;
        this.timeWeight = timeWeight;
    }

    void calculateOptimalRoute(int start, int end) {
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(node -> calculateCost(node.distance, node.risk, node.time)));
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> risks = new HashMap<>();
        Map<Integer, Integer> times = new HashMap<>();
        Map<Integer, Integer> predecessors = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        pq.add(new Node(start, 0, 0, 0, "Start"));
        distances.put(start, 0);
        risks.put(start, 0);
        times.put(start, 0);
        predecessors.put(start, null);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current.id)) continue;
            visited.add(current.id);

            if (current.id == end) {
                logMission(start, end, distances, risks, times, predecessors);
                return;
            }

            for (Node neighbor : map.getOrDefault(current.id, Collections.emptyList())) {
                int newDist = distances.get(current.id) + neighbor.distance;
                int newRisk = risks.get(current.id) + neighbor.risk;
                int newTime = times.get(current.id) + neighbor.time;

                int cost = calculateCost(newDist, newRisk, newTime);

                if (!distances.containsKey(neighbor.id) || cost < calculateCost(distances.get(neighbor.id), risks.get(neighbor.id), times.get(neighbor.id))) {
                    distances.put(neighbor.id, newDist);
                    risks.put(neighbor.id, newRisk);
                    times.put(neighbor.id, newTime);
                    predecessors.put(neighbor.id, current.id);
                    pq.add(new Node(neighbor.id, newDist, newRisk, newTime, neighbor.terrain));
                }
            }
        }
        System.out.println("No route found.");
    }

    private int calculateCost(int distance, int risk, int time) {
        return distance * distanceWeight + risk * riskWeight + time * timeWeight;
    }

    private void logMission(int start, int end, Map<Integer, Integer> distances, Map<Integer, Integer> risks,
                            Map<Integer, Integer> times, Map<Integer, Integer> predecessors) {
        List<Integer> path = new ArrayList<>();
        Integer step = end;

        while (step != null) {
            path.add(step);
            step = predecessors.get(step);
        }
        Collections.reverse(path);

        StringBuilder missionReport = new StringBuilder();
        missionReport.append("Mission Log: Optimal route from ").append(start).append(" to ").append(end).append("\n");
        missionReport.append("Path: ").append(path).append("\n");
        missionReport.append("Total Distance: ").append(distances.get(end)).append("\n");
        missionReport.append("Total Risk Level: ").append(risks.get(end)).append("\n");
        missionReport.append("Total Estimated Time: ").append(times.get(end)).append("\n");

        System.out.println(missionReport.toString());
        missionHistory.add(missionReport.toString());
    }

    void printMissionHistory() {
        System.out.println("\n==== Mission History ====");
        for (String log : missionHistory) {
            System.out.println(log);
        }
    }

    public static void main(String[] args) {
        MilitaryRouteSimulator simulator = new MilitaryRouteSimulator();

        simulator.addRoute(1, 2, 10, 5, 8, "Forest");
        simulator.addRoute(2, 3, 15, 3, 6, "Mountain");
        simulator.addRoute(1, 3, 30, 1, 12, "Urban");
        simulator.addRoute(3, 4, 10, 4, 5, "River Crossing");

        simulator.setWeights(2, 1, 3);

        simulator.calculateOptimalRoute(1, 4);

        simulator.printMissionHistory();
    }
}
