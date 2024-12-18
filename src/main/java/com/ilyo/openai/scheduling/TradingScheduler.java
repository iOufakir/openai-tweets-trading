package com.ilyo.openai.scheduling;

import java.time.LocalDateTime;

import com.ilyo.openai.core.enums.CoinToken;
import com.ilyo.openai.trading.service.TradingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Slf4j
public class TradingScheduler {

  private final TradingService tradingService;

  @Scheduled(cron = "${app.schedule.trading-crypto-cron-expression}")
  public void startAppScheduler() {
    log.info("[Auto trading] Current execution time: {}", LocalDateTime.now());
    tradingService.startCryptoTrading(CoinToken.ETH);
  }

}
