package ca.ucalgary.seng300.platform.domain;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Message {
    public String author;
    public String content;
    long timestamp;

    public Message(String content) {
        this.author = "Guest";
        this.content = content;
        // NOTE(Dylan): Yes, we're just using MST during daylight savings time,
        // we can add TimeZone later if it's really important...
        this.timestamp = LocalDateTime.now().
                toEpochSecond(ZoneOffset.of("-06:00"));
    }
}
