package io.contentchef.sample;

/**
 * Model for the content SampleHeader as specified on ContentChef's dashboard
 */
class SampleHeader {

    public SampleHeader(String header) {
        this.header = header;
    }

    private final String header;

    public String getHeader() {
        return header;
    }

}
