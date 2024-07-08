package sn.ept.git.seminaire.cicd.cucumber.definitions;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
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
class tagStepsIT {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    private TagServiceImpl service;

    private TagDTO tagDTO;

    @BeforeEach
    void beforeEach() {
        tagDTO = TagDTO.builder()
                .id(UUID.randomUUID().toString())
                .name("Work")
                .build();
    }

    @Test
    void addTag_shouldReturn201() throws Exception {
        Mockito.when(service.save(Mockito.any())).thenReturn(tagDTO);
        mockMvc.perform(post(UrlMapping.Tag.ADD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(tagDTO.getName()));
    }

    @Test
    void findTagById_shouldReturnTag() throws Exception {
        Mockito.when(service.findById(Mockito.any())).thenReturn(Optional.ofNullable(tagDTO));
        mockMvc.perform(get(UrlMapping.Tag.FIND, tagDTO.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(tagDTO.getName()));
    }

    @Test
    void updateTag_shouldReturn202() throws Exception {
        tagDTO.setName("Home");
        Mockito.when(service.update(Mockito.any(), Mockito.any())).thenReturn(tagDTO);
        mockMvc.perform(put(UrlMapping.Tag.UPDATE, tagDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TestUtil.convertObjectToJsonBytes(tagDTO)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.name").value(tagDTO.getName()));
    }


    @Test
    void deleteTagById_shouldReturn202() throws Exception {
        Mockito.doNothing().when(service).delete(Mockito.any());
        mockMvc.perform(delete(UrlMapping.Tag.DELETE, tagDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
