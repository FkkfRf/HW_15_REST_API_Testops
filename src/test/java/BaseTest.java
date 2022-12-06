import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import helpers.Attach;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.openqa.selenium.remote.DesiredCapabilities;

import static helpers.CustomApiListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;

public class BaseTest {
    @BeforeAll
    static void sertUp() {
        step("Устанавливаем базовый URL", () -> {
            Configuration.baseUrl = "https://allure.autotests.cloud";
            RestAssured.baseURI = "https://allure.autotests.cloud";
            RestAssured.filters(withCustomTemplates());
        });
        step("Устанавливаем интеграцию с Selenide", () ->
                SelenideLogger.addListener("AllureSelenide", new AllureSelenide()));
        step("Устанавливаем конфигурацию", () -> {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("enableVNC", true);
            capabilities.setCapability("enableVideo", true);
            Configuration.browserCapabilities = capabilities;
        });
    }

    @AfterEach
    void addAttachments() {
        step("прикрепляем логи и отчеты выполнения", () -> {
            Attach.screenshotAs("Last screenshot");
            Attach.pageSource();
            Attach.browserConsoleLogs();
            Attach.addVideo();
        });
    }

    @AfterAll
    static void CloseWebDriver() {
        step("закрываем вебдрайвер", () -> Selenide.closeWebDriver());
    }
}