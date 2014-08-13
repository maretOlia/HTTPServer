package serverBoot;

import handlers.annotations.Mapping;
import org.reflections.Reflections;

import java.util.Set;


/**
 * HandlersLoader responsible for retrieving all classes from defined package with specific annotation
 */
public class HandlersLoader {
//  getting all URL-handlers
    public static Set<Class<?>> loadHandlers() {
        Reflections reflections = new Reflections("handlers.impl");
        Set<Class<?>> urlHandlers = reflections.getTypesAnnotatedWith(Mapping.class);
        return urlHandlers;
    }
}
