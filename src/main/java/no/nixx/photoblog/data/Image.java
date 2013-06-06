package no.nixx.photoblog.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * Oddbjørn Kvalsund
 */
public class Image implements JSONString {

    public String filename;
    public String orientation;

    public Image() {
    }

    public Image(String filename, String orientation) {
        this.filename = filename;
        this.orientation = orientation;
    }

    public JSONObject toJSONObject() {
        try {
            final JSONObject post = new JSONObject();
            post.put("filename", this.filename);
            post.put("orientation", this.orientation);

            return post;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toJSONString() {
        return toJSONObject().toString();
    }

    @Override
    public String toString() {
        return toJSONString();
    }
}
