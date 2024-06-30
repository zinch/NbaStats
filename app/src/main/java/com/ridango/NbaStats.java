package com.ridango;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

class NbaStats {
    public static void main(String[] args) {
        if (args.length == 0) {
            usage();
            System.exit(1);
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        var webDriver = new ChromeDriver(options);

        for (String player : args) {
            tryFetchAndPrintStatsFor(webDriver, player, "3PA");
        }
        webDriver.quit();
    }

    private static void tryFetchAndPrintStatsFor(
            ChromeDriver webDriver, String player, String stats)
    {
        try {
            PlayersStatisticsPage playersStatisticsPage = new PlayersStatisticsPage(webDriver);
            var urls = playersStatisticsPage.findStatisticsLinksMatching(player);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static void usage() {
        System.out.println("""
                This program accepts a list of NBA players and prints statistics for each of them

                Usage:
                    java NbaStats.java 'Luca Doncic'""");
    }
}
