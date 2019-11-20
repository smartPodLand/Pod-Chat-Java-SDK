import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestUploadFile;
import podChat.requestobject.RequestUploadImage;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class UploadFileAndImage implements ChatContract.view {
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
                    .Builder(Constant.queueServer,
                    Constant.queuePort,
                    Constant.queueInput,
                    Constant.queueOutput,
                    Constant.queueUserName,
                    Constant.queuePassword,
                    Constant.serverName,
                    Constant.token,
                    Constant.ssoHost,
                    Constant.platformHost,
                    Constant.fileServer,
                    Constant.chatId)
                    .typeCode("default")
                    .build();

            chatController.connect(requestConnect);

            Thread.sleep(2000);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(2)
    void uploadFile() throws InterruptedException {

        RequestUploadFile requestUploadFile = new RequestUploadFile
                .Builder("F:\\models.txt")
                .build();

        chatController.uploadFile(requestUploadFile);

        Thread.sleep(10000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onUploadFile(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }


    @Test
    @Order(2)
    void uploadImageWithCrop() throws InterruptedException {

        RequestUploadImage requestUploadImage = new RequestUploadImage
                .Builder("C:\\Users\\fanap-10\\Pictures\\Saved Pictures\\a.jpg")
                .xC(100)
                .yC(100)
                .hC(200)
                .wC(200)
                .build();

        chatController.uploadImage(requestUploadImage);


        Thread.sleep(10000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onUploadImageFile(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

    @Test
    @Order(2)
    void uploadImageWithoutCrop() throws InterruptedException {

        RequestUploadImage requestUploadImage = new RequestUploadImage
                .Builder("C:\\Users\\fanap-10\\Pictures\\Saved Pictures\\a.jpg")
                .build();

        chatController.uploadImage(requestUploadImage);


        Thread.sleep(10000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onUploadImageFile(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

}
