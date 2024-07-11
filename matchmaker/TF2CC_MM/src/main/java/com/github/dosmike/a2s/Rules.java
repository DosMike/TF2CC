package com.github.dosmike.a2s;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Rules extends HashMap<String,String> implements A2SMessage.A2SDeserializable {

    Rules(ByteBuffer payload, long expectedGame) {
        int count = payload.getShort();
        for (int i=0; i<count; i++) {
            this.put(A2SMessage.getString(payload), A2SMessage.getString(payload));
        }
    }

}
