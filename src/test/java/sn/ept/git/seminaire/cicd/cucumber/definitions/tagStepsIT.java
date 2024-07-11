package sn.ept.git.seminaire.cicd.cucumber.definitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import sn.ept.git.seminaire.cicd.ReplaceCamelCase;
import sn.ept.git.seminaire.cicd.models.TagDTO;
import sn.ept.git.seminaire.cicd.resources.TagResource;
import sn.ept.git.seminaire.cicd.services.impl.TagServiceImpl;
import sn.ept.git.seminaire.cicd.utils.TestUtil;
import sn.ept.git.seminaire.cicd.utils.UrlMapping;

import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(TagResource.class)
@DisplayNameGeneration(ReplaceCamelCase.class)
public class tagStepsIT {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private TagServiceImpl service;

    private TagDTO tagDTO;
    private String tagId;

    @Before
    public void setUp() {
        tagDTO = TagDTO.builder()
                .id(UUID.randomUUID().toString())
                .name("Work")
                .build();
    }

    @Given("tag name is {string}")
    public void givenTagName(String name) {
        tagDTO.setName(name);
    }

    @When("call add tag")
    public void whenCallAddTag() throws Exception {
        Mockito.when(service.save(Mockito.any())).thenReturn(tagDTO);
        mockMvc.perform(post(UrlMapping.Tag.ADD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(tagDTO.getName()));
    }

    @Then("the returned http status is {int}")
    public void thenTheReturnedHttpStatusIs(int status) throws Exception {
        mockMvc.perform(post(UrlMapping.Tag.ADD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
                .andExpect(status().is(status));
    }

    @Then("the returned tag has name {string}")
    public void thenTheReturnedTagHasName(String name) throws Exception {
        mockMvc.perform(post(UrlMapping.Tag.ADD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Given("tags table contains data:")
    public void givenTagsTableContainsData(io.cucumber.datatable.DataTable dataTable) {
        // Use the DataTable to set up the initial data state
        dataTable.asMaps().forEach(row -> {
            tagDTO.setId(row.get("id"));
            tagDTO.setName(row.get("name"));
        });
    }

    @When("call find tag by id with id={string}")
    public void whenCallFindTagByIdWithId(String id) throws Exception {
        Mockito.when(service.findById(Mockito.any())).thenReturn(Optional.ofNullable(tagDTO));
        mockMvc.perform(get(UrlMapping.Tag.FIND, id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(tagDTO.getName()));
    }

    @When("call update tag with id={string}")
    public void whenCallUpdateTagWithId(String id) throws Exception {
        tagDTO.setName("Home");
        Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(tagDTO);
        mockMvc.perform(put(UrlMapping.Tag.UPDATE, id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value(tagDTO.getName()));
    }

    @When("call delete tag with id={string}")
    public void whenCallDeleteTagWithId(String id) throws Exception {
        Mockito.doNothing().when(service).delete(Mockito.any());
        mockMvc.perform(delete(UrlMapping.Tag.DELETE, id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
