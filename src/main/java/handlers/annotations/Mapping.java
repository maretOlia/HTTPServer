package handlers.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Mapping annotation works like mark for classes which represents URL - handlers.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Mapping {
    String uri();
}
