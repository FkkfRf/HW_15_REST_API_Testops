import api.AuthorizationApi;
import com.github.javafaker.Faker;
import models.lombok.CreateTestCaseBody;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static api.AuthorizationApi.ALLURE_TESTOPS_SESSION;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.is;


public class AllureTestopsTests extends BaseTest {
    public final static String
            USERNAME = "allure8",
            PASSWORD = "allure8",
            USER_TOKEN = "0bd99c71-d966-4079-8ecd-2403b132ca6e"; // create it in allure_url//user/30

    @Test
    void createTestCaseWithApiTest() {
        //{"id":13909,"name":"Random Case 3","automated":false,"external":false,"createdDate":1672088073323,"statusName":"Draft","statusColor":"#abb8c3"}

        AuthorizationApi authorizationApi = new AuthorizationApi();

        String xsrfToken = authorizationApi.getXsrfToken(USER_TOKEN);
        String authorizationCookie = authorizationApi
                .getAuthorizationCookie(USER_TOKEN, xsrfToken, USERNAME, PASSWORD);

        Faker faker = new Faker();
        String testCaseName = faker.name().title();
        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);


        int testCaseId = given()
                .log().all()
                .header("X-XSRF-TOKEN", xsrfToken)
                .cookies("XSRF-TOKEN", xsrfToken,
                        ALLURE_TESTOPS_SESSION, authorizationCookie)
                .body(testCaseBody)
                .contentType(JSON)
                .queryParam("projectId", "1771")
                .post("/api/rs/testcasetree/leaf")
                .then()
                .log().body()
                .statusCode(200)
                .body("name", is(testCaseName))
                .body("automated", is(false))
                .body("external", is(false))
                .extract()
                .path("id");

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));
        open("/project/1771/test-cases/" + testCaseId);
        $(".TestCaseLayout__name").shouldHave(text(testCaseName));
    }
    @Test
    void viewTestStepsWithUiTest() {
        /*
        1. Open page /project/1722/test-cases/13904
        2. Check name is "Case 1"
     */
        String authorizationCookie = new AuthorizationApi()
                .getAuthorizationCookie(USER_TOKEN, USERNAME, PASSWORD);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("/project/1771/test-cases/13904");
        $(".TestCaseLayout__name").shouldHave(text("Case 1"));
    }
}