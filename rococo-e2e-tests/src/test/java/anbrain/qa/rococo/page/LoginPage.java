package anbrain.qa.rococo.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class LoginPage extends BasePage<LoginPage> {

    private final SelenideElement
            inputUsername,
            inputPassword,
            submitButton,
            registerButton;

    public LoginPage() {
        this.inputUsername = $("form[action='/login'] input.form__input[name='username']");
        this.inputPassword = $("form[action='/login'] input.form__input[name='password']");
        this.submitButton = $("form[action='/login'] button.form__submit");
        this.registerButton = $("form[action='/login'] a[href='/register']");
    }

    @Step("Ввод имени пользователя '{username}'")
    public LoginPage setUsername(String username) {
        inputUsername.clear();
        inputUsername.setValue(username);
        return this;
    }

    @Step("Ввод пароля '{password}'")
    public LoginPage setPassword(String password) {
        inputPassword.clear();
        inputPassword.setValue(password);
        return this;
    }

    @Step("Подтверждение авторизации")
    public MainPage submitLogin() {
        submitButton.scrollTo();
        submitButton.click();
        return new MainPage();
    }

    @Step("Переход на страницу авторизации")
    public LoginPage open() {
        new MainPage()
                .open()
                .getHeader()
                .toLoginPage();
        return this;
    }

    @Step("Проверка загрузки страницы авторизации")
    @Override
    public LoginPage checkThatPageLoaded() {
        inputUsername.shouldBe(visible);
        inputPassword.shouldBe(visible);
        submitButton.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }
}
