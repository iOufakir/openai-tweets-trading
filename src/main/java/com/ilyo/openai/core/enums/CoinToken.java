package com.ilyo.openai.core.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoinToken {

  POL("polygon-ecosystem-token", "polygon");

  private final String id;
  private final String fullName;
}
