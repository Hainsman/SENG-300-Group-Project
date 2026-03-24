package ca.ucalgary.seng300.platform.messages;

public class sysMSG {
    private String type;    // system message type (matchmaking, chat, game, lobby, stats)
    private String fieldA;  // data field A
    private String fieldB;  // data field B
    private Object payload; // extra info

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

    public sysMSG(String tp, String data1, String data2, Object o) {
        type = tp;
        fieldA = data1;
        fieldB = data2;
        payload = o;
    }

    public String getType() { return type; }
    public String getFieldA() { return fieldA; }
    public String getFieldB() { return fieldB; }
    public Object getPayload() { return payload; }

}
