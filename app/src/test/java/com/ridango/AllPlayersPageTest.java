package com.ridango;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ridango.domain.Player;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class AllPlayersPageTest {
    @Mock private WebDriver webDriver;
    @Captor private ArgumentCaptor<By> matcherCaptor;

    @Test
    public void should_return_multiple_urls_if_search_matches_multiple_players() {
        // given
        var playerStatisticsPage = new AllPlayersPage(webDriver);

        var firstEl = mockAnchor("Luka Doncic", "https://www.nba.com/stats/player/1629029/");
        var secondEl = mockAnchor("Luka Garza", "https://www.nba.com/stats/player/1630568/");
        when(webDriver.findElements(matcherCaptor.capture()))
                .thenReturn(List.of(firstEl, secondEl));

        // when
        var maybePlayer = playerStatisticsPage.findPlayerByName("Luka");

        // then
        verify(webDriver, times(1)).get("https://www.nba.com/stats/players/traditional?CF=PLAYER_NAME*E*Luka");
        assertThat(maybePlayer).isPresent();
        assertThat(maybePlayer.orElseThrow()).isEqualTo(new Player("Luka Doncic", "https://www.nba.com/stats/player/1629029/"));

        var matcher = matcherCaptor.getValue();
        assertThat(matcher).isInstanceOf(By.ByLinkText.class);
        assertThat(((By.ByLinkText) matcher).getRemoteParameters()).asString()
                .isEqualTo("[link text: Luka]");
    }

    private WebElement mockAnchor(String playerName, String href) {
        var el = mock(WebElement.class);
        when(el.getAttribute("href")).thenReturn(href);
        when(el.getText()).thenReturn(playerName);
        when(el.getTagName()).thenReturn("a");
        return el;
    }

}