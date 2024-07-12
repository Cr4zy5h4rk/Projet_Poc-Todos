package sn.ept.git.seminaire.cicd.cucumber.definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import sn.ept.git.seminaire.cicd.models.TagDTO;
import sn.ept.git.seminaire.cicd.repositories.TagRepository;
import sn.ept.git.seminaire.cicd.entities.Tag;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
//test
@Slf4j
public class TagStepIT {

    private final static String BASE_URI = "http://localhost";
    public static final String API_PATH = "/cicd/api/tags";

    @LocalServerPort
    private int port;

    private String tagName;
    private Response response;

    @Autowired
    private TagRepository tagRepository;

    @Before
    public void init() {
        tagRepository.deleteAll();
    }

    protected RequestSpecification request() {
        RestAssured.baseURI = BASE_URI;
        RestAssured.port = port;
        return given()
                .contentType(ContentType.JSON)
                .log()
                .all();
    }

    @Given("tag name is {string}")
    public void tagNameIs(String name) {
        this.tagName = name;
    }

    @Given("tags table contains data:")
    public void tagsTableContainsData(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        List<Tag> tags = data.stream()
                .map(line -> Tag.builder()
                        .id(line.get("id"))
                        .name(line.get("name"))
                        .build())
                .collect(Collectors.toList());
        tagRepository.saveAllAndFlush(tags);
    }

    @When("call add tag")
    public void callAddTag() {
        TagDTO requestBody = TagDTO.builder().name(this.tagName).build();
        response = request()
                .body(requestBody)
                .when().post(API_PATH);
    }

    @When("call find tag by id with id={string}")
    public void callFindTagByIdWithId(String id) {
        response = request()
                .when().get(API_PATH + "/" + id);
    }

    @When("call update tag with id={string}")
    public void callUpdateTagWithId(String id) {
        TagDTO requestBody = TagDTO.builder().name(this.tagName).build();
        response = request()
                .body(requestBody)
                .when().put(API_PATH + "/" + id);
    }

    @When("call delete tag with id={string}")
    public void callDeleteTagWithId(String id) {
        response = request()
                .when().delete(API_PATH + "/" + id);
    }

    @When("call find all tags with page = {int} and size = {int} and sort={string}")
    public void callFindAllTags(int page, int size, String sort) {
        response = request()
                .when().get(API_PATH + String.format("?page=%d&size=%d&sort=%s", page, size, sort));
    }

    @Then("the returned tag http status is {int}")
    public void theReturnedTagHttpStatusIs(int status) {
        response.then()
                .assertThat()
                .statusCode(status);
    }

    @And("the returned tag has name {string}")
    public void theReturnedTagHasName(String name) {
        response.then()
                .assertThat()
                .body("name", equalTo(name));
    }

    @And("the returned tags list has {int} elements")
    public void theReturnedTagsListHasElements(int size) {
        response.then()
                .assertThat()
                .body("content.size()", equalTo(size));
    }

    @And("that tags list contains values:")
    public void thatTagsListContainsValues(io.cucumber.datatable.DataTable dataTable) {
        List<Map<String, String>> data = dataTable.asMaps(String.class, String.class);
        data.forEach(line -> response.then().assertThat()
                .body("content.name", hasItem(line.get("name").trim())));
    }

    @Given("tag name contains {int} characters")
    public void tagNameContainsCharacters(int size) {
        this.tagName = RandomStringUtils.randomAlphabetic(size);
    }
}