package config;

import org.aeonbits.owner.Config;

@Config.Sources({
        //"classpath:config/${env}.properties"
        "classpath:config/local.properties"
})
public interface BaseTestConfig extends Config {
    @Key("baseUrl")
    @DefaultValue("https://allure.autotests.cloud")
    String getBaseUrl();

    @Key("baseUri")
    @DefaultValue("https://allure.autotests.cloud")
    String getBaseUri();

    @Key("browser")
    @DefaultValue("CHROME")
    String getBrowser();

    @Key("browserVersion")
    @DefaultValue("100.0")
    String getBrowserVersion();

    @Key("browserSize")
    String getBrowserSize();

    @Key("remoteUrl")
    String getRemoteURL();
}

