package handlers.impl;

import handlers.annotations.Mapping;
import handlers.api.URIHandler;
import io.netty.handler.codec.http.HttpRequest;
import statistic.Logger;
import statistic.RequestsCounter;
import statistic.Statistic;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * StatusHandler retrieve full traffic statistic
 */
@Mapping(uri = "/status")
public class StatusHandler implements URIHandler {

    @Override
    public void handle(HttpRequest request, StringBuilder builder) {
        builder.append("------------------------STATISTIC------------------------------\n\n");

        builder.append("Total number of requests: " + Statistic.REQUESTS_COUNTER + "\n\n");

        builder.append("THE NUMBER OF  UNIQUE REQUESTS ON IP: \n");
        ConcurrentHashMap<String, CopyOnWriteArraySet<String>> counter = Statistic.IP_UNIQUE_REQUESTS_COUNTER;
        for (Map.Entry<String, CopyOnWriteArraySet<String>> entries : counter.entrySet()) {
            builder.append("IP: " + entries.getKey() + "   Quantity: " + counter.get(entries.getKey()).size() + "\n\n");
        }

        builder.append("THE NUMBER OF REQUESTS ON IP: \n");
        builder.append("---------------------------------------------------------------\n");
        builder.append("|    IP      | Amount |          Time of last request         |\n");
        builder.append("---------------------------------------------------------------\n");
        ConcurrentHashMap<String, RequestsCounter> totalClientCounter = Statistic.IP_REQUESTS_COUNTER;
        for (Map.Entry<String, RequestsCounter> entries : totalClientCounter.entrySet()) {
            builder.append("|  " + entries.getKey() + " |     " + entries.getValue().getCounter() + "  |  "
                    + entries.getValue().getDate().getTime() + "        | \n");
        }
        builder.append("---------------------------------------------------------------\n\n");

        builder.append("THE NUMBER OF  REDIRECT REQUESTS: \n");
        ConcurrentHashMap<String, AtomicInteger> redirect_counter = Statistic.REDIRECT_COUNTER;
        if (redirect_counter.isEmpty()) {
            builder.append("no redirect requests\n\n");
        }
        for (Map.Entry<String, AtomicInteger> entries : redirect_counter.entrySet()) {
            builder.append("URL: " + entries.getKey() + "   Quantity: " + entries.getValue() + "\n\n");
        }

        builder.append("THE NUMBER OF OPEN CONNECTIONS: \n");
        builder.append(Statistic.CONNECTIONS_COUNTER + "\n\n\n");


        builder.append("THE TOTAL STATISTIC FOR LAST 16 REQUESTS: \n");
        LinkedList<Logger> logs = Statistic.logs;
        builder.append("--------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        builder.append("|   src_ip   |       URI       |          timestamp                          |        sent_bytes        |       received_bytes         |   speed (bytes/sec) |  \n");
        builder.append("--------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
        for (Logger requestInfo : logs) {
            builder.append("|  " + requestInfo.getIp() + " |   " + requestInfo.getUserRequest() + " |                " + requestInfo.getLastTime() +
                    "        |         " + requestInfo.getSentBytes() + "            |           " + requestInfo.getReceivedBytes() + "            |     no defined  \n");
                    }
        builder.append("--------------------------------------------------------------------------------------------------------------------------------------------------------------\n");
    }
}