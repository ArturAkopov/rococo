package anbrain.qa.rococo.jupiter.extension;

import anbrain.qa.rococo.utils.AllureHttpFilter;
import io.restassured.RestAssured;
import org.junit.jupiter.api.extension.ExtensionContext;

public class AllureHttpFilterExtension implements SuiteExtension {

    @Override
    public void beforeSuite(ExtensionContext context) {
        RestAssured.filters(AllureHttpFilter.withCustomTemplates());
    }

    @Override
    public void afterSuite() {
        RestAssured.reset();
    }
}
