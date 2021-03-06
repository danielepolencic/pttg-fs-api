package steps

import com.jayway.restassured.response.Response
import cucumber.api.DataTable
import cucumber.api.Scenario
import cucumber.api.java.After
import cucumber.api.java.Before
import cucumber.api.java.en.Given
import cucumber.api.java.en.Then
import cucumber.api.java.en.When
import net.thucydides.core.annotations.Managed
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.text.WordUtils
import org.json.JSONObject
import org.springframework.beans.BeansException
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.servlet.DispatcherServlet
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ApiExceptionHandler
import uk.gov.digital.ho.proving.financialstatus.api.configuration.ServiceConfiguration

import java.text.DateFormat
import java.text.DecimalFormat
import java.text.SimpleDateFormat

import static com.jayway.jsonpath.JsonPath.read
import static com.jayway.restassured.RestAssured.get
import static com.jayway.restassured.RestAssured.given

/**
 * For wiremock-backed tests use the "test" profile in the @ActiveProfiles annotation:
 *           - This will launch the wiremock server using the application-test.properties
 *
 * To switch to end to end tests use the "endtoendtest" profile in the @ActiveProfiles annotation:
 *            - This will use the application-endtoend.properties
 *
 */
@SpringApplicationConfiguration(classes = [ServiceConfiguration.class, ApiExceptionHandler.class])
@WebAppConfiguration
@IntegrationTest()
@ActiveProfiles("test")
//@ActiveProfiles("endtoend")
class FinancialStatusApiSteps implements ApplicationContextAware {

    @Override
    void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        //required for @controllerAdvice to work
        DispatcherServlet ds = applicationContext.getBean("dispatcherServlet");
        ds.setThrowExceptionIfNoHandlerFound(true)
    }

    @Value('${local.server.port}')
    private String serverPort;

    @Value('${barclays.stub.service}')
    private String barclaysService;

    @Value('${wiremock}')
    private Boolean wiremock;

    @Managed
    public Response resp
    public Response resp2
    String jsonAsString
    String dependants = ""
    String applicantType = ""
    String fromDate = ""
    String toDate = ""
    String accountNumber = ""
    String sortCode = ""
    String minimum = ""
    String days = ""
    String inLondon = ""
    String courseLength = ""
    String tuitionFees = ""
    String tuitionFeesPaid = ""
    String accommodationFeesPaid = ""
    String studentType = ""
    String dob = ""
    String userId //= ""
    String accountHolderConsent = ""
    def courseStartDate = " "
    String courseEndDate = ""
    String continuationEndDate = ""
    String numberOfDependants = ""
    String courseType = ""
    String originalCourseStartDate = ""

    List<String> Todate = new ArrayList()
    List<String> Fromdate = new ArrayList()
    List<String> Minimums = new ArrayList()
    List<String> Accountholderconsent = new ArrayList()
    List<String> Userid = new ArrayList()
    List<String> Dateofbirth = new ArrayList()
    List<String> Sortcode = new ArrayList()
    List<String> Accountnumber = new ArrayList()


    def testDataLoader
    FeatureKeyMapper fkm = new FeatureKeyMapper()

    def balancesUrlRegex = "/financialstatus/v1.*"

    @Before
    def setUp(Scenario scenario) {
        if (wiremock) {
            testDataLoader = new WireMockTestDataLoader(new URL(barclaysService).getPort())
        }
    }

    @After
    def tearDown() {
        testDataLoader?.stop()
    }

    def String toCamelCase(String s) {
        String allUpper = StringUtils.remove(WordUtils.capitalizeFully(s), " ")
        String camelCase = allUpper[0].toLowerCase() + allUpper.substring(1)
        camelCase
    }

    public String verifyDateFormat(String featureDate) {
        String initialDate = featureDate
        String transformedDate
        DateFormat df = new SimpleDateFormat("yyyy-mm-dd")
        Date date = df.parse(initialDate)
        transformedDate = df.format(date)
        return transformedDate
    }

    def String getTableData(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class)
        String[] tableKey = entries.keySet()

        for (String s : tableKey) {

            if (s.equalsIgnoreCase("dependants")) {
                dependants = entries.get(s)
            }
            if (s.equalsIgnoreCase("applicant type")) {
                applicantType = entries.get(s)
            }

            if (s.equalsIgnoreCase("Student Type")) {
                studentType = entries.get(s)
            }
            if (s.equalsIgnoreCase("Account Number")) {
                accountNumber = entries.get(s)
            }
            if (s.equalsIgnoreCase("Minimum")) {
                minimum = entries.get(s)
            }
            if (s.equalsIgnoreCase("From Date")) {
                fromDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("Sort Code")) {
                sortCode = entries.get(s)
            }
            if (s.equalsIgnoreCase("To Date")) {
                toDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("Course start date")) {
                courseStartDate = entries.get(s)

            }
            if (s.equalsIgnoreCase("Course end date")) {
                courseEndDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("Continuation end date")) {
                continuationEndDate = entries.get(s)
            }
            if (s.equalsIgnoreCase("Total tuition fees")) {
                tuitionFees = entries.get(s)
            }
            if (s.equalsIgnoreCase("In London") && entries.get(s).equalsIgnoreCase("Yes")) {
                inLondon = "true"
            } else if (s.equalsIgnoreCase("In London") && entries.get(s).equalsIgnoreCase("No")) {
                inLondon = "false"
            } else if (s.equalsIgnoreCase("In London")) {
                inLondon = ""
            }

            if (s.equalsIgnoreCase("Tuition fees already paid")) {
                tuitionFeesPaid = entries.get(s)
            }

            if (s.equalsIgnoreCase("Accommodation fees already paid")) {
                accommodationFeesPaid = entries.get(s)
            }
            if (s.equalsIgnoreCase("Date of Birth")) {
                dob = entries.get(s)
            }
            if (s.equalsIgnoreCase("user id")) {
                userId = entries.get(s)
            }
            if (s.equalsIgnoreCase("Account Holder Consent")) {
                accountHolderConsent = entries.get(s)
            }

            if (s.equalsIgnoreCase("Course Type")) {
                courseType = entries.get(s)
            }
            if (s.equalsIgnoreCase("Original course start date")) {
                originalCourseStartDate = entries.get(s)
            }
        }
    }

    public String tocamelcase(String g) {
        StringBuilder sbl = new StringBuilder()

        String firstString
        String nextString
        String finalString = null
        char firstChar

        String[] f = g.split(" ")

        for (int e = 0; e < f.length; e++) {

            if (e == 0) {
                firstString = f[0].toLowerCase()
                sbl.append(firstString)

            }
            if (e > 0) {
                nextString = f[e].toLowerCase()
                firstChar = nextString.charAt(0)
                nextString = nextString.replaceFirst(firstChar.toString(), firstChar.toString().toUpperCase())
                sbl.append(nextString)
            }
            finalString = sbl.toString()

        }
        return finalString
    }


    public void validateJsonResult(DataTable arg) {
        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet()
        List<String> allKeys = new ArrayList()
        List<String> allJsonValue = new ArrayList()
        List<String> tableFieldValue = new ArrayList()
        List<String> tableFieldCamelCase = new ArrayList()
        JSONObject json = new JSONObject(jsonAsString);
        DecimalFormat df = new DecimalFormat("#.00")
        double value
        String innerJsonValue
        String jsonValue

        Iterator<String> jasonKey = json.keys()

        while (jasonKey.hasNext()) {
            String key = (String) jasonKey.next();

            if (json.get(key) instanceof JSONObject) {

                String innerValue = json.get(key)
                println "AAAAAAAAAAA" + innerValue
                JSONObject json2 = new JSONObject(innerValue)
                Iterator<String> feild = json2.keys()

                while (feild.hasNext()) {
                    String key2 = (String) feild.next()
                    if((key2 != "code")&&(key2 != "message")) {
                        allKeys.add(key2)

                        println "BBBBBBBBB" + key2
                    }
                    if (!(json2.get(key2) instanceof String)) {
                        double values = json2.getDouble(key2)
                        innerJsonValue = String.valueOf(df.format(values))
                        allJsonValue.add(innerJsonValue)
                        println "DDDDDDDDDDD" + innerJsonValue
                    }
                    innerJsonValue = json2.get(key2)
                    if ((key2 != "code") && (key2 != "message")) {
                        allJsonValue.add(innerJsonValue)
                    }
                }
            }
            if (!(json.get(key) instanceof JSONObject)) {

                jsonValue = json.get(key)
                if ((key == "minimum") || (key == "threshold")) {
                    value = json.getDouble(key)
                    jsonValue = String.valueOf(df.format(value))
                    allJsonValue.add(jsonValue)
                }
                allKeys.add(key)
                if (!(allJsonValue.contains(jsonValue))) {
                    allJsonValue.add(jsonValue)
                }
            }
        }
        for (String s : tableKey) {
            if (s != "HTTP Status") {
                tableFieldCamelCase.add(tocamelcase(s))
                tableFieldValue.add(entries.get(s))
            }
        }
        assert allKeys.containsAll(tableFieldCamelCase)
        assert allJsonValue.containsAll(tableFieldValue)
    }


    public void validateResult(DataTable arg) {

        Map<String, String> entries = arg.asMap(String.class, String.class);
        String[] tableKey = entries.keySet();

        for (String key : tableKey) {
            switch (key) {
                case "HTTP Status":
                    assert entries.get(key) == resp.getStatusCode().toString();
                    break;
                default:
                    String jsonPath = fkm.buildJsonPath(key)

                    assert entries.get(key) == read(jsonAsString, jsonPath).toString();
            }
        }
    }

    def responseStatusFor(String url) {
        Response response = given()
            .get(url)
            .then().extract().response();

        return response.getStatusCode();
    }


    @Given("^the test data for account (.+)\$")
    public void the_test_data_for_account_number(String accountNumber) {
        if (wiremock) {
            testDataLoader.stubTestData(accountNumber, balancesUrlRegex)
        } else {
            throw new RuntimeException("Trying to run wiremock step when not in wiremock mode - please ensure test is the active profile (annotion at the top of FinancialStatusApiSteps.groovy)")
        }
    }

    @Given("^a Service is consuming Financial Status API\$")
    public void a_Service_is_consuming_Financial_Status_API() {

    }

    @Given("^A Service is consuming the FSPS Calculator API\$")
    public void a_Service_is_consuming_the_FSPS_Calculator_API() {

    }

    @Given("^the barclays response has status (\\d+)\$")
    public void the_barclays_response_has_status(int status) {
        testDataLoader.withResponseStatus(balancesUrlRegex, status)
    }

    @Given("^the barclays api is unreachable\$")
    public void the_barclays_api_is_unreachable() {
        testDataLoader.withServiceDown()
    }

    @Given("^the default details are\$")
    public void the_default_details_are(DataTable arg1) {
        getTableData(arg1)

    }


    @When("^the Financial Status API is invoked\$")
    public void the_Financial_Status_API_is_invoked() {

        resp = get("http://localhost:" + serverPort + "/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus?fromDate={fromDate}&toDate={toDate}&minimum={minimum}&dob={dob}&userId={userId}&accountHolderConsent={accountHolderConsent}", sortCode, accountNumber, fromDate, toDate, minimum, dob, userId, accountHolderConsent)
        jsonAsString = resp.asString()

        println("Family Case Worker API: " + jsonAsString)

    }


    @When("^the Financial Status API is invoked with the following:\$")
    public void the_Financial_Status_API_is_invoked_with_the_following(DataTable arg1) {
        getTableData(arg1)
        resp = get("http://localhost:" + serverPort + "/pttg/financialstatusservice/v1/accounts/{sortCode}/{accountNumber}/dailybalancestatus?fromDate={fromDate}&toDate={toDate}&minimum={minimum}&dob={dob}&userId={userId}&accountHolderConsent={accountHolderConsent}", sortCode, accountNumber, fromDate, toDate, minimum, dob, userId, accountHolderConsent)
        jsonAsString = resp.asString()

        println("Family Case Worker API: " + jsonAsString)
    }

    @When("^the FSPS Calculator API is invoked with the following\$")
    public void the_FSPS_Calculator_API_is_invoked_with_the_following(DataTable arg1) {
        getTableData(arg1)
        resp = get("http://localhost:" + serverPort + "/pttg/financialstatusservice/v1/maintenance/threshold?studentType={studentType}&inLondon={inLondon}&courseStartDate={courseStartDate}&courseEndDate={courseEndDate}&continuationEndDate={continuationEndDate}&tuitionFees={tuitionFees}&tuitionFeesPaid={tuitionFeesPaid}&accommodationFeesPaid={accommodationFeesPaid}&dependants={dependants}&courseType={courseType}&originalCourseStartDate={originalCourseStartDate}", studentType, inLondon, courseStartDate, courseEndDate, continuationEndDate, tuitionFees, tuitionFeesPaid, accommodationFeesPaid, dependants, courseType, originalCourseStartDate)
        jsonAsString = resp.asString()

        println("FSPS API Calculator: " + jsonAsString)
    }

    @Then("^The Financial Status API provides the following results:\$")
    public void the_Financial_Status_API_provides_the_following_results(DataTable arg1) {
        validateJsonResult(arg1)
        // validateResult(arg1)


    }

    @Then("^FSPS Tier four general Case Worker tool API provides the following result\$")
    public void fsps_Tier_four_general_Case_Worker_tool_API_provides_the_following_result(DataTable arg1) {
        validateResult(arg1)

    }

    @Then("^the service displays the following result\$")
    public void the_service_displays_the_following_result(DataTable arg1) {
        validateResult(arg1)
    }

    @Then("^the health check response status should be (\\d+)\$")
    def the_response_status_should_be(int expected) {

        def result = getHealthCheckStatus()

        // Sometimes needs a retry, not sure why
        2.times {
            if (result != expected) {
                sleep(500)
                result = getHealthCheckStatus()
            }
        }

        assert result == expected
    }

    private int getHealthCheckStatus() {
        responseStatusFor("http://localhost:" + serverPort + "/healthz")
    }

}
