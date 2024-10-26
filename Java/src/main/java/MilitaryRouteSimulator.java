import java.util.*;

class Node implements Comparable<Node> {
    int id;
    int distance;
    int risk;

    Node(int id, int distance, int risk) {
        this.id = id;
        this.distance = distance;
        this.risk = risk;
    }

    @Override
    public int compareTo(Node other) {
        return Integer.compare(this.distance + this.risk, other.distance + other.risk);
    }
}

class MilitaryRouteSimulator {
    private Map<Integer, List<Node>> map;

    MilitaryRouteSimulator() {
        map = new HashMap<>();
    }

    void addRoute(int source, int destination, int distance, int risk) {
        map.computeIfAbsent(source, k -> new ArrayList<>()).add(new Node(destination, distance, risk));
        map.computeIfAbsent(destination, k -> new ArrayList<>()).add(new Node(source, distance, risk));
    }

    void calculateOptimalRoute(int start, int end) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        Map<Integer, Integer> distances = new HashMap<>();
        Map<Integer, Integer> risks = new HashMap<>();
        Set<Integer> visited = new HashSet<>();
        
        pq.add(new Node(start, 0, 0));
        distances.put(start, 0);
        risks.put(start, 0);

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current.id)) continue;
            visited.add(current.id);

            if (current.id == end) {
                System.out.println("Route found with distance: " + distances.get(current.id) +
                                   " and risk level: " + risks.get(current.id));
                return;
            }

            for (Node neighbor : map.getOrDefault(current.id, Collections.emptyList())) {
                int newDist = distances.get(current.id) + neighbor.distance;
                int newRisk = risks.get(current.id) + neighbor.risk;

                if (!distances.containsKey(neighbor.id) || newDist < distances.get(neighbor.id) ||
                        (newDist == distances.get(neighbor.id) && newRisk < risks.get(neighbor.id))) {
                    distances.put(neighbor.id, newDist);
                    risks.put(neighbor.id, newRisk);
                    pq.add(new Node(neighbor.id, newDist, newRisk));
                }
            }
        }
        System.out.println("No route found.");
    }

    public static void main(String[] args) {
        MilitaryRouteSimulator simulator = new MilitaryRouteSimulator();
        
        // Adicionando rotas com distâncias e níveis de risco
        simulator.addRoute(1, 2, 10, 5);
        simulator.addRoute(2, 3, 15, 3);
        simulator.addRoute(1, 3, 30, 1);
        simulator.addRoute(3, 4, 10, 4);

        simulator.calculateOptimalRoute(1, 4);
    }
}
