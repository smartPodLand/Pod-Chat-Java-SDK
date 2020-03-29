package podChat.model;

import com.google.gson.Gson;

public class ChatResponse<T> extends BaseOutPut {
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
