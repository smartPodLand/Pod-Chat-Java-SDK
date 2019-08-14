import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestEditMessage;

import java.util.List;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class EditMessage implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "7042b1765927491ab96053d29132ece7";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";

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
            chatController.connect("", "", serverName, token, ssoHost, platformHost, fileServer, "default");
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

        UserInfo userInfo = new UserInfo();
        userInfo.setId(4101);
        userInfo.setName("فاطمه خجسته");
        userInfo.setCellphoneNumber("09151242904");
        userInfo.setSendEnable(true);
        userInfo.setReceiveEnable(true);


        Mockito.verify(chatContract).onState("OPEN");
        Mockito.verify(chatContract).onState("ASYNC_READY");

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertEquals(gson.toJson(userInfo), gson.toJson(resultUserInfo1.getUser()));

    }

    @Test
    @Order(2)
    void editMessage() throws InterruptedException {
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hi", 47542)
                .build();
        chatController.editMessage(requestEditMessage, null);

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);
        Mockito.verify(chatContract, Mockito.times(1)).onSentMessage(argument.capture());

        ArgumentCaptor<ChatResponse> argument2 = ArgumentCaptor.forClass(ChatResponse.class);
        Mockito.verify(chatContract, Mockito.times(1)).onEditMessage(argument2.capture());

        List<ChatResponse> sentMessage = argument.getAllValues();
        List<ChatResponse> editMessage = argument2.getAllValues();

        Assertions.assertTrue(!sentMessage.isEmpty() || !editMessage.isEmpty());


    }

    // The message id does not exist
    @Test
    @Order(2)
    void editMessageWithError() throws InterruptedException {
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hi", 475422)
                .build();
        chatController.editMessage(requestEditMessage, null);


        Thread.sleep(3000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract, Mockito.times(1)).onError(argument.capture());


        ErrorOutPut errorOutPut = argument.getValue();

        Assertions.assertTrue(errorOutPut.isHasError());

    }


}
