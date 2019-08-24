import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.UserInfo;
import podChat.model.*;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestMessage;
import podChat.requestobject.RequestRemoveContact;
import podChat.util.Util;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class RemoveConatct implements ChatContract.view {
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

        Mockito.verify(chatContract, Mockito.times(1)).onGetUserInfo(argument.capture());
        ResultUserInfo resultUserInfo1 = (ResultUserInfo) argument.getValue().getResult();

        Assertions.assertEquals(gson.toJson(userInfo), gson.toJson(resultUserInfo1.getUser()));

    }

    @Test
    @Order(2)
    void removeContact() throws InterruptedException {
        RequestRemoveContact requestRemoveContact = new RequestRemoveContact
                .Builder(13812)
                .build();

        chatController.removeContact(requestRemoveContact);

        Thread.sleep(3000);


        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract,Mockito.times(1)).onRemoveContact(argument.capture());

        ResultRemoveContact resultRemoveContact = (ResultRemoveContact) argument.getValue().getResult();

        Assertions.assertTrue(resultRemoveContact.isResult());
    }

}
