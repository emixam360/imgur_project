package fr.esstin.benjamin.imgurproject.services;

import com.squareup.okhttp.OkHttpClient;

import fr.esstin.benjamin.imgurproject.imgurModel.ImgurAPI;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Benjamin on 04/01/2016.
 */
public class ServiceGenerator {

    private static OkHttpClient httpClient = new OkHttpClient();
    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(ImgurAPI.server)
                    .addConverterFactory(GsonConverterFactory.create());

    public static <S> S createService(Class<S> serviceClass) {
        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }
}
