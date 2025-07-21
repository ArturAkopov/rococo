package anbrain.qa.rococo.page;

import anbrain.qa.rococo.config.Config;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Selenide.$$;

public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CONFIG = Config.getInstance();

    private final ElementsCollection error, toast;

    public abstract T checkThatPageLoaded();

    public BasePage() {
        this.error = $$(".form__error");
        this.toast = $$("[data-testid='toast']");
    }

    @Step("Проверка, что toast-message содержит текст '{expectedText}'")
    @Nonnull
    public T checkToastMessage(String expectedText) {
        toast.find(Condition.text(expectedText)).shouldBe(Condition.visible);
        return (T) this;
    }

    @Step("Проверка, что form__error-message содержит текст '{expectedText}'")
    @Nonnull
    public T checkErrorMessage(String expectedText) {
        error.find(Condition.text(expectedText)).scrollTo().shouldBe(Condition.visible);
        return (T) this;
    }
}
