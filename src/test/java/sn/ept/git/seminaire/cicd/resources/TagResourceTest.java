package sn.ept.git.seminaire.cicd.resources;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import sn.ept.git.seminaire.cicd.models.TagDTO;
import sn.ept.git.seminaire.cicd.services.ITagService;
import sn.ept.git.seminaire.cicd.utils.UrlMapping;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TagResource.class)
class TagResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITagService service;

    @Test
    void findAll_ShouldReturnAllTags() throws Exception {
        TagDTO tag = new TagDTO();
        tag.setId(UUID.randomUUID().toString());
        tag.setName("Test Tag");
        tag.setDescription("Test Description");
        Page<TagDTO> page = new PageImpl<>(List.of(tag), PageRequest.of(0, 10), 1);

        when(service.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get(UrlMapping.Tag.ALL)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Tag"));
    }

    @Test
    void findById_ShouldReturnTag() throws Exception {
        TagDTO tag = new TagDTO();
        tag.setId(UUID.randomUUID().toString());
        tag.setName("Test Tag");
        tag.setDescription("Test Description");

        when(service.findById(tag.getId())).thenReturn(Optional.of(tag));

        mockMvc.perform(MockMvcRequestBuilders.get(UrlMapping.Tag.FIND_BY_ID, tag.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Tag"));
    }

    @Test
    void findById_ShouldReturnNotFound() throws Exception {
        when(service.findById(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get(UrlMapping.Tag.FIND_BY_ID, UUID.randomUUID().toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_ShouldCreateTag() throws Exception {
        TagDTO tag = new TagDTO();
        tag.setId(UUID.randomUUID().toString());
        tag.setName("Test Tag");
        tag.setDescription("Test Description");

        when(service.save(any(TagDTO.class))).thenReturn(tag);

        mockMvc.perform(MockMvcRequestBuilders.post(UrlMapping.Tag.ADD)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Test Tag\", \"description\": \"Test Description\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.name").value("Test Tag"));
    }

    @Test
    void delete_ShouldDeleteTag() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete(UrlMapping.Tag.DELETE, UUID.randomUUID().toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_ShouldUpdateTag() throws Exception {
        TagDTO tag = new TagDTO();
        tag.setId(UUID.randomUUID().toString());
        tag.setName("Updated Tag");
        tag.setDescription("Updated Description");

        when(service.update(any(String.class), any(TagDTO.class))).thenReturn(tag);

        mockMvc.perform(MockMvcRequestBuilders.put(UrlMapping.Tag.UPDATE, tag.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Updated Tag\", \"description\": \"Updated Description\"}"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.name").value("Updated Tag"));
    }
}
