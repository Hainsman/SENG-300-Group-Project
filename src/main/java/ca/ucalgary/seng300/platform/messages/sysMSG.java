package ca.ucalgary.seng300.platform.messages;

import java.io.Serializable;

public class sysMSG implements Serializable {
    private static final long serialVersionUID = 1L;
    private String type;    // system message type (matchmaking, chat, game, lobby, stats)
    private String fieldA;  // data field A
    private String fieldB;  // data field B
    private String payload; // extra info, object .tostring of another object if needed

    public sysMSG(String tp, String data1) {
        type = tp;
        fieldA = data1;
        fieldB = null;
        payload = null;
    }

    public sysMSG(String tp, String data1, String data2) {
        type = tp;
        fieldA = data1;
        fieldB = data2;
        payload = null;
    }

    public sysMSG(String tp, String data1, String data2, String s) {
        type = tp;
        fieldA = data1;
        fieldB = data2;
        payload = s;
    }

    public String getType() { return type; }
    public String getFieldA() { return fieldA; }
    public String getFieldB() { return fieldB; }
    public Object getPayload() { return payload; }

}
