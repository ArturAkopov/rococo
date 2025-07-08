package anbrain.qa.rococo.page.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$$;

public abstract class BaseComponent<T extends BaseComponent<?>> {

    protected final SelenideElement self;
    protected final ElementsCollection error;

    public BaseComponent(SelenideElement self) {
        this.self = self;
        this.error = $$(".text-error-400");
    }

    @Step("Проверка, что error-message содержит текст '{expectedText}'")
    @Nonnull
    public T checkErrorMessage(String expectedText) {
        error.findBy(Condition.text(expectedText)).shouldBe(Condition.visible);
        return (T) this;
    }

    @Nonnull
    public SelenideElement getSelf() {
        return self;
    }
}