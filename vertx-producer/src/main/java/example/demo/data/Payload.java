package example.demo.data;

import lombok.Data;

@Data
public class Payload {

    private long timestamp;
    private String message;

    public Payload(){
        this.timestamp = System.currentTimeMillis();
    }

    public Payload(String message) {
        this();
        this.message = message;
    }

}
