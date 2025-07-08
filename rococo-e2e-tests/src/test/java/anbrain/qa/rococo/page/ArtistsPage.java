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
public class ArtistsPage extends BasePage<ArtistsPage> {

    public final static String ARTIST_PAGE_URL = CONFIG.frontUrl() + "/artist";

    private final Header header;
    private final SelenideElement
            addArtistButton,
            inputSearch,
            searchButton;
    private final ElementsCollection paintingsGrid;

    public ArtistsPage() {
        this.header = new Header();
        this.addArtistButton = $("#page-content button.btn");
        this.inputSearch = $("#page-content .input[title='Искать художников...'][type='search']");
        this.searchButton = $("#page-content button.btn-icon");
        this.paintingsGrid = $$("#page-content .grid .items-center");
    }

    @Step("Переход на страницу 'Художники'")
    public ArtistsPage open() {
        Selenide.open(ARTIST_PAGE_URL);
        return this;
    }

    @Step("Проверка загрузки страницы 'Художники'")
    @Override
    public ArtistsPage checkThatPageLoaded() {
        inputSearch.shouldBe(visible);
        return this;
    }

    //ToDo
    @Step("Переход на страницу художника '{nameArtist}'")
    public PaintingPage selectArtist(String nameArtist) {
        paintingsGrid.findBy(text(nameArtist)).scrollTo().click();
        return new PaintingPage();
    }
}
