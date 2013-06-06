package no.nixx.photoblog.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

/**
 * Oddbjørn Kvalsund
 */
public class PostId implements JSONString {
    public String postId;

    public PostId() {
    }

    public PostId(String postId) {
        this.postId = postId;
    }

    public JSONObject toJSONObject() {
        try {
            final JSONObject post = new JSONObject();
            post.put("postId", this.postId);

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