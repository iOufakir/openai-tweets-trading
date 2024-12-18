package com.ilyo.openai.automation;

public interface AutomationService {

  void chooseTab();

  default void initialize() {
    chooseTab();
  }

}
