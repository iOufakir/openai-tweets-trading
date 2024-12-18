package com.ilyo.openai.automation;

import static com.ilyo.openai.automation.utils.WebDriverHelperUtils.randomSleep;
import static com.ilyo.openai.automation.utils.WebDriverHelperUtils.scrollBy;

import java.security.SecureRandom;
import java.time.Duration;

import com.ilyo.openai.core.enums.PromptType;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class OpenAIAutomationService implements AutomationService {

  private static final String CHATGPT_DIV_SELECTOR = "#prompt-textarea";
  private static final String CHATGPT_BUTTON_SCROLL_TO_LATEST_SELECTOR = "button.cursor-pointer";
  private static final String CHATGPT_TWEETS_DISCUSSION_SELECTOR = "div[title='Investing using tweets']";
  private static final String CHATGPT_NEWS_DISCUSSION_SELECTOR = "div[title='Investing using news']";
  private static final String CHATGPT_SEND_BUTTON_SELECTOR = "button[data-testid='send-button']";
  private static final String CHATGPT_URL = "https://chatgpt.com";

  private final WebDriver webDriver;
  private final SecureRandom secureRandom;

  @SneakyThrows
  public void sendPrompt(final String prompt, final PromptType promptType) {
    initialize();
    // To react as human
    scrollBy(webDriver, secureRandom.nextInt(500, 800));
    randomSleep(5000, 10_000);

    final var wait = new WebDriverWait(webDriver, Duration.ofSeconds(secureRandom.nextInt(5, 12)));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(CHATGPT_DIV_SELECTOR)));
    // To react as human
    scrollBy(webDriver, secureRandom.nextInt(500, 801));
    final var discussion =
        webDriver.findElement(By.cssSelector(promptType == PromptType.TWEETS ? CHATGPT_TWEETS_DISCUSSION_SELECTOR : CHATGPT_NEWS_DISCUSSION_SELECTOR));
    discussion.click();

    writeAndSendPrompt(prompt);
  }

  private void writeAndSendPrompt(final String prompt) {
    log.info("[OpenAI] Prompt to be sent: {}", prompt);

    // To react as human
    scrollBy(webDriver, secureRandom.nextInt(100, 400));
    randomSleep(2000, 3500);
    scrollToLatest();
    final var searchElement = webDriver.findElement(By.cssSelector(CHATGPT_DIV_SELECTOR));
    searchElement.click();
    randomSleep(500, 1500);

    final var jsDriver = (JavascriptExecutor) webDriver;
    final var htmlPromptValue = prompt.replace("\n", "<p><br class=\"ProseMirror-trailingBreak\"/></p>");
    jsDriver.executeScript("arguments[0].innerHTML = arguments[1]", searchElement, htmlPromptValue);
    randomSleep(2400, 4400);
    webDriver.findElement(By.cssSelector(CHATGPT_SEND_BUTTON_SELECTOR)).click();
    switchToFirstTab();
  }

  private void scrollToLatest() {
    try{
      final var scrollToLatestButton = webDriver.findElement(By.cssSelector(CHATGPT_BUTTON_SCROLL_TO_LATEST_SELECTOR));
      scrollToLatestButton.click();
    } catch(ElementNotInteractableException e){
      log.warn("[OpenAI] Element is not interactable: {}", e.getMessage());
    }
  }

  @Override
  public void chooseTab() {
    Object[] windowHandles = webDriver.getWindowHandles().toArray();
    if (windowHandles.length == 1) {
      webDriver.switchTo().newWindow(WindowType.TAB);
      webDriver.get(CHATGPT_URL);
    } else {
      // Switch to the last tab
      windowHandles = webDriver.getWindowHandles().toArray();
      webDriver.switchTo().window((String) windowHandles[1]);
      webDriver.navigate().refresh();
    }
  }

  private void switchToFirstTab() {
    // Wait for the response before switching to another tab
    randomSleep(20_000, 40_000);

    final Object[] windowHandles = webDriver.getWindowHandles().toArray();
    webDriver.switchTo().window((String) windowHandles[0]);
  }
}
