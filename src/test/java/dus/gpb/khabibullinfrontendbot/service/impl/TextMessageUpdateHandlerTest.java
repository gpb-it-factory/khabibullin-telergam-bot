package dus.gpb.khabibullinfrontendbot.service.impl;

import dus.gpb.khabibullinfrontendbot.service.MessageSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.message.Message;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@SpringBootTest
class TextMessageUpdateHandlerTest {

    private static final String DEFAULT_ANSWER = "Команда '%s' отсутствует";

    @MockBean
    private MessageSender textMessageSender;

    @Autowired
    TextMessageUpdateHandler textMessageUpdateHandler;

    @DisplayName("Should send default answer when not implemented command")
    @ParameterizedTest
    @ValueSource(strings = {"/wrongCommand", "/wc", "/___111", "/12312", "/####", "/", "not a command"})
    void when_handle_wrong_command(String wrongCommand) {

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
        testMessage.setText(wrongCommand);

        Update testUpdate = new Update();
        testUpdate.setMessage(testMessage);

        String onNotImplementedCommandText = String.format(DEFAULT_ANSWER, wrongCommand);

        SendMessage testSendMessageForStub = SendMessage
                .builder()
                .chatId(testChatId)
                .text(onNotImplementedCommandText)
                .build();

        when(textMessageSender.getSendMessage(testChatId, onNotImplementedCommandText)).thenReturn(testSendMessageForStub);
        doNothing().when(textMessageSender).executeSendMessage(testSendMessageForStub);

        textMessageUpdateHandler.handleUpdate(testUpdate);

        verify(textMessageSender, times(1)).getSendMessage(testChatId, onNotImplementedCommandText);
        verify(textMessageSender, times(1)).executeSendMessage(testSendMessageForStub);
    }
}