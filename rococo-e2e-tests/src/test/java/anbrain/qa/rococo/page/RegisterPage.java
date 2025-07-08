package anbrain.qa.rococo.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class RegisterPage extends BasePage<RegisterPage> {

    private final static String REGISTER_PAGE_URL = CONFIG.authUrl() + "register";

    private final SelenideElement
            inputUsername,
            inputPassword,
            inputSubmitPassword,
            submitRegisterButton,
            loginButton,
            submitEnterInSystemButton;

    public RegisterPage() {
        this.inputUsername = $("form[action='/register'] input.form__input[name='username']");
        this.inputPassword = $("form[action='/register'] input.form__input[name='password']");
        this.inputSubmitPassword = $("form[action='/register'] input.form__input[name='passwordSubmit']");
        this.submitRegisterButton = $("form[action='/register'] button[type='submit']");
        this.loginButton = $("form[action='/register'] .form__link");
        this.submitEnterInSystemButton = $(".form .form__submit");
    }

    @Step("Переход на страницу регистрации")
    public RegisterPage open() {
        Selenide.open(REGISTER_PAGE_URL);
        return this;
    }

    @Step("Ввод имени пользователя '{username}'")
    public RegisterPage setUsername(String username) {
        inputUsername.clear();
        inputUsername.setValue(username);
        return this;
    }

    @Step("Ввод пароля '{password}'")
    public RegisterPage setPassword(String password) {
        inputPassword.clear();
        inputPassword.setValue(password);
        return this;
    }

    @Step("Ввод пароля для подтверждения'{password}'")
    public RegisterPage setSubmitPassword(String password) {
        inputSubmitPassword.clear();
        inputSubmitPassword.setValue(password);
        return this;
    }

    @Step("Подтверждение регистрации")
    public RegisterPage submitRegister() {
        submitRegisterButton.click();
        return this;
    }

    @Step("Проверка успешной регистрации")
    public void checkThatRegisterSuccess(){
        submitEnterInSystemButton.shouldBe(visible).shouldHave(text("Войти в систему"));
    }

    @Step("Проверка неуспешной регистрации")
    public void checkThatRegisterFail(){
        submitRegisterButton.shouldBe(visible).shouldNotHave(text("Войти в систему"));
    }

    @Step("Проверка загрузки страницы регистрации")
    @Override
    public RegisterPage checkThatPageLoaded() {
        inputUsername.shouldBe(visible);
        inputPassword.shouldBe(visible);
        submitRegisterButton.shouldBe(visible);
        loginButton.shouldBe(visible);
        return this;
    }
}
