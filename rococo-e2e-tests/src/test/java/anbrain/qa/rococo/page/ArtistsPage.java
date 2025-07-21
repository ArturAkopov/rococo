package anbrain.qa.rococo.page;

import anbrain.qa.rococo.page.components.EditArtistModal;
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

    public final static String ARTIST_PAGE_URL = CONFIG.frontUrl() + "artist";

    private final Header header;
    private final EditArtistModal editArtistModal;
    private final SelenideElement
            addArtistButton,
            inputSearch,
            searchButton;
    private final ElementsCollection artistsGrid;

    public ArtistsPage() {
        this.header = new Header();
        this.editArtistModal = new EditArtistModal();
        this.addArtistButton = $("#page-content button.btn");
        this.inputSearch = $("#page-content .input[title='Искать художников...'][type='search']");
        this.searchButton = $("#page-content button.btn-icon");
        this.artistsGrid = $$("#page-content .grid .items-center");
    }

    @Step("Переход на страницу 'Художники'")
    public ArtistsPage open() {
        Selenide.open(ARTIST_PAGE_URL);
        return this;
    }

    @Step("Поиск художника по имени '{artistName}'")
    public ArtistsPage searchArtistByName(String artistName) {
        inputSearch.setValue(artistName).pressEnter();
        return this;
    }

    @Step("Нажатие кнопки 'Добавить художника'")
    public ArtistsPage clickAddArtistButton() {
        addArtistButton.click();
        return this;
    }

    @Step("Переход на страницу художника '{nameArtist}'")
    public ArtistPage selectArtist(String nameArtist) {
        inputSearch.setValue(nameArtist).pressEnter();
        artistsGrid.findBy(text(nameArtist)).scrollTo().click();
        return new ArtistPage();
    }

    @Step("Проверка наличия художника на странице")
    public void checkThatArtistExist(String artistName) {
        artistsGrid.findBy(text(artistName)).scrollTo().shouldBe(visible);
    }

    @Step("Проверка загрузки страницы 'Художники'")
    @Override
    public ArtistsPage checkThatPageLoaded() {
        inputSearch.shouldBe(visible);
        return this;
    }
}
