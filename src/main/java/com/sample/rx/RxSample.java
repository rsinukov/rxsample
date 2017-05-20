package com.sample.rx;

import rx.Observable;
import rx.schedulers.Schedulers;

public class RxSample {

    public static void main(String args[]) {
        final EventProducer producer = new EventProducer();

        final Observable<String> sharedObservable = producer.events(100, 200, Schedulers.immediate(), Schedulers.immediate(), 3);

        sharedObservable.subscribe(item -> System.out.println("First subscriber: " + item));
        sharedObservable.subscribe(item -> System.out.println("Second subscriber: " + item));
        sharedObservable.subscribe(item -> System.out.println("Third subscriber: " + item));
    }
}
