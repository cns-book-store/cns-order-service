package inc.evil.cnsorderservice.order.domain;

public enum OrderStatus {
    ACCEPTED,
    REJECTED,
    DISPATCHED;

    public boolean isAccepted() {
        return this == ACCEPTED;
    }
}
