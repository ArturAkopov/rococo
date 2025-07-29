package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.EditPaintingModal;
import anbrain.qa.rococo.page.components.Header;
import com.codeborne.selenide.*;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class PaintingsPage extends BasePage<PaintingsPage> {

    public final static String PAINTING_PAGE_URL = CONFIG.frontUrl() + "painting";

    private final EditPaintingModal editPaintingModal;
    private final Header header;
    private final SelenideElement
            addPaintingButton,
            inputSearch,
            searchButton;
    private final ElementsCollection paintingsGrid;

    public PaintingsPage() {
        this.editPaintingModal = new EditPaintingModal();
        this.header = new Header();
        this.addPaintingButton = $("#page-content button.btn");
        this.inputSearch = $("#page-content .input[title='Искать картины...'][type='search']");
        this.searchButton = $("#page-content button.btn-icon");
        this.paintingsGrid = $$("#page-content .grid .text-center");
    }

    @Step("Переход на страницу 'Картины'")
    public PaintingsPage open() {
        Selenide.open(PAINTING_PAGE_URL);
        return this;
    }

    @Step("Нажатие кнопки 'Добавить картину'")
    public PaintingsPage clickAddPaintingButton() {
        addPaintingButton.click();
        return this;
    }

    @Step("Поиск картины по названию '{title}'")
    public PaintingsPage searchPaintingByTitle(String title) {
        inputSearch.setValue(title).pressEnter();
        return this;
    }

    @Step("Переход на страницу картины '{titlePainting}'")
    public PaintingPage selectPainting(String titlePainting) {
        inputSearch.setValue(titlePainting).pressEnter();
        paintingsGrid.findBy(text(titlePainting)).scrollTo().click();
        return new PaintingPage();
    }

    @Step("Проверка загрузки страницы 'Картины'")
    @Override
    public PaintingsPage checkThatPageLoaded() {
        inputSearch.shouldBe(visible);
        return this;
    }

    @Step("Проверка наличия картины на странице")
    public void checkThatPaintingExist(String titlePainting) {
        paintingsGrid.findBy(text(titlePainting)).scrollTo().shouldBe(visible);
    }
}
