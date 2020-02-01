package podChat.model;

import com.google.gson.Gson;

public class ChatResponse<T> extends BaseOutPut {
    private Gson gson = new Gson();
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getJson(ChatResponse<T> result) {
        return gson.toJson(result);
    }
}
