package podChat.networking.retrofithelper;

import retrofit2.Response;

/**
 * Created By Khojasteh on 7/31/2019
 */
public interface ApiListener<T> {
    void onSuccess(T t);

    void onError(Throwable throwable);

    void onServerError(Response<T> response);
}
