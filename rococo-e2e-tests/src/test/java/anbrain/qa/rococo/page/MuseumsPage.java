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
public class MuseumsPage extends BasePage<MuseumsPage> {

    public final static String MUSEUM_PAGE_URL = CONFIG.frontUrl() + "museum";

    private final Header header;
    private final SelenideElement
            addMuseumButton,
            inputSearch,
            searchButton;
    private final ElementsCollection paintingsGrid;

    public MuseumsPage() {
        this.header = new Header();
        this.addMuseumButton = $("#page-content button.btn");
        this.inputSearch = $("#page-content .input[title='Искать музей...'][type='search']");
        this.searchButton = $("#page-content button.btn-icon");
        this.paintingsGrid = $$("#page-content .grid a[href]");
    }

    @Step("Переход на страницу с 'Музеи'")
    public MuseumsPage open() {
        Selenide.open(MUSEUM_PAGE_URL);
        return this;
    }

    @Step("Проверка загрузки страницы с 'Музеи'")
    @Override
    public MuseumsPage checkThatPageLoaded() {
        inputSearch.shouldBe(visible);
        return this;
    }

    //ToDo
    @Step("Переход на страницу художника '{titleMuseum}'")
    public PaintingPage selectMuseum(String titleMuseum) {
        paintingsGrid.findBy(text(titleMuseum)).scrollTo().click();
        return new PaintingPage();
    }
}
