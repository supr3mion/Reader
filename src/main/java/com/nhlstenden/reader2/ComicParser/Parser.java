package com.nhlstenden.reader2.ComicParser;

import com.nhlstenden.reader2.models.Chapter;

import java.io.File;

/**
 * The Parser class is responsible for parsing different types of compressed comic files
 * and extracting their contents into a Chapter object.
 */
public class Parser {

    private File CompressedComic;
    private Chapter Chapter;

    /**
     * Parses the selected compressed comic file and extracts its contents into the Chapter object.
     *
     * @return The Chapter object with the extracted contents.
     * @throws Exception If no file is selected, no chapter is given, or the file type is unsupported.
     */
    public Chapter parse() throws Exception {
        // Check if a compressed comic file is selected
        if (this.CompressedComic == null) {
            throw new Exception("No file selected");
        }

        // Check if a Chapter object is provided
        if (this.Chapter == null) {
            throw new Exception("No chapter given");
        }

        // Determine the file type and use the appropriate parser
        if (this.CompressedComic.getName().endsWith(".cbz") || this.CompressedComic.getName().endsWith(".zip")) {
            // Use ZipParser for .cbz or .zip files
            ZipParser zipParser = new ZipParser();
            this.Chapter = zipParser.parse(this.CompressedComic, this.Chapter);
            zipParser = null;
            System.gc();
        } else if (this.CompressedComic.getName().endsWith(".cbr")) {
            // Use RarParser for .cbr files
            RarParser rarParser = new RarParser();
            this.Chapter = rarParser.parse(this.CompressedComic, this.Chapter);
            rarParser = null;
            System.gc();
        } else if (this.CompressedComic.getName().endsWith(".nhlcomic")) {
            // Use GifParser for .nhlcomic files
            GifParser gifParser = new GifParser();
            this.Chapter = gifParser.parse(this.CompressedComic, this.Chapter);
        } else {
            // Throw an exception for unsupported file types
            throw new Exception("Unsupported file type");
        }

        // Return the Chapter object with the extracted contents
        return this.Chapter;
    }

    /**
     * Sets the compressed comic file to be parsed.
     *
     * @param compressedComic The compressed comic file.
     */
    public void setCompressedComic(File compressedComic) {
        this.CompressedComic = compressedComic;
    }

    /**
     * Gets the compressed comic file to be parsed.
     *
     * @return The compressed comic file.
     */
    public File getCompressedComic() {
        return this.CompressedComic;
    }

    /**
     * Sets the Chapter object to which the extracted contents will be added.
     *
     * @param Chapter The Chapter object.
     */
    public void setChapter(Chapter Chapter) {
        this.Chapter = Chapter;
    }
}