package com.miyabi.reptile.persistence;

import com.megumi.common.IdGenerator;
import com.megumi.pojo.PTag;
import com.megumi.pojo.Picture;
import com.megumi.pojo.User;
import com.miyabi.reptile.analysis.ResultAnalyzer;
import com.miyabi.reptile.net.download.Downloader;
import com.miyabi.reptile.util.DbUtil;
import com.miyabi.reptile.util.JSONUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * danbooru
 *
 * @author miyabi
 * @date 2021-03-31-15-00
 * @since 1.0
 **/

public class DefaultPersistence implements Persistence {
    private static final List<Picture> picturesList = new ArrayList<>(128);
    private static final Set<String> r18Tags = new HashSet<>(32);
    private static final User uploader = new User(6792575158608601088L);
    private static final String smallSavePath;
    private static final String savePath;

    static {
        smallSavePath = ResourceBundle.getBundle("server").getString("file.img.path");
        savePath = ResourceBundle.getBundle("server").getString("file.img.small.path");
    }

    private final ResultAnalyzer<Map<String, Object>> analyzer;
    private final Downloader downloader;

    public DefaultPersistence(ResultAnalyzer<Map<String, Object>> analyzer, Downloader downloader) {
        this.analyzer = analyzer;
        this.downloader = downloader;
        initR18Tags();
        saveToDB();
    }

    public void saveToDB() {
        var timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                var idGenerator = IdGenerator.getInstance();
                var tags = new HashSet<String>(512);
                var tempPictureList = new ArrayList<>(picturesList);
                tempPictureList.forEach(picture -> tags.addAll(picture.getTagNameList()));
                var tagList = new ArrayList<>(tags);
                var sb = new StringBuilder("select id, name from p_tag where name in (");
                if (!tagList.isEmpty()) {
                    for (int i = 0; i < tagList.size(); i++) {
                        tagList.set(i, tagList.get(i).replace("'", "\\'"));
                        if (i == 998) {
                            sb.deleteCharAt(sb.length() - 1).append(") or name in (");
                        }
                        sb.append("'").append(tagList.get(i)).append("',");
                        if (tagList.get(i).contains("s_blade_ciaran")) {
                            System.out.println(tagList.get(i));
                        }
                    }
                    sb.deleteCharAt(sb.length() - 1).append(')');
                }
                try {
                    var connection = DbUtil.getConnection();
                    var existTags = new ArrayList<PTag>();
                    var existTagNames = new ArrayList<String>();
                    if (!tagList.isEmpty()) {
                        System.out.println(sb.toString());
                        var rs = connection.createStatement().executeQuery(sb.toString());
                        while (rs.next()) {
                            existTagNames.add(rs.getString("name"));
                            var tag = new PTag();
                            tag.setId(rs.getLong("id"));
                            tag.setName(rs.getString("name"));
                            existTags.add(tag);
                        }
                        tagList.removeAll(existTagNames);

                        var tagInSt = connection.createStatement();
                        for (String s : tagList) {
                            var tag = new PTag(idGenerator.getNextId(), s, null, null, null);
                            existTags.add(tag);
                            tagInSt.addBatch("insert into p_tag(id,create_time,name,views,user_id) values(" + tag.getId() + ",'"
                                             + LocalDateTime.now() + "','" + s + "'," + 0 + "," + uploader.getId() + ")");
                        }
                        tagInSt.executeBatch();
                    }

                    var pictureInSt = connection.createStatement();
                    for (Picture picture : tempPictureList) {
                        picture.setId(idGenerator.getNextId());
                        var path = picture.getPath();
                        var smallPath = picture.getSmallPath();
                        var name = path.substring(path.lastIndexOf(File.separator) + 1);
                        var smallName = smallPath.substring(smallPath.lastIndexOf(File.separator) + 1);
                        Files.move(Path.of(path), Path.of(savePath, name));
                        Files.move(Path.of(smallPath), Path.of(smallSavePath, smallName));
                        picture.setPath(name);
                        picture.setSmallPath(smallName);
                        pictureInSt.addBatch("insert into picture(id, approval, collect, is_sexy, path, size, small_path, src, up_time, views, x, y, user_id, d_src) values(" +
                                             picture.getId() + "," + picture.getApproval() + "," + picture.getCollection() + ",'" + picture.getIsSexy() + "','" + picture.getPath() + "'," +
                                             picture.getSize() + ",'" + picture.getSmallPath() + "','" + picture.getSrc() + "','" + LocalDateTime.now() + "'," + picture.getViews() + "," + picture.getX() + "," +
                                             picture.getY() + "," + uploader.getId() + ",'" + picture.getDSrc() + "')");
                    }
                    pictureInSt.executeBatch();

                    picturesList.removeAll(tempPictureList);

                    if (!tagList.isEmpty()) {
                        var pictureTagInSt = connection.createStatement();
                        var tagsMap = new HashMap<String, Long>();
                        existTags.forEach(tag -> tagsMap.put(tag.getName(), tag.getId()));
                        for (Picture picture : tempPictureList) {
                            for (String s : picture.getTagNameList()) {
                                pictureTagInSt.addBatch("insert into tag_picture(id, tag_id, picture_id) values(" + idGenerator.getNextId() + "," + tagsMap.get(s) + "," + picture.getId() + ")");
                            }
                        }
                        pictureTagInSt.executeBatch();
                    }

                    System.out.println("保存至数据库成功 : " + LocalDateTime.now());
                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            }
        }, 30000, 600000);
    }

    private void initR18Tags() {
        var tags = new String[]{"nipples", "areolae", "female_pubic_hair", "mosaic_censoring", "penis",
                "labia","anus","piledriver","spread_anus",
                "pubic_hair", "vaginal", "sex", "completely_nude", "imminent_fellatio"};
        r18Tags.addAll(Arrays.asList(tags));
    }

    private String isR18(List<String> tagNames) {
        for (String tagName : tagNames) {
            if (r18Tags.contains(tagName)) {
                return "1";
            }
        }
        return "0";
    }


    @Override
    public String saveBaseInfo(String result) throws IOException, InterruptedException {
        var map = JSONUtil.strToObjectMap(result);
        var downloadUrl = analyzer.getDownloadUrl(map);
        if (downloadUrl != null) {
            var savePath = downloadImg(downloadUrl).toString();
            var smallUrl = analyzer.smallUrl(map);
            var smallSavePath = savePath;
            if (smallUrl != null) {
                smallSavePath = downloadImg(smallUrl).toString();
            }
            if (savePath == null) {
                return null;
            }
            var picture = new Picture();
            picture.setX(analyzer.width(map));
            picture.setY(analyzer.height(map));
            picture.setSrc(analyzer.getBSource(map));
            picture.setPath(savePath);
            picture.setSmallPath(smallSavePath);
            picture.setSize(analyzer.getSize(map));
            picture.setIsSexy(isR18(analyzer.getGeneral(map)));
            picture.setDSrc(analyzer.getBSource(map));
            picture.setTagNameList(analyzer.getGeneral(map));
            picture.init();
            picturesList.add(picture);
            return savePath;
        }
        return null;
    }

    @Override
    public Path downloadImg(String url) throws IOException, InterruptedException {
        return downloader.download(url);
    }
}
