import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestEditMessage;

import java.util.ArrayList;
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
                    .Builder(new ArrayList<String>() {{
                add(Constant.uri);
            }},
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
    void editMessage() throws InterruptedException {
        RequestEditMessage requestEditMessage = new RequestEditMessage
                .Builder("hi", 47542)
                .build();
        chatController.editMessage(requestEditMessage);

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
        chatController.editMessage(requestEditMessage);


        Thread.sleep(3000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract, Mockito.times(1)).onError(argument.capture());


        ErrorOutPut errorOutPut = argument.getValue();

        Assertions.assertTrue(errorOutPut.isHasError());

    }


}
