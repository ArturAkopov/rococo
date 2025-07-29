package anbrain.qa.rococo.page.components;

import anbrain.qa.rococo.page.ArtistsPage;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.io.File;

import static com.codeborne.selenide.Selenide.$;

public class EditArtistModal extends BaseComponent<EditArtistModal> {

    private final SelenideElement
            inputArtistName,
            inputArtistPhoto,
            textAreaBiography,
            submitButton;

    public EditArtistModal() {
        super($("[data-testid='modal-component']"));
        this.inputArtistName = self.$(".input[name='name']");
        this.inputArtistPhoto = self.$(".input[name='photo']");
        this.textAreaBiography = self.$(".textarea[name='biography']");
        this.submitButton = self.$("button[type='submit']");
    }

    @Nonnull
    @Step("Ввод имени художника '{artistName}'")
    public EditArtistModal setArtistName(String artistName) {
        inputArtistName.clear();
        inputArtistName.setValue(artistName);
        return this;
    }

    @Nonnull
    @Step("Загрузка фотографии художника")
    public EditArtistModal uploadPhoto(File uploadFile) {
        inputArtistPhoto.uploadFile(uploadFile);
        return this;
    }

    @Nonnull
    @Step("Ввод биографии '{biography}'")
    public EditArtistModal setBiography(String biography) {
        textAreaBiography.clear();
        textAreaBiography.setValue(biography);
        return this;
    }

    @Nonnull
    @Step("Сохранить данные художника")
    public ArtistsPage saveArtist() {
        submitButton.scrollTo().click();
        return new ArtistsPage();
    }

    @Nonnull
    @Step("Нажатие кнопки подтверждения")
    public EditArtistModal clickSubmitButton() {
        submitButton.scrollTo().click();
        return this;
    }

}
