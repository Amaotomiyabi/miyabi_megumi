import com.megumi.BusinessApplication;
import com.megumi.pojo.PTag;
import com.megumi.pojo.Picture;
import com.megumi.pojo.User;
import com.megumi.pojo.middle.ImgUserHistory;
import com.megumi.repository.*;
import com.megumi.service.PictureService;
import com.megumi.service.TagService;
import com.megumi.service.UserService;
import com.megumi.util.exception.IllegalRequestParameterException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.message.AuthException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 2021/2/22
 *
 * @author miyabi
 * @since 1.0
 */
@SpringBootTest(classes = BusinessApplication.class)
public class TestApp {
    @Autowired
    UserRepo userRepo;
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    PictureService pictureService;
    @Autowired
    PictureRepo pictureRepo;
    @Autowired
    ImgUserHistoryRepo imgUserHistoryRepo;
    @Autowired
    TagService tagService;
    @Autowired
    private FollowerRepo followerRepo;
    @Autowired
    private PTagRepo pTagRepo;
    @Autowired
    private ImgTagRepo imgTagRepo;

}
