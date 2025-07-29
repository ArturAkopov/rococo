package anbrain.qa.rococo.page.components;

import anbrain.qa.rococo.page.MuseumsPage;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

import javax.annotation.Nonnull;
import java.io.File;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class EditMuseumModal extends BaseComponent<EditMuseumModal> {

    private final SelenideElement
            inputMuseumTitle,
            selectMuseumCountry,
            inputMuseumCity,
            inputMuseumPhoto,
            textAreaDescription,
            submitButton;

    public EditMuseumModal() {
        super($("[data-testid='modal-component']"));
        this.inputMuseumTitle = self.$(".input[name='title']");
        this.selectMuseumCountry = self.$(".select[name='countryId']");
        this.inputMuseumCity = self.$(".input[name='city']");
        this.inputMuseumPhoto = self.$(".input[name='photo']");
        this.textAreaDescription = self.$(".textarea[name='description']");
        this.submitButton = self.$("button[type='submit']");
    }

    @Nonnull
    @Step("Ввод названия музея '{title}'")
    public EditMuseumModal setMuseumTitle(String title) {
        inputMuseumTitle.clear();
        inputMuseumTitle.setValue(title);
        return this;
    }

    @Nonnull
    @Step("Выбор страны {countryName}")
    public EditMuseumModal selectCounty(String countryName) {
        selectMuseumCountry.scrollIntoView(true).click();
        int attempts = 0;
        while (attempts < 200) {
            if (selectMuseumCountry.$$("option").findBy(text(countryName)).isDisplayed()) {
                selectMuseumCountry.$$("option").findBy(text(countryName)).click();
                return this;
            }
            selectMuseumCountry.sendKeys(Keys.ARROW_DOWN);
            attempts++;
        }
        selectMuseumCountry.$$("option").find(text(countryName)).click();
        return this;
    }

    @Nonnull
    @Step("Ввод названия города '{cityName}'")
    public EditMuseumModal setCityName(String cityName) {
        inputMuseumCity.clear();
        inputMuseumCity.setValue(cityName);
        return this;
    }

    @Nonnull
    @Step("Загрузка изображения музея")
    public EditMuseumModal uploadPhoto(File uploadFile) {
        inputMuseumPhoto.uploadFile(uploadFile);
        return this;
    }

    @Nonnull
    @Step("Ввод описания '{description}'")
    public EditMuseumModal setDescription(String description) {
        textAreaDescription.clear();
        textAreaDescription.setValue(description);
        return this;
    }

    @Nonnull
    @Step("Сохранить данные музея")
    public MuseumsPage saveMuseum() {
        submitButton.scrollTo().click();
        return new MuseumsPage();
    }

    @Nonnull
    @Step("Нажатие кнопки подтверждения")
    public EditMuseumModal clickSubmitButton() {
        submitButton.scrollTo().click();
        return this;
    }

}
