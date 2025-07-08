package anbrain.qa.rococo.page.components;

import anbrain.qa.rococo.page.*;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

    private final SelenideElement
            mainPageLink,
            paintingButton,
            artistButton,
            museumButton,
            themeSwitchToggle,
            loginButton,
            profileButton;

    public Header() {
        super($("#shell-header").$("[data-testid='app-bar']"));
        this.mainPageLink = self.$("a[href*='/']");
        this.paintingButton = self.$("a[href*='/painting']");
        this.artistButton = self.$("a[href*='/artist']");
        this.museumButton = self.$("a[href*='/museum']");
        this.themeSwitchToggle = self.$("[role='switch']");
        this.loginButton = self.$("button.btn.variant-filled-primary");
        this.profileButton = self.$("button .avatar");
    }

    @Nonnull
    @Step("Переход на главную страницу")
    public MainPage toMainPage() {
        mainPageLink.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Переход на страницу 'Картины'")
    public PaintingsPage toPaintingsPage() {
        paintingButton.click();
        return new PaintingsPage();
    }

    @Nonnull
    @Step("Переход на страницу 'Художники'")
    public ArtistsPage toArtistsPage() {
        artistButton.click();
        return new ArtistsPage();
    }

    @Nonnull
    @Step("Переход на страницу 'Музеи'")
    public MuseumsPage toMuseumsPage() {
        museumButton.click();
        return new MuseumsPage();
    }

    @Nonnull
    @Step("Переключение темы")
    public Header toggleTheme() {
        themeSwitchToggle.click();
        return this;
    }

    @Nonnull
    @Step("Переход на страницу авторизации")
    public LoginPage toLoginPage() {
        loginButton.click();
        return new LoginPage();
    }

    @Nonnull
    @Step("Переход в меню профиля пользователя")
    public EditProfileModal toProfileModal() {
        profileButton.click();
        return new EditProfileModal();
    }

    @Step("Проверка авторизации")
    public void checkThatUserAuthorized(){
        profileButton.shouldBe(exist).shouldBe(visible);
    }
}
