package anbrain.qa.rococo.page.components;

import anbrain.qa.rococo.page.PaintingPage;
import anbrain.qa.rococo.page.PaintingsPage;
import com.codeborne.selenide.*;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import javax.annotation.Nonnull;

import java.io.File;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class EditPaintingModal extends BaseComponent<EditPaintingModal> {

    private final SelenideElement

            inputPaintingImage,
            inputPaintingTitle,
            selectPaintingArtist,
            textareaPaintingDescription,
            selectPaintingMuseum,
            submitButton,
            closeButton;

    public EditPaintingModal() {
        super($("[data-testid='modal-component']"));
        this.inputPaintingImage = self.$(".input[name='content'][type='file']");
        this.inputPaintingTitle = self.$("input[name='title']");
        this.selectPaintingArtist = self.$("select[name='authorId']");
        this.textareaPaintingDescription = self.$(".textarea[name='description']");
        this.selectPaintingMuseum = self.$("select[name='museumId']");
        this.submitButton = self.$(".btn[type='submit']");
        this.closeButton = self.$(".btn[type='button']");
    }

    @Nonnull
    @Step("Загрузка изображения картины")
    public EditPaintingModal uploadImage(File uploadFile) {
        inputPaintingImage.uploadFile(uploadFile);
        return this;
    }

    @Nonnull
    @Step("Ввод названия картины '{title}'")
    public EditPaintingModal setTitle(String title) {
        inputPaintingTitle.clear();
        inputPaintingTitle.setValue(title);
        return this;
    }

    @Nonnull
    @Step("Выбор автора картины '{artistName}'")
    public EditPaintingModal selectArtist(String artistName) {
        selectPaintingArtist.scrollTo().click();
        int attempts = 0;
        while (attempts < 200) {
            if (selectPaintingArtist.$$("option").findBy(text(artistName)).isDisplayed()) {
                selectPaintingArtist.$$("option").findBy(text(artistName)).click();
                return this;
            }
            selectPaintingArtist.sendKeys(Keys.ARROW_DOWN);
            attempts++;
        }
        selectPaintingArtist.$$("option").find(text(artistName)).click();
        return this;
    }

    @Nonnull
    @Step("Ввод описания картины '{description}'")
    public EditPaintingModal setDescription(String description) {
        textareaPaintingDescription.click();
        textareaPaintingDescription.clear();
        textareaPaintingDescription.setValue(description);
        return this;
    }

    @Nonnull
    @Step("Выбор музея картины '{museumTitle}'")
    public EditPaintingModal selectMuseum(String museumTitle) {
        selectPaintingMuseum.scrollIntoView(true).click();
        int attempts = 0;
        while (attempts < 200) {
            if (selectPaintingMuseum.$$("option").findBy(text(museumTitle)).isDisplayed()) {
                selectPaintingMuseum.$$("option").findBy(text(museumTitle)).click();
                return this;
            }
            selectPaintingMuseum.sendKeys(Keys.ARROW_DOWN);
            attempts++;
        }
        selectPaintingMuseum.$$("option").find(text(museumTitle)).click();
        return this;
    }

    @Nonnull
    @Step("Закрытие модального окна редактирования картины")
    public PaintingPage close() {
        closeButton.scrollTo().click();
        return new PaintingPage();
    }

    @Nonnull
    @Step("Сохранение изменений картины")
    public PaintingPage saveChanges() {
        submitButton.scrollTo().click();
        return new PaintingPage();
    }

    @Nonnull
    @Step("Сохранение новой картины")
    public PaintingsPage saveAddPainting() {
        submitButton.scrollTo().click();
        return new PaintingsPage();
    }

    @Nonnull
    @Step("Нажатие кнопки подтверждения")
    public EditPaintingModal clickSubmit() {
        submitButton.scrollTo().click();
        return this;
    }

}
