import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.Invitee;
import podChat.mainmodel.RequestThreadInnerMessage;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestCreateThread;
import podChat.requestobject.RequestForwardMessage;
import podChat.util.InviteType;
import podChat.util.ThreadType;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ForwardMessage implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;

    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    Gson gson = new Gson();

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    @Order(1)
    public void connect() throws InterruptedException {
        try {
            chatController = new ChatController(chatContract);

            RequestConnect requestConnect = new RequestConnect
                    .Builder(Constant.queueServer, Constant.queuePort, Constant.queueInput, Constant.queueOutput, Constant.queueUserName, Constant.queuePassword, Constant.serverName, Constant.token, Constant.ssoHost, Constant.platformHost, Constant.fileServer)
                    .typeCode("default")
                    .build();

            chatController.connect(requestConnect);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(Constant.userId);
        userInfo.setName(Constant.username);
        userInfo.setCellphoneNumber(Constant.cellphone);
        userInfo.setSendEnable(true);
        userInfo.setReceiveEnable(true);


        Mockito.verify(chatContract).onState("OPEN");
        Mockito.verify(chatContract).onState("ASYNC_READY");

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertEquals(gson.toJson(userInfo), gson.toJson(resultUserInfo1.getUser()));

    }

    @Test
    @Order(2)
    void forwardMessage() throws InterruptedException {
        RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(5461, new ArrayList<Long>() {{
            add(47403l);
            add(47402l);
        }})
                .build();

        chatController.forwardMessage(forwardMessage);

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.times(2)).onSentMessage(argument.capture());
        Mockito.verify(chatContract, Mockito.times(2)).onNewMessage(argument.capture());

    }


    @Test
    @Order(3)
    void ForwardMessageWithNotExistMessageId() throws InterruptedException {
        RequestForwardMessage forwardMessage = new RequestForwardMessage
                .Builder(5461, new ArrayList<Long>() {{
            add(474031l);
        }})
                .build();

        chatController.forwardMessage(forwardMessage);

        Thread.sleep(3000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract, Mockito.times(1)).onError(argument.capture());

        ErrorOutPut errorOutPut =  argument.getValue();

        Assertions.assertTrue(errorOutPut.isHasError());

    }

}
