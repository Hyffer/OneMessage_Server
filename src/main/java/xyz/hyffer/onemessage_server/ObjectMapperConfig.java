package xyz.hyffer.onemessage_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import xyz.hyffer.onemessage_server.storage.component.MessageSegment;
import xyz.hyffer.onemessage_server.storage.component.serialization.MessageSegmentDeserializer;
import xyz.hyffer.onemessage_server.storage.component.serialization.OneBotMessageSegmentDeserializer;
import xyz.hyffer.onemessage_server.storage.component.serialization.OneBotMessageSegmentSerializer;

@Configuration
public class ObjectMapperConfig {

    @Bean
    @Primary
    // ObjectMapper with MessageSegment Serialization
    public ObjectMapper ObjectMapperMSS() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(MessageSegment.class, new MessageSegmentDeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        return objectMapper;
    }

    @Bean
    // ObjectMapper with OneBot compatible MessageSegment Serialization
    public ObjectMapper ObjectMapperOBMSS() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(MessageSegment.class, new OneBotMessageSegmentSerializer());
        module.addDeserializer(MessageSegment.class, new OneBotMessageSegmentDeserializer());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        return objectMapper;
    }
}
