package com.common.config;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.common.dto.StockIVAverageDto;
import com.common.dto.UserAuthDto;

@Configuration
public class RedisConfig {

	@Value("${spring.redis.host}")
	private String hostName;

	@Value("${spring.redis.port}")
	private Integer port;

	@Value("${spring.redis.password}")
	private String password;

	@Value("${spring.redis.database}")
	private Integer database;

	@Value("${spring.cache.cache-names}")
	private String[] cacheNames;

	@Value("${spring.redis.ssl:false}")
	private Boolean redisSSLEnable;

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(hostName, port);
		configuration.setPassword(password);
		configuration.setDatabase(database);
		LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(configuration);
		if (Boolean.TRUE.equals(redisSSLEnable)) {
			connectionFactory.setUseSsl(redisSSLEnable);
			connectionFactory.setValidateConnection(Boolean.TRUE);
//			connectionFactory.setStartTls(redisSSLEnable);
			connectionFactory.setVerifyPeer(Boolean.FALSE);
		}
		return connectionFactory;
	}

	@Bean
	public RedisTemplate<String, UserAuthDto> userDetailsRedisTemplate() {
		RedisTemplate<String, UserAuthDto> userAuthRedisTemplate = new RedisTemplate<>();
		userAuthRedisTemplate.setConnectionFactory(redisConnectionFactory());
		return userAuthRedisTemplate;
	}

	@Bean
	public RedisTemplate<String, List<StockIVAverageDto>> ivAverageByYearRedisTemplate() {
		RedisTemplate<String, List<StockIVAverageDto>> ivAverageByYearRedisTemplate = new RedisTemplate<>();
		ivAverageByYearRedisTemplate.setConnectionFactory(redisConnectionFactory());
		return ivAverageByYearRedisTemplate;
	}

	@Bean
	public RedisTemplate<String, StockIVAverageDto> ivAverageByYearAndStockRedisTemplate() {
		RedisTemplate<String, StockIVAverageDto> ivAverageByYearAndStockRedisTemplate = new RedisTemplate<>();
		ivAverageByYearAndStockRedisTemplate.setConnectionFactory(redisConnectionFactory());
		return ivAverageByYearAndStockRedisTemplate;
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		return RedisCacheManager.builder(connectionFactory)
				.initialCacheNames(Arrays.stream(cacheNames).parallel().collect(Collectors.toSet())).build();
	}

}