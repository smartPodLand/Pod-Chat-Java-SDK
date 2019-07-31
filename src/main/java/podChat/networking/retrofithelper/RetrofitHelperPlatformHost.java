package podChat.networking.retrofithelper;


import retrofit2.Call;
import retrofit2.Response;

public class RetrofitHelperPlatformHost {

    public static <T> void request(Call<T> addContactService, ApiListener<T> listener) {
        addContactService.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                listener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                listener.onError(throwable);
            }
        });
    }

}
