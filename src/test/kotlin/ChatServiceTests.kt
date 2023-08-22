import junit.framework.TestCase.*
import org.junit.Test

class ChatServiceTests {

    @Test
    fun createChat() {
        val chatService = ChatService()
        chatService.createChat(1)
        val chats = chatService.getChats(1).toList()
        assertEquals(1, chats.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun createChat_NegativeUserId() {
        val chatService = ChatService()
        chatService.createChat(-1)
    }

    @Test
    fun deleteChat() {
        val chatService = ChatService()
        val userId = 1
        chatService.createChat(userId)
        val chatId = chatService.chats.lastOrNull()?.id ?: 0

        chatService.deleteChat(chatId)

        assertNull(chatService.chats.find { it.id == chatId })
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteChat_NegativeChatId() {
        val chatService = ChatService()
        chatService.deleteChat(-1)
    }

    @Test
    fun getChats() {
        val chatService = ChatService()
        chatService.createChat(1)
        chatService.createChat(2)
        chatService.createChat(3)

        val result = chatService.getChats(1).toList()

        assertEquals(1, result.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getChat_NegativeUserId() {
        val chatService = ChatService()
        chatService.getChats(1).toList()
    }

    @Test
    fun getUnreadChatsCount() {
        val chatService = ChatService()
        val chat1 = Chat(1, listOf(1, 2, 3))
        val chat2 = Chat(2, listOf(1, 2, 3))

        chat1.messages.add(Message(1, 2, 1, "Здрасти", true))
        chat1.messages.add(Message(2, 1, 1, "Добрый день", true))
        chat2.messages.add(Message(1, 3, 1, "Привет", true))
        chatService.chats.add(chat1)
        chatService.chats.add(chat2)

        val result = chatService.getUnreadChatsCount(1)

        assertEquals(0, result)
    }

    @Test
    fun getLastMessages() {
        val chatService = ChatService()
        chatService.createChat(1)
        chatService.createChat(2)
        val expectedMessages = listOf("нет сообщений")

        val actualMessages = chatService.getLastMessages(2)

        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun getChatMessages() {
        val chatService = ChatService()
        chatService.createChat(1)

        val chat1 = chatService.chats[0]

        chat1.messages.add(Message(1, 1, 2, "Привет", false))
        chat1.messages.add(Message(2, 1, 2, "Как дела", true))
        chat1.messages.add(Message(3, 2, 1, "Привет", false))

        val messages = chatService.getChatMessages(1, chat1.id, 1, 2)

        assertEquals(2, messages.size)
        assertEquals("Привет", messages[0].text)
        assertEquals("Как дела", messages[1].text)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getChatMessages_NegativeUserId() {
        val chatService = ChatService()
        chatService.getChatMessages(-1, 0, 10, 2)
    }

    @Test
    fun createMessage() {
        val chatService = ChatService()
        val chatId = 1
        val fromId = 1
        val peerId = 2
        val text = "Hello"

        chatService.createChat(fromId)

        chatService.createMessage(chatId, fromId, peerId, text)

        val chat = chatService.chats.find { it.id == chatId }
        assertNotNull(chat)

        val message = chat?.messages?.find { it.fromId == fromId && it.peerId == peerId && it.text == text }
        assertNotNull(message)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCreateMessage_invalidParameters() {
        val chatId = 1
        val fromId = -1
        val peerId = 2
        val text = "Муму"
        val chatService = ChatService()

        chatService.createChat(fromId)
        chatService.createMessage(chatId, fromId, peerId, text)
    }

    @Test
    fun deleteMessage() {
        val chatService = ChatService()
        val chatId = 1
        val messageId = 1
        chatService.createChat(1)
        chatService.createMessage(chatId, 1, 2, "день")
        chatService.deleteMessage(chatId, messageId)
        val chat = chatService.chats.find { it.id == chatId }
        assertTrue(chat?.messages?.none { it.id == messageId } ?: false)
    }

    @Test(expected = ChatNotFoundException::class)
    fun deleteMessageNegative() {
        val chatService = ChatService()
        val chatId = 1
        val messageId = 1
        chatService.deleteMessage(chatId, messageId)
    }

    @Test
    fun markMessagesAsRead() {
        val chatService = ChatService()
        val userId = 1
        val chatId = 1
        val message1 = Message(1, 2, 1, "Бух", false)
        val message2 = Message(2, 3, 1, "Бам", false)
        val message3 = Message(3, 1, 2, "Бах", false)
        val chat = Chat(chatId, listOf(1, 2), mutableListOf(message1, message2, message3))
        chatService.chats.add(chat)

        chatService.markMessagesAsRead(userId, chatId)

        assertTrue(chat.messages[0].readState)
    }

    @Test(expected = ChatNotFoundException::class)
    fun markMessagesAsReadNegative() {
        val chatService = ChatService()
        val userId = 1
        val chatId = -1

        chatService.markMessagesAsRead(userId, chatId)
    }

}
