package kuke.board.common.outboxmessagerelay;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class OutBoxEvent {

    private Outbox outbox;

    public static OutBoxEvent of(Outbox outbox) {
        OutBoxEvent outBoxEvent = new OutBoxEvent();
        outBoxEvent.outbox = outbox;
        return outBoxEvent;
    }
}
