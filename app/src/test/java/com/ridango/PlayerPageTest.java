package com.ridango;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ridango.domain.Player;
import com.ridango.domain.Result;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
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
public class PlayerPageTest {
    @Mock
    private WebDriver webDriver;

    @Mock
    private WebElement statsTable;

    @Captor
    private ArgumentCaptor<By> matcherCaptor;

    private PlayerPage playerPage;

    @Before
    public void setUp() {
        playerPage = new PlayerPage(webDriver, new Player("Alice Doe", "https://www.nba.com/stats/player/123456/")) {
            private long columnIdx = 0;

            @Override
            WebElement findParentTable(WebElement element) {
                return statsTable;
            }

            @Override
            long findColumnIdx(WebElement element) {
                return columnIdx++;
            }
        };
    }

    @Test
    public void should_open_players_page() {
        // when
        playerPage.fetchStats("3PA");

        // then
        verify(webDriver, times(1)).get(
                "https://www.nba.com/stats/player/123456/?PerMode=Per40&SeasonType=Regular+Season");
    }

    @Test
    public void should_extract_required_statistics() {
        // given
        when(statsTable.findElement(By.xpath("//th[text()='By Year']")))
                .thenReturn(mock(WebElement.class));

        when(statsTable.findElement(By.xpath("//th[text()='3PA']")))
                .thenReturn(mock(WebElement.class));

        var secondRow = mockRow("2023-24", 11.3);
        var firstRow = mockRow("2022-23", 9.1);
        when(statsTable.findElements(matcherCaptor.capture()))
                .thenReturn(List.of(firstRow, secondRow));

        // when
        var stats = playerPage.fetchStats("3PA");

        // then
        assertThat(stats).hasSize(2);
        assertThat(stats).extracting(Result::season, Result::value)
                .containsExactly(Tuple.tuple("2022-23", 9.1), Tuple.tuple("2023-24", 11.3));

    }

    private WebElement mockRow(String season, double value) {
        var row = mock(WebElement.class);

        var seasonCol = mock(WebElement.class);
        when(seasonCol.getText()).thenReturn(season);
        var valueCol = mock(WebElement.class);
        when(valueCol.getText()).thenReturn(String.valueOf(value));

        when(row.findElements(new By.ByCssSelector("td")))
                .thenReturn(List.of(seasonCol, valueCol));

        return row;
    }
}