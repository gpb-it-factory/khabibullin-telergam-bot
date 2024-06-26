package dus.gpb.khabibullinfrontendbot.service.command.impl;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import dus.gpb.khabibullinfrontendbot.service.command.CommandsEnum;
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
        long testUserId = 123123L;

        stubFor(post(urlEqualTo(ACCOUNTS_API_PREFIX + "/" + testUserId + "/accounts"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())));

        String testUserFirstName = "test_first_name";
        String testUserName = "test_user_name";

        long testChatId = 123L;
        String testChatType = "private";

        User testUser = new User(testUserId, testUserFirstName, false);
        testUser.setUserName(testUserName);

        Message testMessage = new Message();
        testMessage.setFrom(testUser);
        testMessage.setChat(new Chat(testChatId, testChatType));
        testMessage.setText(CommandsEnum.CREATE_ACCOUNT.getValue());

        String onSuccessText = String.format(
                CreateAccountCommand.CREATE_ACCOUNT_COMMAND_ON_SUCCESS_ANSWER,
                testUser.getUserName(),
                testUserName + "'s Account"
        );

        SendMessage testSendMessageForStub = SendMessage
                .builder()
                .chatId(testChatId)
                .text(onSuccessText)
                .build();

        when(textMessageSender.getSendMessage(testChatId, onSuccessText)).thenReturn(testSendMessageForStub);
        doNothing().when(textMessageSender).executeSendMessage(testSendMessageForStub);

        createAccountCommand.handleCommand(testMessage);

        verify(textMessageSender, times(1)).getSendMessage(testChatId, onSuccessText);
        verify(textMessageSender, times(1)).executeSendMessage(testSendMessageForStub);
    }

    @DisplayName("Should send conflict when conflict response from middle service")
    @Test
    void When_handleCommand_with_conflict_registration_than_send_conflict_message() {

        long testUserId = 123123L;

        stubFor(
                post(urlEqualTo(ACCOUNTS_API_PREFIX + "/" + testUserId + "/accounts"))
                        .willReturn(aResponse().withStatus(HttpStatus.CONFLICT.value()))
        );

        String testUserFirstName = "test_first_name";
        String testUserName = "test_user_name";

        long testChatId = 123L;
        String testChatType = "private";

        User testUser = new User(testUserId, testUserFirstName, false);
        testUser.setUserName(testUserName);

        Message testMessage = new Message();
        testMessage.setFrom(testUser);
        testMessage.setChat(new Chat(testChatId, testChatType));
        testMessage.setText(CommandsEnum.CREATE_ACCOUNT.getValue());

        String onConflictText = String.format(
                CreateAccountCommand.CREATE_ACCOUNT_COMMAND_ON_CONFLICT_ANSWER,
                testUser.getUserName()
        );

        SendMessage testSendMessageForStub = SendMessage
                .builder()
                .chatId(testChatId)
                .text(onConflictText)
                .build();

        when(textMessageSender.getSendMessage(testChatId, onConflictText)).thenReturn(testSendMessageForStub);
        doNothing().when(textMessageSender).executeSendMessage(testSendMessageForStub);

        createAccountCommand.handleCommand(testMessage);

        verify(textMessageSender, times(1)).getSendMessage(testChatId, onConflictText);
        verify(textMessageSender, times(1)).executeSendMessage(testSendMessageForStub);
    }

    @DisplayName("Should send error when error response from middle service")
    @Test
    void When_handleCommand_with_error_creating_account_than_send_error_message() {

        long testUserId = 123123L;

        stubFor(
                post(urlEqualTo(ACCOUNTS_API_PREFIX + "/" + testUserId + "/accounts"))
                        .willReturn(aResponse().withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value()))
        );

        String testUserFirstName = "test_first_name";
        String testUserName = "test_user_name";

        long testChatId = 123L;
        String testChatType = "private";

        User testUser = new User(testUserId, testUserFirstName, false);
        testUser.setUserName(testUserName);

        Message testMessage = new Message();
        testMessage.setFrom(testUser);
        testMessage.setChat(new Chat(testChatId, testChatType));
        testMessage.setText(CommandsEnum.CREATE_ACCOUNT.getValue());

        String onErrorText = CreateAccountCommand.ERROR_ANSWER;

        SendMessage testSendMessageForStub = SendMessage
                .builder()
                .chatId(testChatId)
                .text(onErrorText)
                .build();

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

        long testUserId = 123123L;

        String testUserFirstName = "test_first_name";
        String testUserName = "test_user_name";

        long testChatId = 123L;
        String testChatType = "private";

        User testUser = new User(testUserId, testUserFirstName, false);
        testUser.setUserName(testUserName);

        Message testMessage = new Message();
        testMessage.setFrom(testUser);
        testMessage.setChat(new Chat(testChatId, testChatType));
        testMessage.setText(wrongFormatCommand);

        String onWrongCommandFormatText = String.format(
                CreateAccountCommand.WRONG_COMMAND_ANSWER,
                wrongFormatCommand.split(" ")[0],
                CommandsEnum.CREATE_ACCOUNT.getValue());

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

}