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
    var messages: MutableList<Message> = mutableListOf()
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

    fun getChats(userId: Int): Sequence<Chat> {
        return chats.asSequence()
            .filter { it.users.contains(userId) }
            .onEach { it.messages }
            .ifEmpty { throw IllegalArgumentException("Не найдено чатов для пользователя с идентификатором $userId") }
    }

    fun getUnreadChatsCount(userId: Int): Int =
        chats.asSequence()
            .filter { it.users.contains(userId) }
            .count { chat ->
                chat.messages.asSequence()
                    .any { message -> message.peerId == userId && !message.readState }
            }

    fun getLastMessages(userId: Int): List<String> =
        getChats(userId).asSequence()
            .map { it.messages.asSequence().lastOrNull() }
            .map { it?.text ?: "нет сообщений" }
            .toList()


    fun getChatMessages(userId: Int, chatId: Int, lastMessageId: Int, count: Int): List<Message> =
        chats.asSequence()
            .find { it.id == chatId }
            ?.messages?.asSequence()
            ?.filter { it.id >= lastMessageId }
            ?.take(count)
            ?.onEach { if (it.peerId == userId) it.readState = true }
            ?.toList()
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
        val chat = chats.asSequence().find { it.id == chatId }
        if (chat == null) {
            throw ChatNotFoundException("Чат не найден")
        }
        chat.messages = chat.messages.asSequence().filter { message -> message.id != messageId }.toMutableList()
    }

    fun markMessagesAsRead(userId: Int, chatId: Int) {
        val chat = chats.asSequence().find { it.id == chatId }
        if (chat == null) {
            throw ChatNotFoundException("Чат не найден")
        } else {
            chat.messages.asSequence().filter { it.peerId == userId }.forEach { it.readState = true }
        }
    }
}

fun main(args: Array<String>) {

}