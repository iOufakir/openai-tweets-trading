package com.ilyo.openai.core.utils;

public class OpenAIUtils {

  public static final int OPENAI_TWITTER_MAX_TOKENS = 64;
  public static final int OPENAI_LONG_TEXT_MAX_TOKENS = 3000;
  public static final float OPENAI_TEMPERATURE = 0.9f;
  public static final float OPENAI_FOCUSED_TEMPERATURE = 0.2f;

  public static final String OPENAI_PROMPT_IF_SHOULD_PROMOTE_PRODUCT = """
      Only Answer with 'YES' if you think It's a good idea to add a promoting tweet about '%s' product for the following tweet, otherwise answer with 'NO'.
      
      Here is the tweet: %s.
      """;

  public static final String OPENAI_PROMPT_WRITE_TWEET = "Write a tweet as a real Human based for this tweet: %s";

  public static final String OPENAI_PROMPT_REPLY_TO_TWEET = "Write a short reply to this tweet as a real human without using hashtags: %s";

  public static final String OPENAI_PROMPT_PROMOTE_AFFILIATE_PRODUCT = """
      Write a creative reply for the following tweet, without hashtags, make it short and creative, write the reply without using 'Reply' word in the response, don't make the reply sound like an Ads, then promote a product about '%s' by adding this url to the reply: %s
      
      Here is the user tweet: %s.
      """;

  public static final String AI_PROMPT_CRYPTO_TRADING_USER = """
      You are a helpful Financial assistant, that answer and analyzes articles, latest news and financial data.
      Consider checking financial health, Yahoo data, and industry trends, any recent news or events affecting will be appreciated.
      
      Context: I am considering trading a cryptocurrency called "{0}", so is it a good idea to buy or sell "{0}" crypto based on the {2}?
      - Crypto Fear & Greed Index: Today: {3} - Yesterday: {4} - Last Week: {5}
      - Price of "{0}": Today: {6} - Yesterday: {7} - Last Week: {8}
      
      Prompt: Based on this information, should I buy, wait or sell "{0}", and why?
      
      - Here is the {2}:
      {1}
      """;

  public static final String AI_PROMPT_CRYPTO_TRADING_SYSTEM = """
      You are a helpful Financial assistant, that answer and analyse articles, latest news and financial data.
      Consider checking financial health, Yahoo data, and industry trends regarding, any recent news or events affecting will be appreciated.
      """;
}
