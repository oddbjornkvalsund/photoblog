package no.nixx.photoblog.servlet;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class PostServlet extends HttpServlet {

    final public static File DATA_DIR = new File("C:/Temp/images/");
    public static final String FILE_ENCODING = "UTF-8";

    public PostServlet() {
        super();

        if (!DATA_DIR.exists()) {
            DATA_DIR.mkdirs();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String date = request.getParameter("date");
        final String title = request.getParameter("title");
        final String description = request.getParameter("description");
        final String postId = getPostId(date, title);

        createPost(postId, date, title, description);

        try {
            final JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("postId", postId);

            response.getWriter().write(jsonResponse.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void createPost(String postId, String date, String title, String description) throws IOException {
        final File postDir = new File(DATA_DIR, postId);
        final File postFile = new File(postDir, "post.json");

        if (postDir.exists()) {
            throw new IllegalStateException("Post with that id already exists!");
        }

        if (!postDir.mkdir()) {
            throw new IOException("Could not create directory for post with id = " + postId);
        }

        if (!postFile.createNewFile()) {
            throw new IOException("Could not create file for post with id = " + postId);
        }

        try {
            final JSONObject postContent = new JSONObject();
            postContent.put("date", date);
            postContent.put("title", title);
            postContent.put("description", description);
            postContent.put("files", Collections.EMPTY_LIST);

            FileUtils.write(postFile, postContent.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPostId(String date, String title) {
        return date + "_" + title.replaceAll("\\s", "_").replaceAll("[^A-Za-z]", "");
    }
}