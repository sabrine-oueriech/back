package com.Douane.backendPFE.config;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.RequestContext;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.Permission;
import com.github.scribejava.apis.FlickrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.oauth.OAuth10aService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

@Configuration
public class FlickrConfig {

    @Value("${flicker.apiKey}")
    private  String apiKey;
    @Value("${flicker.apiSecret}")
    private String apiSecret;

    @Value("${flicker.appKey}")
    private  String appKey;
    @Value("${flicker.appSecret}")
    private String appSecret;

    /* @Bean
     public Flickr getFlickr() throws IOException, ExecutionException, InterruptedException, FlickrException {
         Flickr flickr=new Flickr(apiKey,apiSecret, new REST());

         OAuth10aService service=new ServiceBuilder(apiKey)
                 .apiSecret(apiSecret)
                 .build(FlickrApi.instance(FlickrApi.FlickrPerm.DELETE));
         final Scanner scanner=new Scanner(System.in);
         final OAuth1RequestToken requestToken=service.getRequestToken();
         final String authUrl= service.getAuthorizationUrl(requestToken);

         System.out.println(authUrl);
         System.out.println("Paste in here >>>> ");

         final String authVerifier=scanner.nextLine();

         OAuth1AccessToken accessToken=service.getAccessToken(requestToken,authVerifier);

         System.out.println(accessToken.getToken());
         System.out.println(accessToken.getTokenSecret());

         Auth auth=flickr.getAuthInterface().checkToken(accessToken);

         System.out.println("------------------------");
         System.out.println(accessToken.getToken());
         System.out.println(accessToken.getTokenSecret());

         return flickr;
     }*/
    @Bean
    public Flickr getFlickr2() {
        Flickr flickr = new Flickr(apiKey, apiSecret, new REST());
        Auth auth = new Auth();
        auth.setPermission(Permission.READ);
        auth.setToken(appKey);
        auth.setTokenSecret(appSecret);
        RequestContext requestContext = RequestContext.getRequestContext();
        requestContext.setAuth(auth);
        flickr.setAuth(auth);
        return flickr;
    }

}
