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
    void loginWithApiTest() {
        String authorizationCookie = new AuthorizationApi()
                .getAuthorizationCookie(USER_TOKEN, USERNAME, PASSWORD);

        open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("");
        $("button[aria-label=\"User menu\"]").click();
        $(".Menu__item_info").shouldHave(text(USERNAME));
    }

    @Test
    void viewTestCaseWithApiTest() {
        /*
        1. Make GET request to /api/rs/testcase/13904/overview
        2. Check name is "Case 1"
     */
        String authorizationCookie = new AuthorizationApi()
                .getAuthorizationCookie(USER_TOKEN, USERNAME, PASSWORD);
        given()
                .log().all()
                .cookie(ALLURE_TESTOPS_SESSION, authorizationCookie)
                .get("/api/rs/testcase/13904/overview")
                .then()
                .log().all()
                .statusCode(200)
                .body("name",is("Case 1"));
    }

    @Test
    void viewTestCaseWithUiTest() {
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

    @Test
    void createTestCaseWithApiTest() {
        /*
        1. Make POST request to /api/rs/testcasetree/leaf?projectId=1771
                with body {"name":"Case 3"}
        2. Get test case {"id":13909,"name":"Random Case 3","automated":false,"external":false,"createdDate":1672088073323,"statusName":"Draft","statusColor":"#abb8c3"}
        3. Open page /project/1721/test-cases/{id}
        4. Check name is "{"name": "Random Case 3"
}"
     */
        AuthorizationApi authorizationApi = new AuthorizationApi();

        String xsrfToken = authorizationApi.getXsrfToken(USER_TOKEN);
        String authorizationCookie = authorizationApi
                .getAuthorizationCookie(USER_TOKEN, xsrfToken, USERNAME, PASSWORD);

        Faker faker = new Faker();
        String testCaseName = faker.name().title();

        CreateTestCaseBody testCaseBody = new CreateTestCaseBody();
        testCaseBody.setName(testCaseName);
//        String testCaseBody = "{\"name\":\"{
//  "name": "Random Case 3"
//}\"}";

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
}