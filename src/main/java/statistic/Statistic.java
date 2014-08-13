package statistic;

import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Statistic class represents static methods for computing statistic date
 */
public class Statistic {
    public static final ConcurrentHashMap<String, RequestsCounter> IP_REQUESTS_COUNTER = new ConcurrentHashMap<String, RequestsCounter>();
    public static final ConcurrentHashMap<String, CopyOnWriteArraySet<String>> IP_UNIQUE_REQUESTS_COUNTER = new ConcurrentHashMap<String, CopyOnWriteArraySet<String>>();
    public static final ConcurrentHashMap<String, AtomicInteger> REDIRECT_COUNTER = new ConcurrentHashMap<String, AtomicInteger>();
    public static final AtomicInteger REQUESTS_COUNTER = new AtomicInteger();
    public static final AtomicInteger CONNECTIONS_COUNTER = new AtomicInteger();
    public static final LinkedList<Logger> logs = new LinkedList<Logger>();


    public static void refreshStatistic(String clientIP, String url) {
        count();
        countClientRequests(clientIP);
        countUniqueRequests(clientIP, url);
    }

    public static void countUniqueRequests(String clientIP, String url) {
        IP_UNIQUE_REQUESTS_COUNTER.putIfAbsent(clientIP, new CopyOnWriteArraySet<String>());
        IP_UNIQUE_REQUESTS_COUNTER.get(clientIP).add(url);
    }

    public static void count() {
        REQUESTS_COUNTER.incrementAndGet();
    }

    public static void countClientRequests(String clientIP) {

        IP_REQUESTS_COUNTER.putIfAbsent(clientIP, new RequestsCounter());
        RequestsCounter requestsCounter = IP_REQUESTS_COUNTER.get(clientIP);

        synchronized (requestsCounter) {
            requestsCounter.incrementCounter();
            requestsCounter.refreshDate(Calendar.getInstance());
        }
    }

    public static void countRedirectRequests(String url) {
        REDIRECT_COUNTER.putIfAbsent(url, new AtomicInteger());
        REDIRECT_COUNTER.get(url).incrementAndGet();
    }


    public static void incrementOpenedConnections() {
        CONNECTIONS_COUNTER.incrementAndGet();
    }

    public static void decrementOpenedConnections() {
        CONNECTIONS_COUNTER.decrementAndGet();
    }


    public synchronized static void addRequestToStatistic(Logger logger) {
        int cacheSize = logs.size();
        if (cacheSize == 16) {
            logs.removeLast();
        }
        logs.addFirst(logger);
    }
}
