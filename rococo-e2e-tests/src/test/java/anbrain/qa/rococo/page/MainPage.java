package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.Header;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class MainPage extends BasePage<MainPage> {

    public final static String MAIN_PAGE_URL = CONFIG.frontUrl();

    private final Header header;
    private final SelenideElement
            paintingImgButton,
            artistImgButton,
            museumImgButton;

    public MainPage() {
        this.header = new Header();
        this.paintingImgButton = $("#page-content a[href='/painting']");
        this.artistImgButton = $("#page-content a[href='/artist']");
        this.museumImgButton = $("#page-content a[href='/museum']");
    }

    @Step("Переход на главную страницу")
    public MainPage open() {
        Selenide.open(MAIN_PAGE_URL);
        return this;
    }

    @Step("Проверка загрузки главной страницы")
    @Override
    public MainPage checkThatPageLoaded() {
        paintingImgButton.shouldBe(visible);
        artistImgButton.shouldBe(visible);
        museumImgButton.shouldBe(visible);
        return this;
    }

    @Step("Проверка отсутствия загрузки главной страницы")
    public MainPage checkThatPageUnloaded() {
        paintingImgButton.shouldNotBe(exist).shouldNotBe(visible);
        artistImgButton.shouldNotBe(exist).shouldNotBe(visible);
        museumImgButton.shouldNotBe(exist).shouldNotBe(visible);
        return this;
    }
}
