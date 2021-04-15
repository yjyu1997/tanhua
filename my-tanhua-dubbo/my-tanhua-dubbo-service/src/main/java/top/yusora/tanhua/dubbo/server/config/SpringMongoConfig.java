package top.yusora.tanhua.dubbo.server.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.convert.CustomConversions;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mapping.model.SpELContext;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.util.Assert;

/**
 * @author heyu
 */
@Slf4j
@Configuration
public class SpringMongoConfig{

     @Bean
     public DefaultDbRefResolver defaultDbRefResolver(MongoDatabaseFactory mongoDatabaseFactory){
       return new DefaultDbRefResolver(mongoDatabaseFactory);
    }

    @Bean
    public MappingMongoConverter mappingMongoConverter (DbRefResolver dbRefResolver, MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {
        MappingMongoConverter mappingMongoConverter = new MappingMongoConverter(dbRefResolver, mappingContext);
        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return mappingMongoConverter;
    }
}
