package project.two.wallet.Config;

import java.time.Duration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public RedisCacheConfiguration cacheConfiguration() {
		PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
				.allowIfSubType("project.two.wallet").allowIfSubType("java.util").build();

		GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
				.enableDefaultTyping(typeValidator).build();

		return RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofMinutes(10))
				.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));
	}
}
