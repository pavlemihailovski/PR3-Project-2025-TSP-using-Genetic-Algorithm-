import java.util.*;
import java.lang.Math;

public class TSPGeneticAlgorithm {
    private static final int POPULATION_SIZE = 100;
    private static final int MAX_GENERATIONS = 500;
    private static final double MUTATION_RATE = 0.1;
    private static final double CROSSOVER_RATE = 0.8;
    private static List<String> cities;
    private static double[][] distanceMatrix;

    // List of cities and their latitudes and longitudes
    private static final List<City> allCities = Arrays.asList(
            new City("New York", 40.7128, -74.0060),
            new City("London", 51.5074, -0.1278),
            new City("Tokyo", 35.6762, 139.6503),
            new City("Paris", 48.8566, 2.3522),
            new City("Berlin", 52.5200, 13.4050),
            new City("Sydney", -33.8688, 151.2093),
            new City("Los Angeles", 34.0522, -118.2437),
            new City("Rome", 41.9028, 12.4964),
            new City("Beijing", 39.9042, 116.4074),
            new City("Moscow", 55.7558, 37.6173),
            new City("Ljubljana", 46.0511, 14.5051),
            new City("Koper", 45.5501, 13.7304),
            new City("Piran", 45.5277, 13.5723),
            new City("Izola", 45.5440, 13.6551),
            new City("Trieste", 45.6495, 13.7768),
            new City("Zagreb", 45.8131, 15.978),
            new City("Pula", 44.8686, 13.8486),
            new City("Dubrovnik", 42.6507, 18.0944),
            new City("Prague", 50.0755, 14.4378),
            new City("Vienna", 48.2082, 16.3738),
            new City("Barcelona", 41.3851, 2.1734),
            new City("Skopje", 41.9981, 21.4254),
            new City("Sofia", 42.6977, 23.3219),
            new City("Sarajevo", 43.8486, 18.3564),
            new City("Belgrade", 44.8176, 20.4633),
            new City("Maribor", 46.5547, 15.6450)
    );

    public static void main(String[] args) {
        // Input city names and distance matrix
        inputCitiesAndDistances();

        // Initialize population
        List<int[]> population = initializePopulation();

        // Genetic Algorithm
        int[] bestRoute = null;
        double bestDistance = Double.MAX_VALUE;

        long startTime = System.nanoTime();
        for (int generation = 0; generation < MAX_GENERATIONS; generation++) {
            population = evolvePopulation(population);
            int[] currentBest = getFittest(population);
            double currentDistance = calculateRouteDistance(currentBest);

            if (currentDistance < bestDistance) {
                bestDistance = currentDistance;
                bestRoute = currentBest;
            }

            System.out.println("Generation " + generation + ": Best Distance = " + bestDistance);
        }
        long endTime = System.nanoTime();
        System.out.println("Execution Time: " + (endTime - startTime) / 1e6 + " ms");

        // Output the best solution
        System.out.println("Best Route Found:");
        printRoute(bestRoute);
        System.out.println("Distance: " + Math.round(bestDistance) + " km");
    }

    private static void inputCitiesAndDistances() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of cities you want to visit (Max " + allCities.size() + "):");
        int numCities = scanner.nextInt();
        scanner.nextLine();  // Consume the newline

        // Validate number of cities
        if (numCities < 2 || numCities > allCities.size()) {
            System.out.println("Invalid number of cities. Please enter between 2 and " + allCities.size());
            return;
        }

        cities = new ArrayList<>();
        System.out.println("Enter the names of the cities (Available cities: " + allCities.stream().map(city -> city.name).toList() + "):");

        for (int i = 0; i < numCities; i++) {
            System.out.print("City " + (i + 1) + ": ");
            String cityName = scanner.nextLine().trim();
            if (isCityValid(cityName)) {
                cities.add(cityName);
            } else {
                System.out.println("Invalid city name entered. Please choose from the available cities.");
                i--; // Re-prompt for the current city
            }
        }

        // Generate the distance matrix based on the selected cities ; It is better to be generated than to be manually entered
        generateDistanceMatrix(numCities);
    }

    private static boolean isCityValid(String cityName) {
        for (City city : allCities) {
            if (city.name.equalsIgnoreCase(cityName)) {
                return true;
            }
        }
        return false;
    }

    private static void generateDistanceMatrix(int numCities) {
        distanceMatrix = new double[numCities][numCities];

        for (int i = 0; i < numCities; i++) {
            for (int j = 0; j < numCities; j++) {
                if (i != j) {
                    double distance = haversineDistance(allCities.get(i).latitude, allCities.get(i).longitude,
                            allCities.get(j).latitude, allCities.get(j).longitude);
                    distanceMatrix[i][j] = Math.round(distance * 100.0) / 100.0; // Round to 2 decimal places
                } else {
                    distanceMatrix[i][j] = 0; // Distance to itself is zero
                }
            }
        }
    }

    // Using the Haversine Distance formula for a "better real data" results
    private static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    private static List<int[]> initializePopulation() {
        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            population.add(generateRandomRoute());
        }
        return population;
    }

    private static int[] generateRandomRoute() {
        int[] route = new int[cities.size()];
        for (int i = 0; i < cities.size(); i++) {
            route[i] = i;
        }
        shuffleArray(route);
        return route;
    }

    private static void shuffleArray(int[] array) {
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }

    private static List<int[]> evolvePopulation(List<int[]> population) {
        List<int[]> newPopulation = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < population.size(); i++) {
            int[] parent1 = selectParent(population);
            int[] parent2 = selectParent(population);

            int[] offspring;
            if (random.nextDouble() < CROSSOVER_RATE) {
                offspring = crossover(parent1, parent2);
            } else {
                offspring = parent1.clone();
            }

            if (random.nextDouble() < MUTATION_RATE) {
                mutate(offspring);
            }

            newPopulation.add(offspring);
        }

        return newPopulation;
    }

    private static int[] selectParent(List<int[]> population) {
        Random random = new Random();
        int tournamentSize = 5;
        int[] best = null;
        double bestFitness = Double.MAX_VALUE;

        for (int i = 0; i < tournamentSize; i++) {
            int[] individual = population.get(random.nextInt(population.size()));
            double fitness = calculateRouteDistance(individual);
            if (fitness < bestFitness) {
                bestFitness = fitness;
                best = individual;
            }
        }
        return best;
    }

    private static int[] crossover(int[] parent1, int[] parent2) {
        Random random = new Random();
        int[] offspring = new int[parent1.length];
        Arrays.fill(offspring, -1);

        int start = random.nextInt(parent1.length);
        int end = random.nextInt(parent1.length);

        for (int i = Math.min(start, end); i < Math.max(start, end); i++) {
            offspring[i] = parent1[i];
        }

        for (int i = 0; i < parent2.length; i++) {
            if (!contains(offspring, parent2[i])) {
                for (int j = 0; j < offspring.length; j++) {
                    if (offspring[j] == -1) {
                        offspring[j] = parent2[i];
                        break;
                    }
                }
            }
        }

        return offspring;
    }

    private static boolean contains(int[] array, int value) {
        for (int i : array) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }

    private static void mutate(int[] route) {
        Random random = new Random();
        int index1 = random.nextInt(route.length);
        int index2 = random.nextInt(route.length);

        int temp = route[index1];
        route[index1] = route[index2];
        route[index2] = temp;
    }

    private static double calculateRouteDistance(int[] route) {
        double totalDistance = 0;
        for (int i = 0; i < route.length - 1; i++) {
            totalDistance += distanceMatrix[route[i]][route[i + 1]];
        }
        totalDistance += distanceMatrix[route[route.length - 1]][route[0]]; // Return to start
        return totalDistance;
    }

    private static int[] getFittest(List<int[]> population) {
        int[] fittest = population.get(0);
        double bestDistance = calculateRouteDistance(fittest);
        for (int[] individual : population) {
            double distance = calculateRouteDistance(individual);
            if (distance < bestDistance) {
                fittest = individual;
                bestDistance = distance;
            }
        }
        return fittest;
    }

    private static void printRoute(int[] route) {
        for (int i : route) {
            System.out.print(cities.get(i) + " -> ");
        }
        System.out.println(cities.get(route[0]));
    }

    static class City {
        String name;
        double latitude;
        double longitude;

        City(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}








