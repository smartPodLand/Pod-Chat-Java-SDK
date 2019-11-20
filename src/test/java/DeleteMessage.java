import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.model.ErrorOutPut;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestDeleteMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class DeleteMessage implements ChatContract.view {
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

    //The message may have already been deleted

    @Test
    @Order(2)
    void deleteMessage() throws InterruptedException {

        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(47581l);
        }})
                .deleteForAll(true)
                .build();

        chatController.deleteMessage(requestDeleteMessage);

        Thread.sleep(3000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract, Mockito.atLeast(0)).onDeleteMessage(argument.capture());

        ArgumentCaptor<ErrorOutPut> argument2 = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract, Mockito.atLeast(0)).onError(argument2.capture());

        List<ChatResponse> chatResponse = argument.getAllValues();
        List<ErrorOutPut> errorOutPut = argument2.getAllValues();

        Assertions.assertTrue(!chatResponse.isEmpty() || !errorOutPut.isEmpty());

    }

    //The messageId does not exist
    @Test
    @Order(2)
    void deleteMessageError() throws InterruptedException {

        RequestDeleteMessage requestDeleteMessage = new RequestDeleteMessage
                .Builder(new ArrayList<Long>() {{
            add(469811L);
        }})
                .deleteForAll(true)
                .build();

        chatController.deleteMessage(requestDeleteMessage);

        Thread.sleep(3000);

        ArgumentCaptor<ErrorOutPut> argument = ArgumentCaptor.forClass(ErrorOutPut.class);

        Mockito.verify(chatContract, Mockito.times(1)).onError(argument.capture());

        Assertions.assertTrue(argument.getValue().isHasError());

    }

}
