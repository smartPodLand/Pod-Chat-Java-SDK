import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.mainmodel.UserInfo;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.model.ResultUserInfo;
import podChat.requestobject.RequestAddContact;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AddContact implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);

    static String platformHost = "https://sandbox.pod.land:8043";
    static String token = "fa51a58fa1b042baac2738b74978aa26";
    static String ssoHost = "https://accounts.pod.land";
    static String fileServer = "https://sandbox.pod.land:8443";
    static String serverName = "chat-server";

    Gson gson = new Gson();

    @BeforeAll
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

    // Add contact with phone and last name
    @Test
    @Order(2)
    void addContactPhoneLName() throws InterruptedException {
        RequestAddContact requestAddContact = new RequestAddContact
                .Builder()
                .cellphoneNumber("09156452709")
                .lastName("مظلوم")
                .build();
        chatController.addContact(requestAddContact);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onAddContact(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }

    // Add contact with email and name
    @Test
    @Order(2)
    void addContactEmailFName() throws InterruptedException {
        RequestAddContact requestAddContact = new RequestAddContact
                .Builder()
                .email("m@gmail.com")
                .firstName("mohammad")
                .build();
        chatController.addContact(requestAddContact);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeastOnce()).onAddContact(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }

    //Check the platform server error
    @Test
    @Order(2)
    void addContactError() throws InterruptedException {
        RequestAddContact requestAddContact = new RequestAddContact
                .Builder()
                .cellphoneNumber("09156452709")
                .build();
        chatController.addContact(requestAddContact);

        Thread.sleep(3000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract).onError(argument.capture());

        Assertions.assertTrue(argument.getValue().isHasError());

    }

}
