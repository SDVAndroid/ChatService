data class Message(
    val id: Int,
    val fromId: Int,
    val peerId: Int,
    val text: String,
    var readState: Boolean
)

data class Chat(
    val id: Int,
    val users: List<Int>,
    val messages: MutableList<Message> = mutableListOf()
)

class ChatNotFoundException(message: String) : Exception(message)

class ChatService {
    val chats: MutableList<Chat> = mutableListOf()
    var chatIdCounter = 0

    fun createChat(userId: Int) {
        if (userId < 0) {
            throw IllegalArgumentException("Недопустимый Id")
        }
        chats.add(Chat(chatIdCounter++, listOf(userId)))
    }

    fun deleteChat(chatId: Int) {
        val chat = chats.find { it.id == chatId }
        if (chat == null) {
            throw ChatNotFoundException("Чат не найден")

        } else {
            chats.remove(chat)
        }
    }

    fun getChats(userId: Int): List<Chat> {
        val result = chats.filter { it.users.contains(userId) }
        if (result.isEmpty()) {
            throw IllegalArgumentException("Не найдено чатов для пользователя с идентификатором $userId")
        } else
            return result
    }

    fun getUnreadChatsCount(userId: Int): Int =
        chats.filter { it.users.contains(userId) }.count { chat ->
            chat.messages.any { message -> message.peerId == userId && !message.readState }
        }

    fun getLastMessages(userId: Int): List<String> =
        getChats(userId).map { it.messages.lastOrNull() }.map { it?.text ?: "нет сообщений" }


    fun getChatMessages(userId: Int, chatId: Int, lastMessageId: Int, count: Int): List<Message> =
        chats.find { it.id == chatId }
            ?.messages?.filter { it.id >= lastMessageId }
            ?.take(count)
            ?.onEach { if (it.peerId == userId) it.readState = true }
            ?: throw IllegalArgumentException("Чат не найден")


    fun createMessage(chatId: Int, fromId: Int, peerId: Int, text: String) {
        val chat = chats.find { it.id == chatId }

        if (chat == null) {
            val newChat = Chat(chatId, listOf(fromId, peerId))
            newChat.messages.add(Message(1, fromId, peerId, text, false))
            chats.add(newChat)
        } else {
            val newMessageId = chat.messages.maxByOrNull { it.id }?.id ?: 0
            chat.messages.add(Message(newMessageId + 1, fromId, peerId, text, false))
        }
    }


    fun deleteMessage(chatId: Int, messageId: Int) {
        val chat = chats.find { it.id == chatId }
        if (chat == null) {
            throw ChatNotFoundException("Чат не найден")
        }
        chat.messages.removeIf { message -> message.id == messageId }
    }

    fun markMessagesAsRead(userId: Int, chatId: Int) {
        val chat = chats.find { it.id == chatId }
        if (chat == null) {
            throw ChatNotFoundException("Чат не найден")
        } else {
            chat.messages.forEach {
                if (it.peerId == userId) {
                    it.readState = true
                }
            }
        }
    }
}

fun main(args: Array<String>) {

}