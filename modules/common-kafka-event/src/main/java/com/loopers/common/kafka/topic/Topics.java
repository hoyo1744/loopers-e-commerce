package com.loopers.common.kafka.topic;

public class Topics {

    public static final String LIKE = "like-event";

    public static final String TRACE = "trace-event";

    public static final String ORDER = "order-event";

    public static final String PAGE_VIEW = "pageview-event";

    public static class Trace {
        public static final String LIKE_CREATED = "trace-like-created";
        public static final String LIKE_CANCELED = "trace-like-canceled";
        public static final String ORDER_COMPLETED = "trace-order-completed";
        public static final String PAYMENT_COMPLETED = "trace-payment-completed";
    }

    public static class Order {
        public static final String COMPLETED = "order-completed";
    }

    public static class Payment {
        public static final String REQUESTED = "payment-requested";
        public static final String COMPLETED = "payment-completed";
    }

    public static final String STOCK = "stock-event";

}
