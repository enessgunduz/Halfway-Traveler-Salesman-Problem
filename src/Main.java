import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.*;

class City {
    int id;
    int x;
    int y;

    City(int id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}

class Main {
    private static List<City> cities;

    public static void main(String[] args) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        System.out.println(formatter.format(date));

        String inputFile = "input.txt";
        String outputFile = "output.txt";

        cities = readInputFile(inputFile);
        List<Integer> tour = findHalfTour(cities);
        writeOutputFile(outputFile, tour);

        Date date2 = new Date();
        System.out.println("\n"+formatter.format(date2));
    }

    private static List<City> readInputFile(String inputFile) {
        List<City> cities = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(inputFile))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim(); // Trim leading and trailing whitespace
                String[] parts = line.split("\\s+"); // Split using one or more whitespace characters
                int id = Integer.parseInt(parts[0]);
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                cities.add(new City(id, x, y));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return cities;
    }



    private static List<Integer> findHalfTour(List<City> cities) {
        int n = cities.size();
        int halfN = (int) Math.ceil(n / 2.0);

        // Calculate distances between all cities
        int[][] distances = new int[n][n];
        for (int i = 0; i < n; i++) {
            City city1 = cities.get(i);
            for (int j = 0; j < n; j++) {
                City city2 = cities.get(j);
                distances[i][j] = calculateDistance(city1, city2);
            }
        }

        // Sort cities based on their IDs
        cities.sort(Comparator.comparingInt(c -> c.id));

        // Start from the first city
        City startCity = cities.get(0);
        List<Integer> tour = new ArrayList<>();
        tour.add(startCity.id);

        // Choose the next closest city until we have visited half of the cities
        City currCity = startCity;
        for (int i = 0; i < halfN - 1; i++) {
            if (i%1000==0)
                System.out.print("-");
            int minDistance = Integer.MAX_VALUE;
            City nextCity = null;

            for (City city : cities) {
                if (tour.contains(city.id)) {
                    continue;
                }

                int distance = distances[currCity.id][city.id];
                if (distance < minDistance) {
                    minDistance = distance;
                    nextCity = city;
                }
            }

            if (nextCity != null) {
                tour.add(nextCity.id);
                currCity = nextCity;
            }
        }

        // Return to the start city
        tour.add(startCity.id);

        return tour;
    }

    private static int calculateDistance(City city1, City city2) {
        double distance = Math.sqrt(Math.pow(city1.x - city2.x, 2) + Math.pow(city1.y - city2.y, 2));
        return (int) Math.round(distance);
    }

    private static void writeOutputFile(String outputFile, List<Integer> tour) {
        try (FileWriter writer = new FileWriter(outputFile)) {
            int totalDistance = 0;
            for (int i = 1; i < tour.size(); i++) {
                int city1Id = tour.get(i - 1);
                int city2Id = tour.get(i);
                City city1 = findCityById(city1Id);
                City city2 = findCityById(city2Id);
                int distance = calculateDistance(city1, city2);
                totalDistance += distance;
            }

            writer.write(totalDistance + "\n");
            for (int i = 0; i < tour.size()-1; i++) {
                writer.write(tour.get(i) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static City findCityById(int id) {
        for (City city : cities) {
            if (city.id == id) {
                return city;
            }
        }
        return null;
    }
}
