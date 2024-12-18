package com.ilyo.openai.automation;

import static com.ilyo.openai.automation.utils.WebDriverHelperUtils.scrollBy;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.ilyo.openai.external.twitter.config.TwitterConfig;
import com.ilyo.openai.external.twitter.dto.Tweet;
import com.ilyo.openai.external.twitter.service.TwitterService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class TwitterExtractionService implements AutomationService {

  private static final String AUTHORIZE_BUTTON_SELECTOR = "button[data-testid='OAuth_Consent_Button']";
  private static final String TWITTER_TIMELINE_SELECTOR = "div[aria-label^='Timeline']";
  private static final String TWEET_SELECTOR = "[data-testid='tweet']";
  private static final String TWEET_TEXT_SELECTOR = "div[data-testid='tweetText']";
  private static final String TWEET_AUTHOR_SELECTOR = "div[data-testid='User-Name'] div:nth-child(2) div a div";
  private static final String TWITTER_SEARCH_URL = "https://x.com/search?q=%s&src=typed_query&f=live";
  private static final String TWITTER_KEYWORD_QUERY = "%s OR economy OR crypto OR bitcoin";
  private static final int VALID_TWEET_MIN_LENGTH = 15;

  private final WebDriver webDriver;
  private final TwitterConfig twitterConfig;

  @SneakyThrows
  public String extractTwitterOAuth2Code(final String url, final String callbackUrl) {
    initialize();
    webDriver.get(url);

    WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(12));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(AUTHORIZE_BUTTON_SELECTOR)));
    WebElement button = webDriver.findElement(By.cssSelector(AUTHORIZE_BUTTON_SELECTOR));
    button.click();

    // wait for the redirect
    wait.until(ExpectedConditions.urlContains(callbackUrl));
    wait.until(ExpectedConditions.urlContains("code="));
    return webDriver.getCurrentUrl().split("code=")[1];
  }

  public List<Tweet> extractTweets(final String token, final boolean withSpecificInfluencers) {
    loadSearchPage(token, withSpecificInfluencers);

    WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(20));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(TWITTER_TIMELINE_SELECTOR)));
    scrollBy(webDriver, 1000);

    final var tweetElements = findTweets();
    final List<Tweet> tweets = new ArrayList<>();
    for (final var tweetElement : tweetElements) {
      try {
        final var tweetAuthor = tweetElement.findElement(By.cssSelector(TWEET_AUTHOR_SELECTOR)).getText();
        final var tweetText = tweetElement.findElement(By.cssSelector(TWEET_TEXT_SELECTOR)).getText();
        if (tweetText.length() >= VALID_TWEET_MIN_LENGTH) {
          log.info("Retrieved tweet from: {}", tweetAuthor);
          log.debug("{}", tweetText);
          tweets.add(Tweet.builder().authorId(tweetAuthor).text(tweetText).build());
        }
      } catch (NoSuchElementException exception) {
        log.warn("Element missing in X timeline: {}", exception.getMessage());
      }
    }
    return tweets;
  }

  private List<WebElement> findTweets() {
    final var tweetElements = webDriver.findElement(By.cssSelector(TWITTER_TIMELINE_SELECTOR)).findElements(By.cssSelector(TWEET_SELECTOR));
    log.info("Number of tweets found {}", tweetElements.size());
    return tweetElements;
  }

  private void loadSearchPage(final String token, final boolean withSpecificInfluencers) {
    final var query = "(%s) (%s)".formatted(TWITTER_KEYWORD_QUERY.formatted(token), TwitterService.buildInfluencersQuery(twitterConfig.influencersList()));
    webDriver.get(withSpecificInfluencers ? TWITTER_SEARCH_URL.formatted(query) : TWITTER_SEARCH_URL.formatted(token));
  }

  @Override
  public void chooseTab() {
    // Always the first tab
    webDriver.switchTo().window(webDriver.getWindowHandles().iterator().next());
  }

}
