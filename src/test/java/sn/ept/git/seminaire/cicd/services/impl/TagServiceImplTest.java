package sn.ept.git.seminaire.cicd.services.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sn.ept.git.seminaire.cicd.entities.Tag;
import sn.ept.git.seminaire.cicd.exceptions.ItemExistsException;
import sn.ept.git.seminaire.cicd.exceptions.ItemNotFoundException;
import sn.ept.git.seminaire.cicd.mappers.TagMapper;
import sn.ept.git.seminaire.cicd.models.TagDTO;
import sn.ept.git.seminaire.cicd.repositories.TagRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository repository;

    @Mock
    private TagMapper mapper;

    @InjectMocks
    private TagServiceImpl service;

    private Tag tag;
    private TagDTO tagDTO;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(UUID.randomUUID().toString());
        tag.setName("Test Tag");
        tag.setDescription("Test Description");

        tagDTO = new TagDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        tagDTO.setDescription(tag.getDescription());
    }

    @Test
    void save_ShouldSaveTag() {
        when(repository.findByName(tagDTO.getName())).thenReturn(Optional.empty());
        when(mapper.toEntity(any(TagDTO.class))).thenReturn(tag);
        when(mapper.toDTO(any(Tag.class))).thenReturn(tagDTO);
        when(repository.saveAndFlush(any(Tag.class))).thenReturn(tag);

        TagDTO savedTag = service.save(tagDTO);

        assertNotNull(savedTag);
        assertEquals(tagDTO.getName(), savedTag.getName());
        verify(repository, times(1)).findByName(tagDTO.getName());
        verify(repository, times(1)).saveAndFlush(any(Tag.class));
    }

    @Test
    void save_ShouldThrowItemExistsException() {
        when(repository.findByName(tagDTO.getName())).thenReturn(Optional.of(tag));

        assertThrows(ItemExistsException.class, () -> service.save(tagDTO));
        verify(repository, times(1)).findByName(tagDTO.getName());
    }

    @Test
    void delete_ShouldDeleteTag() {
        when(repository.findById(tag.getId())).thenReturn(Optional.of(tag));

        service.delete(tag.getId());

        verify(repository, times(1)).findById(tag.getId());
        verify(repository, times(1)).deleteById(tag.getId());
    }

    @Test
    void delete_ShouldThrowItemNotFoundException() {
        when(repository.findById(tag.getId())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.delete(tag.getId()));
        verify(repository, times(1)).findById(tag.getId());
    }

    @Test
    void findById_ShouldReturnTag() {
        when(repository.findById(tag.getId())).thenReturn(Optional.of(tag));
        when(mapper.toDTO(any(Tag.class))).thenReturn(tagDTO);

        Optional<TagDTO> foundTag = service.findById(tag.getId());

        assertTrue(foundTag.isPresent());
        assertEquals(tagDTO.getName(), foundTag.get().getName());
        verify(repository, times(1)).findById(tag.getId());
    }

    @Test
    void findById_ShouldReturnEmpty() {
        when(repository.findById(tag.getId())).thenReturn(Optional.empty());

        Optional<TagDTO> foundTag = service.findById(tag.getId());

        assertFalse(foundTag.isPresent());
        verify(repository, times(1)).findById(tag.getId());
    }

    @Test
    void findAll_ShouldReturnAllTags() {
        List<Tag> tags = List.of(tag);
        List<TagDTO> tagDTOs = List.of(tagDTO);
        when(repository.findAll()).thenReturn(tags);
        when(mapper.toDTO(any(Tag.class))).thenReturn(tagDTO);

        List<TagDTO> foundTags = service.findAll();

        assertNotNull(foundTags);
        assertEquals(1, foundTags.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findAllWithPagination_ShouldReturnPagedTags() {
        List<Tag> tags = List.of(tag);
        List<TagDTO> tagDTOs = List.of(tagDTO);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Tag> tagPage = new PageImpl<>(tags, pageable, tags.size());
        when(repository.findAll(any(Pageable.class))).thenReturn(tagPage);
        when(mapper.toDTO(any(Tag.class))).thenReturn(tagDTO);

        Page<TagDTO> foundTags = service.findAll(pageable);

        assertNotNull(foundTags);
        assertEquals(1, foundTags.getTotalElements());
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void update_ShouldUpdateTag() {
        when(repository.findById(tag.getId())).thenReturn(Optional.of(tag));
        when(repository.findByNameWithIdNotEquals(tagDTO.getName(), tag.getId())).thenReturn(Optional.empty());
        when(mapper.toDTO(any(Tag.class))).thenReturn(tagDTO);
        when(repository.saveAndFlush(any(Tag.class))).thenReturn(tag);

        TagDTO updatedTag = service.update(tag.getId(), tagDTO);

        assertNotNull(updatedTag);
        assertEquals(tagDTO.getName(), updatedTag.getName());
        verify(repository, times(1)).findById(tag.getId());
        verify(repository, times(1)).saveAndFlush(any(Tag.class));
    }

    @Test
    void update_ShouldThrowItemNotFoundException() {
        when(repository.findById(tag.getId())).thenReturn(Optional.empty());

        assertThrows(ItemNotFoundException.class, () -> service.update(tag.getId(), tagDTO));
        verify(repository, times(1)).findById(tag.getId());
    }

    @Test
    void update_ShouldThrowItemExistsException() {
        when(repository.findById(tag.getId())).thenReturn(Optional.of(tag));
        when(repository.findByNameWithIdNotEquals(tagDTO.getName(), tag.getId())).thenReturn(Optional.of(tag));

        assertThrows(ItemExistsException.class, () -> service.update(tag.getId(), tagDTO));
        verify(repository, times(1)).findById(tag.getId());
        verify(repository, times(1)).findByNameWithIdNotEquals(tagDTO.getName(), tag.getId());
    }

    @Test
    void deleteAll_ShouldDeleteAllTags() {
        service.deleteAll();

        verify(repository, times(1)).deleteAll();
    }
}