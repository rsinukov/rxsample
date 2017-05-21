package com.sample.rx;


import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.TestScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

public class EventProducerTest {

    private TestScheduler delayScheduler;

    private TestScheduler periodScheduler;

    @Before
    public void setUp() throws Exception {
        delayScheduler = new TestScheduler();
        periodScheduler = new TestScheduler();
        delayScheduler.advanceTimeTo(1, TimeUnit.MILLISECONDS);
        periodScheduler.advanceTimeTo(1, TimeUnit.MILLISECONDS);
    }

    @Test
    public void eventsObservable_emitsItemsPeriodically() throws Exception {
        final Observable<String> events = getProducer(1, 0, 1).events();

        final TestSubscriber<String> subscriber = new TestSubscriber<>();
        events.subscribe(subscriber);

        for (int i = 1; i < 20; i++) {
            periodScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
            delayScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
            subscriber.assertValueCount(i);
        }
    }

    @Test
    public void eventsObservable_emitsItemsWithDelay() throws Exception {
        long delay = 10;
        final Observable<String> events = getProducer(1, delay, 1).events();

        final TestSubscriber<String> subscriber = new TestSubscriber<>();
        events.subscribe(subscriber);

        periodScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        delayScheduler.advanceTimeBy(delay - 1, TimeUnit.MILLISECONDS);
        subscriber.assertNoValues();

        delayScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        subscriber.assertValueCount(1);
    }

    @Test
    public void eventsObservable_subscribesOnlyAfterShareCountReach() throws Exception {
        final int scareCount = 5;

        final Observable<String> events = getProducer(scareCount, 0, 1).events();

        final List<TestSubscriber<String>> subscribers = new ArrayList<>(scareCount);

        // subscribe less then required subscribers
        for (int i = 0; i < scareCount - 1; i++) {
            final TestSubscriber<String> subscriber = new TestSubscriber<>();
            subscribers.add(subscriber);
            events.subscribe(subscriber);
        }

        periodScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        delayScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        subscribers.forEach(TestSubscriber::assertNoValues);

        // subscribe final subscriber
        final TestSubscriber<String> subscriber = new TestSubscriber<>();
        subscribers.add(subscriber);
        events.subscribe(subscriber);

        periodScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        delayScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        // items received
        subscribers.forEach(s -> assertThat(s.getValueCount()).isGreaterThan(0));
    }

    @Test
    public void eventsObservable_emitsSameItemsForAllSubscribers() throws Exception {
        final Observable<String> events = getProducer(2, 0, 1).events();

        final TestSubscriber<String> subscriber1 = new TestSubscriber<>();
        events.subscribe(subscriber1);
        final TestSubscriber<String> subscriber2 = new TestSubscriber<>();
        events.subscribe(subscriber2);

        periodScheduler.advanceTimeBy(5, TimeUnit.MILLISECONDS);
        delayScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        final List<String> items1 = subscriber1.getOnNextEvents();
        final List<String> items2 = subscriber2.getOnNextEvents();

        assertThat(items1).hasSameSizeAs(items2);
        for (int i = 0; i < items1.size(); i++) {
            assertThat(items1.get(i)).isSameAs(items2.get(i));
        }
    }

    @Test
    public void eventsObservable_emitsNaturalNumbersAsString() throws Exception {
        final Observable<String> events = getProducer(1, 0, 1).events();

        final TestSubscriber<String> subscriber = new TestSubscriber<>();
        events.subscribe(subscriber);

        periodScheduler.advanceTimeBy(5, TimeUnit.MILLISECONDS);
        delayScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        subscriber.assertValues("0", "1", "2", "3", "4");
    }

    private EventProducer getProducer(int scareCount, long delayMillis, int periodMillis) {
        final EventProducer.Config config = EventProducer.Config.builder()
                .shareCount(scareCount)
                .delayScheduler(delayScheduler)
                .periodScheduler(periodScheduler)
                .delayMillis(delayMillis)
                .periodMillis(periodMillis)
                .build();
        return new EventProducer(config);
    }
}