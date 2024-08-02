package xyz.hyffer.onemessage_server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.hyffer.onemessage_server.model.MessageSegment;
import xyz.hyffer.onemessage_server.model.serialization.OneBotMessageSegmentDeserializer;
import xyz.hyffer.onemessage_server.model.serialization.OneBotMessageSegmentSerializer;

@Configuration
public class ObjectMapperConfig {

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
