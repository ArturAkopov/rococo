package anbrain.qa.rococo.page.components;

import anbrain.qa.rococo.page.MainPage;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;

public class EditProfileModal extends BaseComponent<EditProfileModal>{

    private final SelenideElement
            logoutButton,
            inputAvatar,
            inputFirstName,
            inputSurname,
            closeButton,
            updateProfileButton;

    public EditProfileModal() {
        super($("[data-testid='modal-component']"));
        this.logoutButton = self.$("button[type='button'].btn.variant-ghost");
        this.inputAvatar = self.$("input.input[type='file']");
        this.inputFirstName = self.$("input.input[name='firstname'][type='text']");
        this.inputSurname = self.$("input.input[name='surname'][type='text']");
        this.closeButton = self.$("button.btn.variant-ringed");
        this.updateProfileButton = self.$("button.btn.variant-filled-primary[type='submit']");
    }

    @Nonnull
    @Step("Выйти из профиля")
    public MainPage logout() {
        logoutButton.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Загрузить аватар")
    public EditProfileModal uploadAvatar(File filuploadFile) {
        inputAvatar.uploadFile(filuploadFile);
        return this;
    }

    @Nonnull
    @Step("Ввод имени '{firstName}'")
    public EditProfileModal setFirstName(String firstName) {
        inputFirstName.setValue(firstName);
        return this;
    }

    @Nonnull
    @Step("Ввод фамилии '{surname}'")
    public EditProfileModal setSurname(String surname) {
        inputSurname.setValue(surname);
        return this;
    }

    @Nonnull
    @Step("Закрыть модальное окно профиля")
    public MainPage close() {
        closeButton.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Сохранение изменений профиля")
    public MainPage saveChanges() {
        updateProfileButton.click();
        return new MainPage();
    }

    @Nonnull
    @Step("Неуспешное сохранение изменений профиля")
    public EditProfileModal invalidSaveChanges() {
        updateProfileButton.click();
        return new EditProfileModal();
    }

}
