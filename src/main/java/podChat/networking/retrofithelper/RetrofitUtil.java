package podChat.networking.retrofithelper;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created By Khojasteh on 2/25/2019
 */
public class RetrofitUtil {


    public static <T> void request(Call<T> addContactService, ApiListener<T> listener) {
        addContactService.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.body() != null && response.isSuccessful())
                    listener.onSuccess(response.body());

                else listener.onServerError(response);
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                listener.onError(throwable);
            }
        });
    }

}
