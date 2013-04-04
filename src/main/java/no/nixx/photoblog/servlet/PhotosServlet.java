package no.nixx.photoblog.servlet;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpUtils;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PhotosServlet extends HttpServlet {

    final String dataDir = "C:/Temp/images/";
    final SimpleDateFormat postIdDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public PhotosServlet() {
        super();

        final File dataDirFile = new File(dataDir);
        if (!dataDirFile.exists()) {
            dataDirFile.mkdirs();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String id = request.getParameter("id");
        if (id == null) {
            throw new RuntimeException("Photo id must be specified!!");
        }

        final File file = new File(dataDir, id); // Perform sanity check on the photo id string
        final PrintWriter writer = response.getWriter();
        if (file.exists()) {
            final BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));

            // Set content type and length
            // Read from input and write to writer
        }

        writer.write("Hello!");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // TODO: This is called in parallel, one for each file upload, must fix

        final String postId = generatePostId();

        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please use 'multipart/form-data' enctype for your form.");
        }

        String title = "";
        String date = "";
        String description = "";
        final List<File> files = new ArrayList<File>();

        final ServletFileUpload uploadHandler = new ServletFileUpload(new DefaultFileItemFactory());

        try {
            final List<FileItem> items = uploadHandler.parseRequest(request);
            for (FileItem item : items) {
                if (item.isFormField()) {
                    final String fieldName = item.getFieldName();
                    if (fieldName.equals("title")) {
                        title = item.getString();
                    } else if (fieldName.equals("datepicker")) {
                        date = item.getString();
                    } else if (fieldName.equals("description")) {
                        description = item.getString();
                    } else {
                        throw new RuntimeException("Uknown field name: " + fieldName);
                    }
                } else {
                    final File file = new File(dataDir, item.getName());
                    files.add(file);
                    item.write(file);
                }
            }

            final PrintWriter writer = response.getWriter();
            writer.write(generateJSONResponse(postId, files));

        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }

        // TODO: Save post data to json-file
        try {
            final JSONObject postData = new JSONObject();
            postData.put("title", title);
            postData.put("date", date);
            postData.put("description", description);
            final JSONArray postFiles = new JSONArray();
            for (File file : files) {
                final JSONObject photo = new JSONObject();
                photo.put("file", file.getName());
                postFiles.put(photo);
            }
            postData.put("files", postFiles);

            System.out.println(postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //response.sendRedirect("/edit?id=" + postId);

    }


    private String generateJSONResponse(String postId, List<File> files) throws Exception {
        final JSONArray json = new JSONArray();

        for (File file : files) {
            JSONObject jsono = new JSONObject();
            jsono.put("post_id", postId);
            jsono.put("name", file.getName());
            jsono.put("size", file.getTotalSpace());
            jsono.put("url", "/photos?id=" + file.getName());
            jsono.put("thumbnail_url", "upload?getthumb=" + file.getName());
            jsono.put("delete_url", "upload?delfile=" + file.getName());
            jsono.put("delete_type", "GET");
            json.put(jsono);
        }

        return json.toString();
    }


    private String generatePostId() {
        final UUID uuid = UUID.randomUUID();

        return String.format("%s-%s", postIdDateFormat.format(new Date()), uuid);
    }
}