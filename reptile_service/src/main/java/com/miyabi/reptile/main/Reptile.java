package com.miyabi.reptile.main;

import com.miyabi.reptile.MaxUrlException;
import com.miyabi.reptile.analysis.DanbooruAnalyzer;
import com.miyabi.reptile.net.Client;
import com.miyabi.reptile.net.RequestFactory;
import com.miyabi.reptile.net.download.Downloader;
import com.miyabi.reptile.persistence.DefaultPersistence;
import com.miyabi.reptile.persistence.Persistence;
import com.miyabi.reptile.url.DanbooruUrlFactory;
import com.miyabi.reptile.url.UrlFactory;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.http.HttpTimeoutException;
import java.nio.file.FileSystems;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 爬虫
 *
 * @author miyabi
 * @date 2021-03-31-16-45
 * @since 1.0
 **/


public class Reptile {
    private final static String DEFAULT_SAVE_PATH = "D:\\reptile\\img";
    private final Client client;
    private final Persistence persistence;
    private final UrlFactory urlFactory;
    private final ConcurrentLinkedQueue<String> queue1 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> queue2 = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<String> queue3 = new ConcurrentLinkedQueue<>();
    private RandomAccessFile idFile;
    private long lastId;
    private int downloadCount;
    private boolean useOldFile;
    private int concurrentDownload = 6;


    private Reptile(Client client, String savePath, Persistence persistence, UrlFactory urlFactory, boolean useOldFile) throws IOException {
        this.client = client;
        this.persistence = persistence;
        this.urlFactory = urlFactory;
        var tempFile = new File(savePath + FileSystems.getDefault().getSeparator() + "reptile.log");
        if (!tempFile.exists()) {
            if (!tempFile.createNewFile()) {
                throw new RuntimeException("创建ID保存文件失败");
            }
        }
        idFile = new RandomAccessFile(tempFile, "rw");
        this.useOldFile = useOldFile;
    }

    public static ReptileBuilder newBuilder() {
        return new ReptileBuilder();
    }

    private synchronized String getNewUrl(long maxId) throws MaxUrlException {
        var id = urlFactory.getId();
        if (id + 1 > maxId) {
            throw new MaxUrlException("超过最新Id");
        }
        var url = urlFactory.getNextObjUrl();
        lastId = id + 1;
        return url;
    }

    private synchronized void saveCurrentId() throws IOException {
        idFile.seek(0);
        idFile.writeLong(lastId);
    }

    public void start(long period) {
        if (useOldFile) {
            try {
                var id = idFile.readLong();
                urlFactory.setId(id);
                lastId = id;
                System.out.println("上一次ID: " + id);
            } catch (EOFException eofException) {
                System.out.println("ID文件为空，使用初始化ID");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lastId = urlFactory.getId();
            System.out.println("传入ID");
        }
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Reptile.this.run();
            }
        }, 0, period);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveCurrentId();
                idFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        //run();
        retry();
    }

  /*  public void start(long period) {
        if (useOldFile) {
            try {
                var id = idFile.readLong();
                urlFactory.setId(id);
                lastId = id;
                System.out.println("上一次ID: " + id);
            } catch (EOFException eofException) {
                System.out.println("ID文件为空，使用初始化ID");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            lastId = urlFactory.getId();
            System.out.println("传入ID");
        }
        var timer = new Timer();
        var date = LocalDateTime.now();
        var init = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), 0, 0);
        var calendar = Calendar.getInstance();
        while (!init.isAfter(date)) {
            init = init.plusMinutes(period);
        }
        calendar.clear();
        calendar.set(init.getYear(), init.getMonthValue() - 1, init.getDayOfMonth(), init.getHour(), init.getMinute());
        var startTime = calendar.getTime();
        System.out.println("启动时间: " + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(startTime));
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Reptile.this.run();
            }
        }, startTime, period);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveCurrentId();
                idFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        //run();
        retry();
    }*/

    public synchronized void saveId() throws IOException {
        if (++downloadCount == 4) {
            saveCurrentId();
            downloadCount = 0;
        }
    }


    private void run() {
        try {
            var maxId = urlFactory.getMaxId();
            var endCount = new AtomicInteger(concurrentDownload);
            var timeOutCount = new AtomicInteger(0);

            List<Thread> threads = new ArrayList<>(concurrentDownload);

            IntStream.range(0, concurrentDownload).forEach(i -> threads.add(new Thread(() -> {
                var url = "";
                while (true) {
                    try {
                        url = getNewUrl(maxId);
                        saveId();
                        var res = client.sendRetString(RequestFactory.getInstance().getRequest(url));
                        var pt = persistence.saveBaseInfo(res.body());
                        if (pt == null) {
                            queue1.offer(url);
                            System.out.println("没有获取到下载链接: " + url);
                        } else {
                            System.out.println("保存路径: " + pt);
                        }
                        timeOutCount.set(0);
                    } catch (MaxUrlException e) {
                        System.out.println(e.getMessage());
                        endCount.decrementAndGet();
                        break;
                    } catch (HttpTimeoutException e) {
                        e.printStackTrace();
                        queue1.offer(url);
                        if (timeOutCount.incrementAndGet() >= 4) {
                            try {
                                System.out.println("超时次数过多，休眠5分钟");
                                TimeUnit.MINUTES.sleep(5);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                        queue1.offer(url);
                    }
                }
            })));

            threads.forEach(Thread::start);
            while (endCount.get() != 0) {
                TimeUnit.MINUTES.sleep(5);
            }

            saveCurrentId();
            System.out.println("已更新至最新");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void retry() {
        var schedule1 = new ScheduledThreadPoolExecutor(3, new ThreadPoolExecutor.CallerRunsPolicy());
        schedule1.scheduleAtFixedRate(() -> retryQueue(queue1, queue2), 0, 10, TimeUnit.MINUTES);
        schedule1.scheduleAtFixedRate(() -> retryQueue(queue2, queue3), 0, 1, TimeUnit.HOURS);
        schedule1.scheduleAtFixedRate(() -> retryQueue(queue3, null), 0, 12, TimeUnit.HOURS);
    }

    private void retryQueue(ConcurrentLinkedQueue<String> queue, ConcurrentLinkedQueue<String> nextQueue) {
        var concurrentDownload = 2;
        IntStream.range(0, concurrentDownload).forEach(i -> new Thread(() -> {
            while (true) {
                var url = queue.poll();
                if (url == null) {
                    break;
                }
                try {
                    var res = client.sendRetString(RequestFactory.getInstance().getRequest(url));
                    var pt = persistence.saveBaseInfo(res.body());
                    if (pt == null && nextQueue != null) {
                        nextQueue.offer(url);
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    if (nextQueue != null) {
                        nextQueue.offer(url);
                    }
                }
            }
        }).start());
    }

    public static class ReptileBuilder {
        private Client client;
        private String savePath;
        private ComponentGroup componentGroup;
        private Map<String, List<String>> headers;
        private boolean useOldFile = false;

        public ReptileBuilder setHeaders(Map<String, List<String>> headers) {
            this.headers = headers;
            return this;
        }

        public ReptileBuilder setComponentGroup(ComponentGroup componentGroup) {
            this.componentGroup = componentGroup;
            return this;
        }

        public ReptileBuilder setClient(Client client) {
            this.client = client;
            return this;
        }

        public ReptileBuilder setSavePath(String savePath) {
            this.savePath = savePath;
            return this;
        }

        public ReptileBuilder setUseOldFile(boolean useOldFile) {
            this.useOldFile = useOldFile;
            return this;
        }

        public Reptile build() throws IOException {
            if (client == null) {
                client = new Client();
            }
            if (savePath == null) {
                savePath = DEFAULT_SAVE_PATH;
            }
            UrlFactory urlFactory;
            Persistence persistence;
            if (componentGroup == null) {
                var downloader = new Downloader(client, savePath, headers);
                persistence = new DefaultPersistence(new DanbooruAnalyzer(), downloader);
                urlFactory = new DanbooruUrlFactory(1, client);
            } else {
                persistence = componentGroup.getPersistence();
                urlFactory = componentGroup.getUrlFactory();
            }
            return new Reptile(client, savePath, persistence, urlFactory, useOldFile);
        }
    }

}
