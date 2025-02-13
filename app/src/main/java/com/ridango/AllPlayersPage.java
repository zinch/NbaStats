package com.ridango;

import com.ridango.domain.Player;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

class AllPlayersPage {
    private static final String NBA_PLAYERS_SEARCH_URL_TEMPLATE
            = "https://www.nba.com/stats/players/traditional?CF=PLAYER_NAME*E*%s";
    private final WebDriver webDriver;

    public AllPlayersPage(WebDriver webDriver) {
        this.webDriver = Objects.requireNonNull(webDriver);
    }

    public Optional<Player> findPlayerByName(String playerName) {
        var urlSafePlayer = URLEncoder.encode(playerName, StandardCharsets.UTF_8);
        var playerSearchUrl = NBA_PLAYERS_SEARCH_URL_TEMPLATE.formatted(urlSafePlayer);
        webDriver.get(playerSearchUrl);

        return webDriver.findElements(new By.ByLinkText(playerName)).stream()
                .filter(AllPlayersPage::isAnchor)
                .map(el -> new Player(el.getText(), el.getAttribute("href")))
                .findFirst();
    }

    private static boolean isAnchor(WebElement el) {
        return "a".equalsIgnoreCase(el.getTagName());
    }
}
