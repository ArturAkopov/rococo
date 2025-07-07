package anbrain.qa.rococo.jupiter.annotation.meta;

import anbrain.qa.rococo.jupiter.extension.TestMethodContextExtension;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith({
        TestMethodContextExtension.class,
        AllureJunit5.class,
})
public @interface KafkaTest {
}