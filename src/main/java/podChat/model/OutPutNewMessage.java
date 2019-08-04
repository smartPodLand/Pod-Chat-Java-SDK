package podChat.model;

import podChat.mainmodel.MessageVO;

public class OutPutNewMessage extends BaseOutPut {
    private ResultNewMessage result;

    public ResultNewMessage getResult() {
        return result;
    }

    public void setResult(ResultNewMessage result) {
        this.result = result;
    }
}
