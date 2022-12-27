import api.AuthorizationApi;
import com.github.javafaker.Faker;
import models.lombok.StepBody;
import models.lombok.CaseBody;
import models.lombok.StepData;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import java.util.ArrayList;
import java.util.List;

import static api.AuthorizationApi.ALLURE_TESTOPS_SESSION;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.is;


public class AllureTestopsTests extends BaseTest {
    public final static String
            USERNAME = "allure8",
            PASSWORD = "allure8",
            USER_TOKEN = "0bd99c71-d966-4079-8ecd-2403b132ca6e";

    @Test
    void createTestCaseWithApiTest() {
        //{"id":13909,"name":"Random Case 3","automated":false,"external":false,"createdDate":1672088073323,"statusName":"Draft","statusColor":"#abb8c3"}

        AuthorizationApi authorizationApi = new AuthorizationApi();

        String xsrfToken = authorizationApi.getXsrfToken(USER_TOKEN);
        String authorizationCookie = authorizationApi
                .getAuthorizationCookie(USER_TOKEN, xsrfToken, USERNAME, PASSWORD);

        Faker faker = new Faker();
        String testCaseName = faker.name().title();
        CaseBody caseBody = new CaseBody();
        caseBody.setName(testCaseName);

        int testCaseId = given()
                .log().all()
                .header("X-XSRF-TOKEN", xsrfToken)
                .cookies("XSRF-TOKEN", xsrfToken,
                        ALLURE_TESTOPS_SESSION, authorizationCookie)
                .body(caseBody)
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

        String stepName;
        List<StepData> stepList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            stepName = testCaseName + " Step " + String.valueOf(i+1);
            StepData stepData = new StepData();
            stepData.setName(stepName);
            stepData.setStepsCount(i);
            stepList.add(stepData);
        }
            StepBody stepBody = new StepBody();
            stepBody.setSteps(stepList);
            given()
                    .log().all()
                    .header("X-XSRF-TOKEN", xsrfToken)
                    .cookies("XSRF-TOKEN", xsrfToken,
                            ALLURE_TESTOPS_SESSION, authorizationCookie)
                    .body(stepBody)
                    .contentType(JSON)
                    .queryParam("projectId", "1771")
                    //testcase/13917/scenario
                    .post("/api/rs/testcase/" + testCaseId + "/scenario")
                    .then()
                    .log().body()
                    .statusCode(200);


       open("/favicon.ico");
        getWebDriver().manage().addCookie(new Cookie(ALLURE_TESTOPS_SESSION, authorizationCookie));

        open("/project/1771/test-cases/" + testCaseId);
        for (int i = 0; i < stepList.size(); i++) {
            $$(".TreeElement__node").get(i).shouldHave(text(stepList.get(i).getName()));
        }
    }
}