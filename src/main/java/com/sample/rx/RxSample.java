package com.sample.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

public class RxSample {

    public static void main(String args[]) {
        final EventProducer.Config config = EventProducer.Config.builder()
                .periodMillis(100)
                .delayMillis(200)
                .periodScheduler(Schedulers.immediate())
                .delayScheduler(Schedulers.immediate())
                .shareCount(3)
                .build();

        final EventProducer producer = new EventProducer(config);

        final Observable<String> sharedObservable = producer.events();

        sharedObservable.subscribe(item -> System.out.println("First subscriber: " + item + " " + System.currentTimeMillis()));
        sharedObservable.subscribe(item -> System.out.println("Second subscriber: " + item + " " + System.currentTimeMillis()));
        sharedObservable.subscribe(item -> System.out.println("Third subscriber: " + item + " " + System.currentTimeMillis()));
    }
}
