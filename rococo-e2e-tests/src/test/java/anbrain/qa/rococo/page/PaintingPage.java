package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.EditPaintingModal;
import anbrain.qa.rococo.page.components.Header;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class PaintingPage extends BasePage<PaintingPage> {

    private final Header header;
    private final SelenideElement
            paintingTitle,
            paintingArtist,
            paintingDescription,
            editPaintingButton;

    public PaintingPage() {
        this.header = new Header();
        this.paintingTitle = $("#page-content .card-header");
        this.paintingArtist = $("#page-content div.text-center");
        this.paintingDescription = $("#page-content .grid").$$("div").last();
        this.editPaintingButton = $("button[data-testid='edit-painting']");
    }

    @Step("Переход к редактированию картины")
    public EditPaintingModal toEditPainting() {
        editPaintingButton.scrollTo().click();
        return new EditPaintingModal();
    }

    @Step("Проверка загрузки страницы с картиной")
    @Override
    public PaintingPage checkThatPageLoaded() {
        paintingTitle.shouldBe(visible);
        paintingArtist.shouldBe(visible);
        paintingDescription.shouldBe(visible);
        return this;
    }

}
