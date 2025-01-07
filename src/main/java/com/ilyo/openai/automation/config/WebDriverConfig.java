package com.ilyo.openai.automation.config;

import java.security.SecureRandom;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverConfig {


  @Bean
  public WebDriver webDriver() {
    return new FirefoxDriver(buildLocalFirefoxOptions());
  }

  @Bean
  public SecureRandom secureRandom() {
    return new SecureRandom();
  }

  private FirefoxOptions buildLocalFirefoxOptions() {
    final var options = new FirefoxOptions();
    options.addPreference("dom.webdriver.enabled", false); // Hide WebDriver
    options.addPreference("useAutomationExtension", false); // Disable automation extension
    options.addPreference("media.navigator.permission.disabled", true); // Disable permission prompts

    options.addArguments("--headless");
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.addArguments("--start-maximized");
    options.addArguments("--enable-javascript");
    options.addArguments("--profile", "/Users/ilyasdev/Library/Application Support/Firefox/Profiles/ja0iu3ci.default-release");
    return options;
  }

}
