package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.EditArtistModal;
import anbrain.qa.rococo.page.components.Header;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class ArtistPage extends BasePage<ArtistPage> {

    private final Header header;
    private final EditArtistModal editArtistModal;
    private final SelenideElement
            headerArtistName,
            artistDescription,
            figureAvatar,
            editArtistButton;

    public ArtistPage() {
        this.header = new Header();
        this.editArtistModal = new EditArtistModal();
        this.headerArtistName = $("header.card-header");
        this.artistDescription = $("p.col-span-2");
        this.editArtistButton = $("[data-testid='edit-artist']");
        this.figureAvatar = $("#page [data-testid='avatar']");
    }

    @Step("Нажатие кнопки редактирования художника")
    public ArtistPage clickEditArtistButton() {
        editArtistButton.scrollTo().click();
        return this;
    }

    @Step("Проверка загрузки страницы художника")
    @Override
    public ArtistPage checkThatPageLoaded() {
        headerArtistName.shouldBe(visible);
        return this;
    }
}
