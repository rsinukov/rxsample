package com.sample.rx;

import rx.schedulers.Schedulers;

public class RxSample {

    public static void main(String args[]) {
        final EventProducer producer = new EventProducer();

        producer.events(100, Schedulers.immediate())
                .subscribe(item -> System.out.println(item));
    }
}
