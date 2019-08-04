package podChat.networking.retrofithelper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import config.MainConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Date;

/**
 * Created By Khojasteh on 2/25/2019
 */
public class RetrofitUtil {


    private static Retrofit retrofit = null;

    public static synchronized Retrofit getInstance(String baseUrl) {

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Date.class, new MyDateTypeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        return retrofit;
    }


}
