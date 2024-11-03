package com.nhlstenden.reader2.services;

import com.nhlstenden.reader2.models.Chapter;
import com.nhlstenden.reader2.models.Serie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImportServiceTest {

    private ImportService importService;

    @BeforeEach
    void setUp() {
        importService = new ImportService();
    }

    @Test
    void updateSerieModel_createsNewSerieModel() {
        List<File> comicFiles = List.of(new File("comic1.cbz"), new File("comic2.cbz"));
        importService.updateSerieModel("SerieName", "Description", comicFiles);

        Serie serieModel = importService.getSerieModel();
        assertNotNull(serieModel);
        assertEquals("SerieName", serieModel.getName());
        assertEquals("Description", serieModel.getDescription());
        assertEquals(2, serieModel.getChapters().size());
    }

    @Test
    void updateSerieModel_updatesExistingSerieModel() {
        List<File> comicFiles = List.of(new File("comic1.cbz"), new File("comic2.cbz"));
        importService.updateSerieModel("SerieName", "Description", comicFiles);

        List<File> newComicFiles = List.of(new File("comic3.cbz"));
        importService.updateSerieModel("SerieName", "New Description", newComicFiles);

        Serie serieModel = importService.getSerieModel();
        assertNotNull(serieModel);
        assertEquals("SerieName", serieModel.getName());
        assertEquals("New Description", serieModel.getDescription());
        assertEquals(1, serieModel.getChapters().size());
    }

    @Test
    void updateSerieModel_sortsChaptersByTitle() {
        List<File> comicFiles = List.of(new File("comicB.cbz"), new File("comicA.cbz"));
        importService.updateSerieModel("SerieName", "Description", comicFiles);

        Serie serieModel = importService.getSerieModel();
        assertNotNull(serieModel);
        List<Chapter> chapters = serieModel.getChapters();
        assertEquals("comicA.cbz", chapters.get(0).getTitle());
        assertEquals("comicB.cbz", chapters.get(1).getTitle());
    }

    @Test
    void updateSerieModel_handlesEmptyComicFiles() {
        List<File> comicFiles = new ArrayList<>();
        importService.updateSerieModel("SerieName", "Description", comicFiles);

        Serie serieModel = importService.getSerieModel();
        assertNotNull(serieModel);
        assertEquals("SerieName", serieModel.getName());
        assertEquals("Description", serieModel.getDescription());
        assertTrue(serieModel.getChapters().isEmpty());
    }

}