import Constant.Constant;
import com.google.gson.Gson;
import exception.ConnectionException;
import exmaple.ChatContract;
import org.junit.jupiter.api.*;
import org.mockito.*;
import podChat.model.ChatResponse;
import podChat.requestobject.RequestConnect;
import podChat.requestobject.RequestGetAdmin;
import podChat.requestobject.RequestRole;
import podChat.requestobject.RequestSetAdmin;
import podChat.util.RoleType;

import java.util.ArrayList;

/**
 * Created By Khojasteh on 8/6/2019
 */

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class AdminScenario implements ChatContract.view {
    @Mock
    static ChatContract.view chatContract;
    @InjectMocks
    static ChatController chatController = Mockito.mock(ChatController.class);
    long userId = 4781;
    long threadId = 5941;
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
    @Order(3)
    void addRole() throws InterruptedException {

        RequestRole requestRole = new RequestRole();
        requestRole.setId(userId);
        requestRole.setRoleTypes(new ArrayList<String>() {{
            add(RoleType.THREAD_ADMIN);
        }});

        ArrayList<RequestRole> requestRoleArrayList = new ArrayList<>();
        requestRoleArrayList.add(requestRole);

        RequestSetAdmin requestSetAdmin = new RequestSetAdmin
                .Builder(threadId, requestRoleArrayList)
                .build();

        chatController.addAdmin(requestSetAdmin);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onSetRole(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }

    @Test
    @Order(3)
    void getThreadAdmin2() throws InterruptedException {

        RequestGetAdmin requestGetAdmin = new RequestGetAdmin
                .Builder(threadId)
                .build();

        chatController.getAdminList(requestGetAdmin);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetThreadParticipant(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

    @Test
    @Order(4)
    void deleteRole() throws InterruptedException {

        RequestRole requestRole = new RequestRole();
        requestRole.setId(userId);
        requestRole.setRoleTypes(new ArrayList<String>() {{
            add(RoleType.THREAD_ADMIN);
        }});

        ArrayList<RequestRole> requestRoleArrayList = new ArrayList<>();
        requestRoleArrayList.add(requestRole);

        RequestSetAdmin requestSetAdmin = new RequestSetAdmin
                .Builder(threadId, requestRoleArrayList)
                .build();

        chatController.removeAdmin(requestSetAdmin);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onRemoveRole(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());

    }

    @Test
    @Order(5)
    void getThreadAdmin3() throws InterruptedException {

        RequestGetAdmin requestGetAdmin = new RequestGetAdmin
                .Builder(threadId)
                .build();

        chatController.getAdminList(requestGetAdmin);

        Thread.sleep(2000);

        ArgumentCaptor<ChatResponse> argument = ArgumentCaptor.forClass(ChatResponse.class);

        Mockito.verify(chatContract).onGetThreadParticipant(argument.capture());

        ChatResponse chatResponse = argument.getValue();

        Assertions.assertTrue(!chatResponse.hasError());
    }

}
