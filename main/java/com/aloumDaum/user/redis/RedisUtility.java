package com.aloumDaum.user.redis;

import ch.qos.logback.classic.Logger;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisUtility {
    private static final Logger LOGGER = (Logger) LoggerFactory.getLogger(RedisUtility.class);
    private transient final ValueOperations<String, String> valOps;
    private transient final RedisTemplate<String, String> redisTemplate;
    private RedisUtility hashOperations;


    @Value("${spring.redis.timeout}")
    private int expiryTime;


    @Autowired
    public RedisUtility(final RedisTemplate<String, String> redisTemplate) {

        this.redisTemplate = redisTemplate;
        this.valOps = redisTemplate.opsForValue();

    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }


    public String getData(final String key) {

        String str = redisTemplate.opsForValue().get(key);
        System.out.println(str + ">>>>>>>>>>>>>");

        LOGGER.info("Getting data from redis for key:{}", key);
        return str;
    }

    public void setData(final String key, final Object data) {

        setData(key, data, expiryTime);

    }

    public void setData(final String key, final Object data, final Integer expiryInMinutes) {
        try {
            if (data == null)
                return;

            final ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            String stringData;
            if (data instanceof String) {
                stringData = (String) data;
            } else {
                stringData = objectMapper.writeValueAsString(data);
            }
            LOGGER.info("Setting data to redis for key:{}", key);

            this.valOps.set(key, stringData, expiryInMinutes, TimeUnit.MINUTES);
        } catch (JsonProcessingException e) {
            LOGGER.warn("Error saving data to redis key : {}", key, e);
        }

    }
}

