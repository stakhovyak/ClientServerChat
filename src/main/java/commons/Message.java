package commons;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Message data type.
 * @param localDateTime When message was sent
 * @param clientsUsername Client's username
 * @param messageContents The contents of message
 */
public record Message(LocalDateTime localDateTime,
                      String clientsUsername,
                      String messageContents) implements Serializable {

    public static Message createMessage(String clientsUsername, String messageContents) {
        return new Message(LocalDateTime.now(), clientsUsername, messageContents);
    }

    /**
     * Message representation in chat.
     * @return How users will see the message.
     */
    @Override
    public String toString() {
        return "[" +
                localDateTime.getMonth() + " "
                + localDateTime.getDayOfMonth()
                + ", " + localDateTime.getHour()
                + ":" + localDateTime.getMinute() + "] " +
                clientsUsername + ": " +
                messageContents;
    }

}