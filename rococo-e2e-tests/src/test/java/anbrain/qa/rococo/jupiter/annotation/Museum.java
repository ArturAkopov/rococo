package anbrain.qa.rococo.jupiter.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Museum {

    String title() default "";

    String description() default "";

    String city() default "";

    String country() default "";

}
