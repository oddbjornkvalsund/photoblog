package no.nixx.photoblog.servlet;

import no.nixx.photoblog.data.Image;
import no.nixx.photoblog.data.Post;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PhotosServlet extends HttpServlet {

    public PhotosServlet() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String id = request.getParameter("id");
        if (id == null) {
            throw new RuntimeException("Photo id must be specified!!");
        }

        final File file = new File(PostServlet.DATA_DIR, id); // Perform sanity check on the photo id string
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

        // This is called in parallel/separate calls, one for each file upload

        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new IllegalArgumentException("Request is not multipart, please use 'multipart/form-data' enctype for your form.");
        }

        final ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());

        try {
            final Map<String, List<FileItem>> parameterMap = uploadHandler.parseParameterMap(request);
            final String postId = getPostId(parameterMap);
            final FileItem imageFileItem = getImageFile(parameterMap);

            addImageToPost(postId, imageFileItem);
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }
    }

    private String getPostId(Map<String, List<FileItem>> parameterMap) {
        final List<FileItem> fileItems = parameterMap.get("postId");

        if (fileItems.size() != 1) {
            throw new IllegalArgumentException("Number of postIds must be exactly 1, but was: " + fileItems.size());
        }

        final FileItem postIdFileItem = fileItems.iterator().next();
        if (!postIdFileItem.isFormField()) {
            throw new IllegalArgumentException("postId cannot be file, must be form field!");

        }

        return postIdFileItem.getString();
    }

    private FileItem getImageFile(Map<String, List<FileItem>> parameterMap) {
        final List<FileItem> fileItems = parameterMap.get("files[]");

        if (fileItems.size() != 1) {
            throw new IllegalArgumentException("Number of files must be exactly 1, but was: " + fileItems.size());
        }

        final FileItem imageFileItem = fileItems.iterator().next();
        if (imageFileItem.isFormField()) {
            throw new IllegalArgumentException("File cannot be form field, must be file!");
        }

        final List<String> legalFileTypes = Arrays.asList("image/png", "image/jpg", "image/jpeg", "image/gif", "image/bmp");
        if (!legalFileTypes.contains(imageFileItem.getContentType())) {
            throw new IllegalArgumentException("Illegal file type: " + imageFileItem.getContentType());
        }

        return imageFileItem;
    }

    private synchronized void addImageToPost(String postId, FileItem imageFileItem) {
        final File postDir = new File(PostServlet.DATA_DIR, postId);

        final File postFile = new File(postDir, "post.json");
        if (!postFile.exists()) {
            throw new IllegalArgumentException("No such post: " + postId);
        }

        final File imageFile = new File(postDir, getSanitizedFileName(imageFileItem));
        try {
            imageFileItem.write(imageFile);
        } catch (Exception e) {
            throw new RuntimeException("Unable to write uploaded file to file: " + imageFile, e);
        }

        final Image image = new Image(imageFile.getName(), "");// TODO: Empty orientation
        final Post post = Post.parseFromFile(postFile);
        post.images.add(image);
        post.writeToFile(postFile);
    }

    private String getSanitizedFileName(FileItem imageFileItem) {
        return imageFileItem.getName()
                .replaceAll("\u00e6", "Ae")
                .replaceAll("\u00c6", "ae")
                .replaceAll("\u00d8", "Oe")
                .replaceAll("\u00f8", "oe")
                .replaceAll("\u00c5", "Aa")
                .replaceAll("\u00e5", "aa")
                .replaceAll("[^0-9a-zA-Z\\.]", "");
    }
}