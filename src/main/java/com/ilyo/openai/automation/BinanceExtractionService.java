package com.ilyo.openai.automation;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.ilyo.openai.automation.dto.FearGreedIndex;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class BinanceExtractionService implements AutomationService {

  private static final String BINANCE_FEAR_GREED_SELECTOR = "body div .feed-layout-main > div > div:nth-child(2) > div";
  private static final String BINANCE_FEAR_GREED_INDEX_URL = "https://www.binance.com/en/square/fear-and-greed-index";

  private final WebDriver webDriver;

  @SneakyThrows
  public FearGreedIndex getFearGreedIndex() {
    initialize();
    webDriver.get(BINANCE_FEAR_GREED_INDEX_URL);

    final var wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(BINANCE_FEAR_GREED_SELECTOR)));

    final var text = webDriver.findElement(By.cssSelector(BINANCE_FEAR_GREED_SELECTOR)).getText();
    final var indexNumbers = extractIndexNumbers(text);
    if (indexNumbers.size() == 3) {
      return new FearGreedIndex(indexNumbers.get(0), indexNumbers.get(1), indexNumbers.get(2));
    }
    throw new IllegalStateException("Invalid index numbers: %s".formatted(indexNumbers));
  }

  private static List<Integer> extractIndexNumbers(String text) {
    final List<Integer> numbers = new ArrayList<>();
    final var matcher = Pattern.compile("\\d+").matcher(text);
    while (matcher.find()) {
      numbers.add(Integer.valueOf(matcher.group()));
    }
    return numbers;
  }

  @Override
  public void chooseTab() {
    // Always the first tab
    webDriver.switchTo().window(webDriver.getWindowHandles().iterator().next());
  }
}
