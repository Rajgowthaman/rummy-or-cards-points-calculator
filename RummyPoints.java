import java.util.*;

class Player {
    String name;
    int points;

    public Player(String name, int points) {
        this.name = name;
        this.points = points;
    }

    public void updatePoints(int points) {
        this.points += points;
    }
}

public class RummyPoints {
    // ANSI escape codes for yellow color
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        clearConsole(); // Clear the console

        Scanner scanner = new Scanner(System.in);
        List<Player> players = new ArrayList<>();
        List<String> playerNames = new ArrayList<>(); // Store player names separately

        int numPlayers = 0;
        while (numPlayers < 2) {
            System.out.println("Enter the number of players (at least 2):");
            numPlayers = scanner.nextInt();
            if (numPlayers < 2) {
                System.out.println("Invalid input. Please enter at least 2 players.");
            }
        }
        scanner.nextLine(); // Consume newline

        // Input player names and initial points
        for (int i = 0; i < numPlayers; i++) {
            String playerName = "";
            while (playerName.trim().isEmpty() || !playerName.matches("^[a-zA-Z0-9]+$")) {
                System.out.print("Enter name for player " + (i + 1) + ": ");
                playerName = scanner.nextLine().trim();
                if (playerName.isEmpty()) {
                    System.out.println("Player name cannot be blank. Please enter a valid name.");
                } else if (!playerName.matches("^[a-zA-Z0-9]+$")) {
                    System.out.println("Player name must be alphanumeric. Please enter a valid name.");
                } else {
                    playerName = formatPlayerName(playerName); // Format player name
                }
            }
            players.add(new Player(playerName, 0));
            playerNames.add(playerName); // Store name in playerNames list
        }

        // Round loop
        int round = 1;
        while (true) {
            System.out.println(YELLOW + "\nRound " + round + RESET); // Yellow color for round number

            // Prompt for points for each player in the same order as their names were entered
            for (String playerName : playerNames) {
                boolean pointsEntered = false;
                while (!pointsEntered) {
                    try {
                        System.out.print("Enter points for " + playerName + ": ");
                        int points = scanner.nextInt();
                        if (points < 0) {
                            System.out.println("Negative points are not allowed. Please enter a non-negative integer.");
                            continue; // Ask for input again
                        }
                        Player currentPlayer = getPlayerByNameObject(players, playerName);
                        currentPlayer.updatePoints(points);
                        pointsEntered = true;
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter points as integers.");
                        scanner.nextLine(); // Clear the invalid input
                    }
                }
            }

            System.out.println("\nLeaderboard after Round " + round + ":");
            displayLeaderboard(players);

            // Prompt for next round or exit
            while (true) {
                try {
                    System.out.println("\nPress 1 to go to the next round, or 2 to exit:");
                    int choice = scanner.nextInt();
                    if (choice == 2) {
                        System.out.println("\nFinal Leaderboard:");
                        displayLeaderboard(players);
                        scanner.close();
                        return;
                    } else if (choice == 1) {
                        round++;
                        System.out.println(); // Line break
                        break;
                    } else {
                        System.out.println("Invalid choice. Please enter 1 or 2.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter 1 or 2.");
                    scanner.nextLine(); // Clear the invalid input
                }
            }
        }
    }

    private static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            // Handle exceptions
        }
    }

    private static void displayLeaderboard(List<Player> players) {
        // Sort players by points (ascending) and then by name (ascending)
        players.sort((p1, p2) -> {
            if (p1.points == p2.points) {
                return p1.name.compareToIgnoreCase(p2.name); // Sort alphabetically if points are tied
            } else {
                return Integer.compare(p1.points, p2.points); // Sort by points in ascending order
            }
        });

        // Calculate maximum length of player names
        int maxNameLength = players.stream().mapToInt(p -> formatPlayerName(p.name).length()).max().orElse(0);

        // Display the leaderboard
        System.out.println("Rank\tPlayer" + " ".repeat(Math.max(0, maxNameLength - 6)) + "\tPoints");
        int rank = 1;
        int previousPoints = Integer.MIN_VALUE;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.points != previousPoints) {
                rank = i + 1; // Update rank for new points
                System.out.println(getRankIndicator(rank, previousPoints, player.points) + "\t" + formatPlayerName(player.name) + " ".repeat(Math.max(0, maxNameLength - formatPlayerName(player.name).length())) + "\t" + player.points);
            } else {
                System.out.println(getRankIndicator(rank, previousPoints, player.points) + "\t" + formatPlayerName(player.name) + " ".repeat(Math.max(0, maxNameLength - formatPlayerName(player.name).length())) + "\t" + player.points);
            }
            previousPoints = player.points;
        }
    }

    private static String formatPlayerName(String name) {
        // Split the name by space and convert first letter of each word to uppercase, and the rest to lowercase
        String[] words = name.trim().split("\\s+");
        StringBuilder formattedName = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            formattedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1).toLowerCase());
            if (i < words.length - 1) {
                formattedName.append(" "); // Add space between words
            }
        }
        return formattedName.toString();
    }

    private static String getRankIndicator(int currentRank, int previousPoints, int currentPoints) {
        if (currentPoints != previousPoints) {
            return Integer.toString(currentRank);
        } else {
            return "=";
        }
    }

    private static Player getPlayerByNameObject(List<Player> players, String name) {
        for (Player player : players) {
            if (player.name.equalsIgnoreCase(name)) {
                return player;
            }
        }
        return null; // Player not found
    }
}