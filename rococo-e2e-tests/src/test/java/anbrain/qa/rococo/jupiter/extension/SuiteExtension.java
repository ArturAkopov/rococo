package anbrain.qa.rococo.jupiter.extension;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public interface SuiteExtension extends BeforeAllCallback {
    @Override
    default void beforeAll(ExtensionContext context) {
        ExtensionContext rootContext = context.getRoot();
        rootContext.getStore(ExtensionContext.Namespace.GLOBAL)
                .getOrComputeIfAbsent(
                        this.getClass(),
                        key -> {
                            beforeSuite(rootContext);
                            return (ExtensionContext.Store.CloseableResource) this::afterSuite;
                        }


                );
    }

    default void beforeSuite(ExtensionContext context) {
    }

    default void afterSuite() {
    }
}
