package anbrain.qa.rococo.page.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class Alert extends BaseComponent {

    private final SelenideElement textBase;

    public Alert() {
        super($("[role='alertdialog']"));
        this.textBase = $(".text-base");
    }

    public void checkAlertText(String alertText){
        textBase.shouldBe(visible).shouldHave(text(alertText));
    }
}
