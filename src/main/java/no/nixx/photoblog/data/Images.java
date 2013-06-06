package no.nixx.photoblog.data;

import org.json.JSONArray;
import org.json.JSONString;

import java.util.ArrayList;
import java.util.List;

public class Images implements JSONString {

    public List<Image> images = new ArrayList<Image>();

    public Images() {
    }

    public Images(List<Image> images) {
        this.images = images;
    }

    public void add(Image image) {
        images.add(image);
    }

    public JSONArray toJSONArray() {
        final JSONArray jsonArray = new JSONArray();

        for (Image image : images) {
            jsonArray.put(image);
        }

        return jsonArray;
    }

    @Override
    public String toJSONString() {
        return toJSONArray().toString();
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}