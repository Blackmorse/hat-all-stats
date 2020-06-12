package com.blackmorse.hattrick.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;

@Component
@Slf4j
public class Telegram {
    private static final String URL_STRING =
            "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";

    private final String botToken;
    private final Integer chatId;
    private final String proxyHost;
    private final Integer proxyPort;

    @Autowired
    public Telegram(@Value("${telegram.botToken}") String botToken,
                    @Value("${telegram.chatId}") Integer chatId,
                    @Value("${telegram.socksHost}") String proxyHost,
                    @Value("${telegram.socksPort}") Integer proxyPort) {
        this.botToken = botToken;
        this.chatId = chatId;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }


    public void send(String message) {
        Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxyHost, proxyPort));

        String urlString = String.format(URL_STRING, botToken, chatId, message);
        try {
            URL url = new URL(urlString);
            HttpURLConnection socksConnection
                    = (HttpURLConnection) url.openConnection(proxy);

            try(InputStream is = new BufferedInputStream(socksConnection.getInputStream());) {
            }
            catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
