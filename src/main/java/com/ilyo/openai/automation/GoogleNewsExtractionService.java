package com.ilyo.openai.automation;

import static com.ilyo.openai.core.utils.TextCleanerUtils.cleanText;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class GoogleNewsExtractionService implements AutomationService {

  public static final String GOOGLE_NEWS_SELECTOR = "main div:nth-child(2) > c-wiz article a[href^='./read/']";
  public static final String GOOGLE_ARTICLE_SELECTOR =
      "body #content-area, body .news-content, body .post-content, body div .main-content, body div .article-body, body div .article, body div article, body div .feed-layout-main, body div .layout-main, div[class*='main_content']";
  public static final String GOOGLE_CRYPTO_NEWS_URL = "https://news.google.com/search?q=\"%s\" crypto when:1d";
  public static final String GOOGLE_NEWS_URL = "https://news.google.com/search?q=%s when:1d";

  private final WebDriver webDriver;

  @SneakyThrows
  public List<String> extractGoogleNews(final String url, final int maxArticles) {
    initialize();
    webDriver.get(url);
    final var wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(GOOGLE_NEWS_SELECTOR)));

    final List<String> articlesUrls = webDriver.findElements(By.cssSelector(GOOGLE_NEWS_SELECTOR)).stream()
        .map(articleElement -> articleElement.getAttribute("href"))
        .toList();
    return extractTexts(maxArticles, articlesUrls, wait);
  }

  private List<String> extractTexts(int maxArticles, List<String> articlesUrls, WebDriverWait wait) {
    final List<String> articlesText = new ArrayList<>();
    for (var i = 0; i < maxArticles; i++) {
      final var articleUrl = articlesUrls.get(i);
      log.debug("Retrieved article from URL: {}", articleUrl);
      webDriver.navigate().to(articleUrl);
      try {
        WebElement articleBody = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(GOOGLE_ARTICLE_SELECTOR)));
        articlesText.add(cleanText(articleBody.getText()));
      } catch (WebDriverException e) {
        log.error("Can't retrieve article from URL: {} - {}", webDriver.getCurrentUrl(), e.getMessage());
      }
    }
    log.info("Retrieved articles: {}", articlesText.size());
    return articlesText;
  }

  @Override
  public void chooseTab() {
    Object[] windowHandles = webDriver.getWindowHandles().toArray();
    if (windowHandles.length == 2) {
      webDriver.switchTo().newWindow(WindowType.TAB);
    }

    // Switch to the last tab
    windowHandles = webDriver.getWindowHandles().toArray();
    webDriver.switchTo().window((String) windowHandles[2]);
  }

}
