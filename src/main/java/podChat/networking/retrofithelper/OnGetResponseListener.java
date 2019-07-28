package podChat.networking.retrofithelper;


/**
 * Created By Khojasteh on 2/26/2019
 */
public interface OnGetResponseListener<T> {
    void onSuccess(T t);

    void onError(Throwable throwable);

    void onServerError(String errorMessage);

}
