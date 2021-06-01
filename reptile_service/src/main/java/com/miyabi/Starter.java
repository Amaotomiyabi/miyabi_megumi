package com.miyabi;

import com.miyabi.reptile.analysis.DanbooruAnalyzer;
import com.miyabi.reptile.main.ComponentGroup;
import com.miyabi.reptile.main.Reptile;
import com.miyabi.reptile.net.Client;
import com.miyabi.reptile.net.download.Downloader;
import com.miyabi.reptile.persistence.DefaultPersistence;
import com.miyabi.reptile.url.DanbooruUrlFactory;
import com.miyabi.reptile.util.WebsiteDict;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * 启动器】
 *
 * @author miyabi
 * @date 2021-04-02-11-06
 * @since 1.0
 **/

@SpringBootApplication
public class Starter {
    private static long id = 1;
    private static String dest = "E:\\迅雷下载\\DownloadImg";
    private static String name = WebsiteDict.DANBOORU;
    private static boolean useLog = false;
    private static boolean useProxy = false;
    private static boolean useAuth = false;

    public static void main(String[] args) throws IOException {
        resolveArgs(args);
        var builder = Reptile.newBuilder();
        var client = getClient(useProxy, useAuth);
        //var savePath = "C:\\Users\\sss\\Desktop\\uu";
        var reptile = builder.setComponentGroup(getComponentGroup(name, client, dest, null))
                .setSavePath(dest)
                .setClient(client)
                .setUseOldFile(useLog)
                .build();
        getConsoleInfo();
        reptile.start(600000);
    }

    public static Client getClient(boolean useProxy, boolean useAuth) {
        if (useProxy) {
            var proxy = ProxySelector.of(new InetSocketAddress("127.0.0.1", 4780));
            return new Client(null, proxy);
        }
        return new Client(null, null);
    }

    public static ComponentGroup getComponentGroup(String name, Client client, String dest, Map<String, List<String>> headers) throws IOException {
        return switch (name) {
            case "DANBOORU" -> new ComponentGroup(
                    new DefaultPersistence(new DanbooruAnalyzer(), new Downloader(client, dest, headers)),
                    new DanbooruUrlFactory(id, client));
            case "XXX" -> null;
            default -> throw new IllegalArgumentException("没有对应的爬虫构件组");
        };
    }

    public static void getConsoleInfo() {
        new Thread(() -> {
            var s = System.in;
            Scanner scanner = new Scanner(s);
            while (true) {
                var str = scanner.nextLine();
                if ("exit".equalsIgnoreCase(str)) {
                    System.exit(0);
                }
            }
        }).start();
    }

    public static void resolveArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-up", "--use_proxy" -> useProxy = true;
                case "-ua", "--use_auth" -> useAuth = true;
                case "-ul", "--use_log" -> useLog = true;
                case "-s", "--start" -> id = Long.parseLong(args[i + 1]);
                case "-d", "--dest" -> dest = args[i + 1];
                case "-n", "--name" -> name = args[i + 1];
            }
        }
    }
}
