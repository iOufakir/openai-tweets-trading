package com.ilyo.openai.service.impl;

import com.ilyo.openai.external.twitter.client.TwitterApiClient;
import com.ilyo.openai.external.twitter.config.TwitterConfig;
import com.ilyo.openai.external.twitter.dto.TweetReply;
import com.ilyo.openai.external.twitter.dto.request.AccessTokenRequest;
import com.ilyo.openai.external.twitter.dto.request.TweetCreationRequest;
import com.ilyo.openai.external.twitter.dto.request.TweetLikeRequest;
import com.ilyo.openai.external.twitter.dto.request.TweetReplyRequest;
import com.ilyo.openai.external.twitter.dto.response.AccessTokenResponse;
import com.ilyo.openai.external.twitter.dto.response.LatestTweetsResponse;
import com.ilyo.openai.external.twitter.dto.response.TweetCreationResponse;
import com.ilyo.openai.external.twitter.dto.response.TweetLikeResponse;
import com.ilyo.openai.service.MyWebClient;
import com.ilyo.openai.service.TwitterService;
import com.ilyo.openai.util.AuthorizationUtils;
import com.ilyo.openai.util.RetrofitUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneOffset;


@Service
@AllArgsConstructor
@Slf4j
public class BaseTwitterService implements TwitterService {

    private final TwitterApiClient twitterApiClient;
    private final TwitterConfig twitterConfig;

    private final MyWebClient myWebClient;
    private final SecureRandom secureRandom;

    @Override
    public LatestTweetsResponse getLatestTweets(final Instant startTime, String searchQuery, boolean withInfluencers) {
        log.debug("Get Latest Tweets using {}", startTime);

        var query = searchQuery;
        if (withInfluencers) {
            query = "%s (%s)".formatted(searchQuery, getInfluencersQuery(twitterConfig.influencersList().split(",")));
        }
        var call = twitterApiClient
                .getPublicLatestTweets(AuthorizationUtils.createBearerToken(twitterConfig.clientToken()), query,
                        twitterConfig.tweetsMaxResults(),
                        startTime.atZone(ZoneOffset.UTC).toInstant());
        return RetrofitUtils.executeCall(call);
    }

    @Override
    public TweetCreationResponse publishTweet(final String tweetText) {
        var oAuth2BearerToken = getOAuth2Token("users.read tweet.read tweet.write offline.access");
        var call = twitterApiClient.createTweet(oAuth2BearerToken, new TweetCreationRequest(tweetText));
        return RetrofitUtils.executeCall(call);
    }

    @Override
    public TweetCreationResponse replyToTweet(final String text, final String targetTweetId) {
        var oAuth2BearerToken = getOAuth2Token("users.read tweet.read tweet.write offline.access");
        var call = twitterApiClient.reply(oAuth2BearerToken, new TweetReplyRequest(text, new TweetReply(targetTweetId)));
        return RetrofitUtils.executeCall(call);
    }

    @Override
    public TweetLikeResponse likeTweet(final String authenticatedUserId, final String targetTweetId) {
        var oAuth2BearerToken = getOAuth2Token("users.read like.write tweet.read");
        var call = twitterApiClient.likeTweet(oAuth2BearerToken, authenticatedUserId, new TweetLikeRequest(targetTweetId));
        return RetrofitUtils.executeCall(call);
    }

    private String getOAuth2Token(String scope) {
        final var secretState = "state-%s".formatted(secureRandom.nextInt(9_000));
        final String authorizationUrl = buildOAuth2AuthorizeUrl(twitterConfig.clientKey(),
                twitterConfig.callbackUrl(), scope,
                secretState);
        final var code = myWebClient.extractTwitterOAuth2Code(authorizationUrl, twitterConfig.callbackUrl());
        var accessTokenResponse = getAccessToken(new AccessTokenRequest(code,
                "authorization_code", "challenge"));

        log.debug("Getting OAuth2 Access Token: {}", accessTokenResponse.accessToken());
        return AuthorizationUtils.createBearerToken(accessTokenResponse.accessToken());
    }


    private AccessTokenResponse getAccessToken(final AccessTokenRequest request) {
        var call = twitterApiClient.getAccessToken(
                AuthorizationUtils.createBasicAuthorizationHeader(twitterConfig.clientKey(), twitterConfig.clientSecret()),
                request.code(), request.grantType(), twitterConfig.clientKey(), request.codeVerifier(), twitterConfig.callbackUrl());
        return RetrofitUtils.executeCall(call);
    }
}
