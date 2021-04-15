package top.yusora.tanhua.dubbo.server.config;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import top.yusora.tanhua.dubbo.server.anno.CascadeSave;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author heyu
 */
@Component
public class CascadingMongoEventListener<E> extends AbstractMongoEventListener<E> {
  @Autowired
  private MongoOperations mongoOperations;
 
  @Override
  public void onBeforeConvert(BeforeConvertEvent<E> event) {
      ReflectionUtils.doWithFields(event.getSource().getClass(), field -> {
          ReflectionUtils.makeAccessible(field);

          if (field.isAnnotationPresent(DBRef.class) && field.isAnnotationPresent(CascadeSave.class)) {
              final List<Object> fieldValue = (List<Object>)field.get(event.getSource());

              DbRefFieldCallback callback = new DbRefFieldCallback();

              for (Object o : fieldValue) {
                  ReflectionUtils.doWithFields(o.getClass(), callback);

                  if (!callback.isIdFound()) {
                      throw new MappingException("Cannot perform cascade save on child object without id set");
                  }

                  mongoOperations.save(o);
              }

          }
      });
  }
 
  private static class DbRefFieldCallback implements ReflectionUtils.FieldCallback {
      private boolean idFound;
 
      @Override
      public void doWith(Field field) throws IllegalArgumentException {
          ReflectionUtils.makeAccessible(field);
 
          if (field.isAnnotationPresent(Id.class)) {
              idFound = true;
          }
      }
 
      public boolean isIdFound() {
          return idFound;
      }
  }
}