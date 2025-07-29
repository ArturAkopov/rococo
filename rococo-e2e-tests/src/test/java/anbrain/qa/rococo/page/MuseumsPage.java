package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.EditMuseumModal;
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
    private final EditMuseumModal editMuseumModal;
    private final SelenideElement
            addMuseumButton,
            inputSearch,
            searchButton;
    private final ElementsCollection museumsGrid;

    public MuseumsPage() {
        this.header = new Header();
        this.editMuseumModal = new EditMuseumModal();
        this.addMuseumButton = $("#page-content button.btn");
        this.inputSearch = $("#page-content .input[title='Искать музей...'][type='search']");
        this.searchButton = $("#page-content button.btn-icon");
        this.museumsGrid = $$("#page-content .grid a[href]");
    }

    @Step("Переход на страницу 'Музеи'")
    public MuseumsPage open() {
        Selenide.open(MUSEUM_PAGE_URL);
        return this;
    }

    @Step("Поиск музея по названию '{museumTitle}'")
    public MuseumsPage searchMuseumByTitle(String titleMuseum) {
        inputSearch.setValue(titleMuseum).pressEnter();
        return this;
    }

    @Step("Нажатие кнопки 'Добавить музей'")
    public MuseumsPage clickAddMuseumButton() {
        addMuseumButton.click();
        return this;
    }

    @Step("Переход на страницу музея '{titleMuseum}'")
    public MuseumPage selectMuseum(String titleMuseum) {
        inputSearch.setValue(titleMuseum).pressEnter();
        museumsGrid.findBy(text(titleMuseum)).scrollTo().click();
        return new MuseumPage();
    }

    @Step("Проверка наличия музея на странице")
    public void checkThatMuseumExist(String titleMuseum) {
        museumsGrid.findBy(text(titleMuseum)).scrollTo().shouldBe(visible);
    }

    @Step("Проверка загрузки страницы с 'Музеи'")
    @Override
    public MuseumsPage checkThatPageLoaded() {
        inputSearch.shouldBe(visible);
        return this;
    }

}
