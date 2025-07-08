package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.Header;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class PaintingsPage extends BasePage<PaintingsPage> {

    public final static String PAINTING_PAGE_URL = CONFIG.frontUrl() + "/painting";

    private final Header header;
    private final SelenideElement
            addPaintingButton,
            inputSearch,
            searchButton;
    private final ElementsCollection paintingsGrid;

    public PaintingsPage() {
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

    @Step("Проверка загрузки страницы 'Картины'")
    @Override
    public PaintingsPage checkThatPageLoaded() {
        inputSearch.shouldBe(visible);
        return this;
    }

    @Step("Переход на страницу картины '{titlePainting}'")
    public PaintingPage selectPainting(String titlePainting) {
        paintingsGrid.findBy(text(titlePainting)).scrollTo().click();
        return new PaintingPage();
    }
}
