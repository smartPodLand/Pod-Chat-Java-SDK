import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.chat.ChatHandler;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestThreadParticipant;
import podChat.requestobject.RequestUploadImage;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class GetParticipant implements ChatContract.view {
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

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertEquals(gson.toJson(userInfo), gson.toJson(resultUserInfo1.getUser()));

    }

    @Test
    @Order(2)
    void getGroupParticipant() throws InterruptedException {

        RequestThreadParticipant requestThreadParticipant = new RequestThreadParticipant
                .Builder(5781)
                .count(5)
                .offset(0)
                .build();

        chatController.getThreadParticipant(requestThreadParticipant, null);

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetThreadParticipant(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

    @Test
    @Order(2)
    void getParticipant() throws InterruptedException {

        RequestThreadParticipant requestThreadParticipant = new RequestThreadParticipant
                .Builder(5461)
                .build();

        chatController.getThreadParticipant(requestThreadParticipant, null);

        Thread.sleep(5000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetThreadParticipant(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }


}
