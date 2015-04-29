package me.curlpipesh.bytecodetools.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Classes marked with this annotation are used for injecting code into
 * arbitrary classes at runtime.
 *
 * @author audrey
 * @since 4/29/15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    String value();
}
