package no.nixx.photoblog.data;

import no.nixx.photoblog.servlet.PostServlet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Oddbjørn Kvalsund
 */
public class Post implements JSONString {

    public String postId;
    public String title;
    public String date;
    public String description;
    public Images images;

    public Post() {
    }

    public Post(String postId, String title, String date, String description) {
        this.postId = postId;
        this.title = title;
        this.date = date;
        this.description = description;
        this.images = new Images();
    }

    public Post(String postId, String title, String date, String description, Images images) {
        this.postId = postId;
        this.title = title;
        this.date = date;
        this.description = description;
        this.images = images;
    }

    public static Post parseFromString(String string) {
        try {
            final JSONObject jsonObject = new JSONObject(string);
            final Post post = new Post();
            post.postId = jsonObject.getString("postId");
            post.title = jsonObject.getString("title");
            post.date = jsonObject.getString("date");
            post.description = jsonObject.getString("description");

            final Images images = new Images();
            final JSONArray files = jsonObject.getJSONArray("images");
            for(int i = 0; i < files.length(); i++) {
                final JSONObject file = files.getJSONObject(i);
                final String filename = file.getString("filename");
                final String orientation = file.getString("orientation");

                final Image image = new Image(filename, orientation);
                images.add(image);
            }
            post.images = images;

            return post;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Post parseFromFile(File file) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final String content = IOUtils.toString(fileInputStream, PostServlet.FILE_ENCODING);

            return parseFromString(content);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void writeToFile(File file) {
        try {
            FileUtils.write(file, this.toString(), PostServlet.FILE_ENCODING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public JSONObject toJSONObject() {
        try {
            final JSONObject post = new JSONObject();
            post.put("postId", this.postId);
            post.put("date", this.date);
            post.put("title", this.title);
            post.put("description", this.description);
            post.put("images", this.images);

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
