package com.ridango;

import com.ridango.domain.Result;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

class NbaStats {
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }

        var webDriver = initWebDriver();

        for (String player : args) {
            tryFetchAndPrintStatsFor(webDriver, player, "3PA");
        }
        webDriver.quit();
    }

    private static ChromeDriver initWebDriver() {
        var webDriver = new ChromeDriver();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        return webDriver;
    }

    private static void tryFetchAndPrintStatsFor(ChromeDriver webDriver, String playerSearch, String stats) {
        try {
            var allPlayersPage = new AllPlayersPage(webDriver);
            var players = allPlayersPage.findStatisticsLinksMatching(playerSearch);
            if (players.size() == 1) {
                print(new PlayerPage(webDriver, players.get(0)).fetchStats(stats));
            } else {
                for (var player : players) {
                    System.out.println(player);
                    print(new PlayerPage(webDriver, player).fetchStats(stats));
                    System.out.println();
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void print(List<Result> results) {
        for (var result : results) {
            System.out.println(result.season() + " " + result.value());
        }
    }

    private static void usage() {
        System.out.println("""
                This program accepts a list of NBA players and prints statistics for each of them

                Usage:
                    java NbaStats.java 'Luca Doncic'""");
    }
}
