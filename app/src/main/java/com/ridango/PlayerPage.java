package com.ridango;

import com.ridango.domain.Player;
import com.ridango.domain.Result;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

class PlayerPage {
    private static final String TRADITIONAL_SPLITS = "Traditional Splits";
    private final WebDriver webDriver;
    private final Player player;

    public PlayerPage(WebDriver webDriver, Player player) {
        this.webDriver = Objects.requireNonNull(webDriver);
        this.player = Objects.requireNonNull(player);
    }

    public List<Result> fetchStats(String stats) {
        webDriver.get(player.statsUrl() + "?PerMode=Per40&SeasonType=Regular+Season");
        var tableHeaderXPath = "//th[text()='" + TRADITIONAL_SPLITS + "']";
        var header = webDriver.findElement(By.xpath(tableHeaderXPath));

        var parentTable = findParentTable(header);

        if (parentTable == null) {
            System.err.println("Unable to find table for " + TRADITIONAL_SPLITS);
            return List.of();
        }

        var season = parentTable.findElement(By.xpath("//th[text()='By Year']"));
        if (season == null) {
            System.err.println("Cannot find seasons in " + TRADITIONAL_SPLITS + " table");
            return List.of();
        }
        var stat = parentTable.findElement(By.xpath("//th[text()='%s']".formatted(stats)));
        if (stat == null) {
            System.err.println("Cannot find " + stats + " in " + TRADITIONAL_SPLITS + " table");
            return List.of();
        }

        var seasonIdx = (int) findColumnIdx(season);
        var statIdx = (int) findColumnIdx(stat);

        return parentTable.findElements(new By.ByCssSelector("tbody > tr")).stream()
                .map(el -> {
                    var columns = el.findElements(new By.ByCssSelector("td"));
                    var seasonName = columns.get(seasonIdx).getText();
                    var value = Double.parseDouble(columns.get(statIdx).getText());
                    return new Result(seasonName, value);
                })
                .sorted(Comparator.comparing(Result::season))
                .toList();
    }

    WebElement findParentTable(WebElement element) {
        var parentTable = (WebElement) ((JavascriptExecutor) webDriver).executeScript(
                "return arguments[0].parentNode;", element);
        while (parentTable != null) {
            if ("table".equalsIgnoreCase(parentTable.getTagName())) {
                return parentTable;
            } else {
                parentTable = findParentTable(parentTable);
            }
        }
        return null;
    }

    long findColumnIdx(WebElement element) {
        return (long) ((JavascriptExecutor) webDriver).executeScript(
                "return [...arguments[0].parentNode.childNodes].indexOf(arguments[0]);", element);
    }
}
