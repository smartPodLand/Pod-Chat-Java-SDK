package podChat.networking.retrofithelper;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created By Khojasteh on 2/26/2019
 */
public class GetResult<T> {

    private OnGetResponseListener<T> onGetResponseListener;
    private Call<T> call;

    public GetResult(Call<T> call, OnGetResponseListener<T> onGetResponseListener) {
        this.call = call;
        this.onGetResponseListener = onGetResponseListener;
    }

    public void get() {
        if (onGetResponseListener != null) {

            call.enqueue(new Callback<T>() {

                @Override
                public void onResponse(Call<T> call, Response<T> response) {
                    onGetResponseListener.onSuccess(response.body());
                }

                @Override
                public void onFailure(Call<T> call, Throwable throwable) {

                    onGetResponseListener.onServerError(throwable.getMessage());
                }
            });
        }
    }
}
