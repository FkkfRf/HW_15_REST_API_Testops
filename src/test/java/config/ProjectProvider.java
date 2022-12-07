package config;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.aeonbits.owner.ConfigFactory;

public class ProjectProvider {
    static BaseTestConfig configBase = ConfigFactory.create(BaseTestConfig.class, System.getProperties());

    public static void configBase() {
        RestAssured.baseURI = ProjectProvider.configBase.getBaseUri();
        Configuration.baseUrl = ProjectProvider.configBase.getBaseUrl();
        Configuration.browser = ProjectProvider.configBase.getBrowser();
        Configuration.browserVersion = ProjectProvider.configBase.getBrowserVersion();
        Configuration.browserSize = ProjectProvider.configBase.getBrowserSize();
        String remoteUrl = ProjectProvider.configBase.getRemoteURL();
        if (remoteUrl != null) {
            Configuration.remote = remoteUrl;
        }
    }
}
