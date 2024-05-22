package com.Douane.backendPFE.services.flickr;
import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.uploader.UploadMetaData;
import com.flickr4java.flickr.uploader.Uploader;
import lombok.SneakyThrows;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

@Service
public class FlickrServiceImp implements FlickrService{
    @Value("${flicker.apiKey}")
    private  String apiKey;
    @Value("${flicker.apiSecret}")
    private String apiSecret;

    @Value("${flicker.appKey}")
    private  String appKey;
    @Value("${flicker.appSecret}")
    private String appSecret;
    private Flickr flickr;


    @Override
    @SneakyThrows
    public String savePhoto(InputStream photo, String title) {
        connect();

        Uploader uploader = flickr.getUploader();
        UploadMetaData uploadMetaData = new UploadMetaData();
        uploadMetaData.setTitle(title);

        String mediaId = uploader.upload(photo, uploadMetaData);

        // Obtenez l'URL du média uploadé
        Photo photos = flickr.getPhotosInterface().getPhoto(mediaId);
        String mediaUrl = photos.getMediumUrl();

        return mediaUrl;
    }

    private void connect() throws InterruptedException, ExecutionException, IOException, FlickrException {
        flickr = new Flickr(apiKey, apiSecret, new REST());
        Auth auth = new Auth();
        auth.setPermission(Permission.READ);
        auth.setToken(appKey);
        auth.setTokenSecret(appSecret);
        RequestContext requestContext = RequestContext.getRequestContext();
        requestContext.setAuth(auth);
        flickr.setAuth(auth);
    }

}
