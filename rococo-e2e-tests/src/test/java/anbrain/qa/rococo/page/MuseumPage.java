package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.EditMuseumModal;
import anbrain.qa.rococo.page.components.Header;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class MuseumPage extends BasePage<MuseumPage> {

    private final Header header;
    private final SelenideElement
            museumTitle,
            museumCityAndCountry,
            museumDescription,
            editMuseumButton;

    public MuseumPage() {
        this.header = new Header();
        this.museumTitle = $("#page-content .card-header");
        this.museumCityAndCountry = $("#page-content div.text-center");
        this.museumDescription = $("#page-content .grid").$$("div").last();
        this.editMuseumButton = $("button[data-testid='edit-museum']");
    }

    @Step("Переход к редактированию музея")
    public EditMuseumModal toEditMuseum() {
        editMuseumButton.scrollTo().click();
        return new EditMuseumModal();
    }

    @Step("Проверка загрузки страницы с картиной")
    @Override
    public MuseumPage checkThatPageLoaded() {
        museumTitle.shouldBe(visible);
        museumCityAndCountry.shouldBe(visible);
        museumDescription.shouldBe(visible);
        return this;
    }

}
