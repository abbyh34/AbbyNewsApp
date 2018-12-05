package com.example.android.news24;

public class News {

    // Title
    private String mTitle;

    // Publish date
    private String mDate = NO_DATE_PROVIDED;

    // Section
    private String mSection;

    // Author
    private String mAuthor = NO_AUTHOR_PROVIDED;

    // Url
    private String mUrl;

    // No date provided
    private static final String NO_DATE_PROVIDED = null;

    // No author provided
    private static final String NO_AUTHOR_PROVIDED = null;

    /**
     * create a new News object
     *
     * @param title is title of the article
     * @param date is the date of the article
     * @param section is the section the article is in
     * @param author is the author of the article
     * @param url is website of article
     */

    public News(String title, String date, String section, String author, String url) {
        mTitle = title;
        mDate = date;
        mSection = section;
        mAuthor = author;
        mUrl = url;
    }

    /**
     * create a new News object for no author or date
     *
     * @param title is title of the article
     * @param section is the section the article is in
     * @param url is website of article
     */

    public News(String title, String section, String url) {
        mTitle = title;
        mSection = section;
        mUrl = url;
    }

    // Getters

    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }

    public String getSection() {
        return mSection;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    // Date and author booleans for error
    public boolean hasDate() {
        return mDate != NO_DATE_PROVIDED;
    }

    public boolean hasAuthor() {
        return mAuthor != NO_AUTHOR_PROVIDED;
    }

}