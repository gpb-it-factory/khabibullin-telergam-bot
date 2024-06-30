package dus.gpb.khabibullinfrontendbot.service.command.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static dus.gpb.khabibullinfrontendbot.client.user.AccountClient.ACCOUNTS_API_PREFIX;
import static dus.gpb.khabibullinfrontendbot.service.command.impl.CreateAccountCommand.CREATE_ACCOUNT_COMMAND_VALUE;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class CreateAccountCommandTest {

    @MockBean
    private MessageSender textMessageSender;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    private static WireMockServer wireMockServer;

    @BeforeAll
    public static void setUp() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8082));
        configureFor("localhost", 8082);
        wireMockServer.start();
    }

    @AfterAll
    public static void tearDown() {
        wireMockServer.stop();
    }

    @DisplayName("Should send success message when successful response from middle service")
    @Test
    void When_handleCommand_with_successful_registration_than_send_success_message() {
        long testChatId = 123L;

        User testUser = getTestUser(123123L, "test_first_name", "test_user_name");
        Message testMessage = getTestMessage(testUser, testChatId, CREATE_ACCOUNT_COMMAND_VALUE);
        String onSuccessText = String.format(
                CreateAccountCommand.CREATE_ACCOUNT_COMMAND_ON_SUCCESS_ANSWER,
                testUser.getUserName(),
                testUser.getUserName() + "'s Account"
        );
        SendMessage testSendMessageForStub = getTestSendMessage(testChatId, onSuccessText);

        stubFor(post(urlEqualTo(ACCOUNTS_API_PREFIX + "/" + testUser.getId() + "/accounts"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())));
        when(textMessageSender.getSendMessage(testChatId, onSuccessText)).thenReturn(testSendMessageForStub);
        doNothing().when(textMessageSender).executeSendMessage(testSendMessageForStub);

        createAccountCommand.handleCommand(testMessage);

        verify(textMessageSender, times(1)).getSendMessage(testChatId, onSuccessText);
        verify(textMessageSender, times(1)).executeSendMessage(testSendMessageForStub);
    }

    @DisplayName("Should send conflict when conflict response from middle service")
    @Test
    void When_handleCommand_with_conflict_registration_than_send_conflict_message() {

        long testChatId = 123L;

        User testUser = getTestUser(123123L, "test_first_name", "test_user_name");
        Message testMessage = getTestMessage(testUser, testChatId, CREATE_ACCOUNT_COMMAND_VALUE);
        String onConflictText = String.format(
                CreateAccountCommand.CREATE_ACCOUNT_COMMAND_ON_CONFLICT_ANSWER,
                testUser.getUserName()
        );
        SendMessage testSendMessageForStub = getTestSendMessage(testChatId, onConflictText);

        stubFor(post(urlEqualTo(ACCOUNTS_API_PREFIX + "/" + testUser.getId() + "/accounts"))
                        .willReturn(aResponse().withStatus(HttpStatus.CONFLICT.value())));
        when(textMessageSender.getSendMessage(testChatId, onConflictText)).thenReturn(testSendMessageForStub);
        doNothing().when(textMessageSender).executeSendMessage(testSendMessageForStub);

        createAccountCommand.handleCommand(testMessage);

        verify(textMessageSender, times(1)).getSendMessage(testChatId, onConflictText);
        verify(textMessageSender, times(1)).executeSendMessage(testSendMessageForStub);
    }

    @DisplayName("Should send error when error response from middle service")
    @Test
    void When_handleCommand_with_error_creating_account_than_send_error_message() {
        long testChatId = 123L;

        User testUser = getTestUser(123123L, "test_first_name", "test_user_name");
        Message testMessage = getTestMessage(testUser, testChatId, CREATE_ACCOUNT_COMMAND_VALUE);
        String onErrorText = CreateAccountCommand.ERROR_ANSWER;
        SendMessage testSendMessageForStub = getTestSendMessage(testChatId, onErrorText);

        stubFor(post(urlEqualTo(ACCOUNTS_API_PREFIX + "/" + testUser.getId() + "/accounts"))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        when(textMessageSender.getSendMessage(testChatId, onErrorText)).thenReturn(testSendMessageForStub);
        doNothing().when(textMessageSender).executeSendMessage(testSendMessageForStub);

        createAccountCommand.handleCommand(testMessage);

        verify(textMessageSender, times(1)).getSendMessage(testChatId, onErrorText);
        verify(textMessageSender, times(1)).executeSendMessage(testSendMessageForStub);
    }

    @DisplayName("Should send wrong format command answer when handle command with wrong format")
    @ParameterizedTest
    @ValueSource(strings = {"/createaccount test", "/createaccount1", "/createaccount_test"})
    void When_handleCommand_with_error_registration_than_send_error_message(String wrongFormatCommand) {

        long testChatId = 123L;

        User testUser = getTestUser(333L, "test_first_name3", "test_user_name3");
        Message testMessage = getTestMessage(testUser, testChatId, wrongFormatCommand);
        String onWrongCommandFormatText = String.format(
                CreateAccountCommand.WRONG_COMMAND_ANSWER,
                wrongFormatCommand.split(" ")[0],
                CREATE_ACCOUNT_COMMAND_VALUE);
        SendMessage testSendMessageForStub = SendMessage
                .builder()
                .chatId(testChatId)
                .text(onWrongCommandFormatText)
                .build();

        when(textMessageSender.getSendMessage(testChatId, onWrongCommandFormatText)).thenReturn(testSendMessageForStub);
        doNothing().when(textMessageSender).executeSendMessage(testSendMessageForStub);

        createAccountCommand.handleCommand(testMessage);

        verify(textMessageSender, times(1)).getSendMessage(testChatId, onWrongCommandFormatText);
        verify(textMessageSender, times(1)).executeSendMessage(testSendMessageForStub);
    }

    private User getTestUser(long testUserId, String testUserFirstName, String testUserName) {
        User testUser = new User(testUserId, testUserFirstName, false);
        testUser.setUserName(testUserName);
        return testUser;
    }

    private SendMessage getTestSendMessage(long testChatId, String text) {
        return SendMessage
                .builder()
                .chatId(testChatId)
                .text(text)
                .build();
    }

    private Message getTestMessage(User testUser, long testChatId, String text) {
        String testChatType = "private";
        Message testMessage = new Message();
        testMessage.setFrom(testUser);
        testMessage.setChat(new Chat(testChatId, testChatType));
        testMessage.setText(text);
        return testMessage;
    }

}