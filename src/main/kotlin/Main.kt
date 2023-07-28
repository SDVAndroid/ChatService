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

    fun createChat(userId: Int) {
        if (userId < 0) {
            throw IllegalArgumentException("Недопустимый Id")
        }
        chats.add(Chat(chats.size + 1, listOf(userId)))
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

    fun getUnreadChatsCount(userId: Int): Int {
        var unreadChatsCount = 0

        for (chat in chats) {
            if (chat.users.contains(userId)) {
                for (message in chat.messages) {
                    if (message.peerId == userId && !message.readState) {
                        unreadChatsCount++
                        break
                    }
                }
            }
        }

        return unreadChatsCount
    }

    fun getLastMessages(userId: Int): List<String> {
        val userChats = getChats(userId)
        val messages = mutableListOf<String>()
        for (chat in userChats) {
            val lastMessage = chat.messages.lastOrNull()
            if (lastMessage != null) {
                messages.add(lastMessage.text)
            } else {
                messages.add("нет сообщений")
            }
        }
        return messages
    }

    fun getChatMessages(chatId: Int, lastMessageId: Int, count: Int): List<Message> {
        val chat = chats.find { it.id == chatId } ?: throw IllegalArgumentException("Чат не найден")
        val messages = mutableListOf<Message>()
        for (message in chat.messages) {
            if (message.id >= lastMessageId) {
                messages.add(message)
                if (messages.size == count) {
                    break
                }
            }
        }
        return messages
    }

    fun createMessage(chatId: Int, fromId: Int, peerId: Int, text: String) {
        val chat = chats.find { it.id == chatId }
        if (chat == null) {
            throw ChatNotFoundException("Чат не найден.")
        }
        chat.messages.add(Message(chat.messages.size + 1, fromId, peerId, text, false))
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