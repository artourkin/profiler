package com.ifs.megaprofiler.elements;

import java.util.List;

/**
 * Created by artur on 4/10/14.
 */
public class Endpoint {
    private List<String> sources;

    public Endpoint(List<String> sources) {
        this.sources = sources;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }
}
