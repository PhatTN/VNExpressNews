package com.example.phat.vnexpressnews.io;

import android.test.suitebuilder.annotation.MediumTest;

import com.example.phat.vnexpressnews.exceptions.InternalServerErrorException;
import com.example.phat.vnexpressnews.exceptions.JacksonProcessingException;
import com.example.phat.vnexpressnews.model.Article;
import com.example.phat.vnexpressnews.model.Category;
import com.example.phat.vnexpressnews.model.Photo;
import com.example.phat.vnexpressnews.model.ReferenceArticle;
import com.example.phat.vnexpressnews.model.Video;
import com.example.phat.vnexpressnews.util.CollectionUtils;
import com.example.phat.vnexpressnews.util.FileSystemUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@MediumTest
@RunWith(MockitoJUnitRunner.class)
public class FullArticleHandlerTest {

    private String jsonString;
    private JSONHandler<Article> jsonHandler;
    private Article mockArticle;

    // All of this properties are got from json file which is used for this test
    private static final int MOCK_ARTICLE_TYPE = 3;
    private static final int MOCK_ORIGINAL_CATEGORY = 1003459;
    private static final int MOCK_SITE_ID = 1000000;
    private static final String MOCK_TITLE = "5 lực lượng xuống đường chống ùn tắc ở Hà Nội";
    private static final String MOCK_LEAD = "Sáng 19/10, 30 người gồm cả cảnh sát cơ động, cảnh sát trật tự được huy động hướng dẫn giao thông tại nút giao Nguyễn Trãi - Khuất Duy Tiến (Thanh Xuân, Hà Nội) khiến tình trạng tắc nghẽn ở \"điểm đen\" này giảm đáng kể.";
    private static final String MOCK_SHARE_URL = "http://vnexpress.net/photo/giao-thong/5-luc-luong-xuong-duong-chong-un-tac-o-ha-noi-3297999.html";
    private static final String MOCK_THUMBNAIL_URL = "http://m.f32.img.vnecdn.net/2015/10/19/a-1445221119.jpg";
    private static final int MOCK_PRIVACY = 6;
    private static final List<ReferenceArticle> MOCK_REFERENCE_ARTICLES = setUpMockReferenceArticles();
    private static final int MOCK_TOTAL_PAGE = 1;
    private static final long MOCK_PUBLISH_TIME = 1445229636;
    private static final int MOCK_TOTAL_COMMENT = 25;
    private static final int MOCK_ARTICLE_ID = 3297999;
    private static final String MOCK_CONTENT = "<p style=\"text-align:right;\">\n\t<strong>Bá Đô</strong></p>";
    private static final List<Photo> MOCK_PHOTOS = setUpMockPhotos();
    private static final int MOCK_MODE_VIEW = 0;
    private static final Category MOCK_CATEGORY_PARENT = setUpCategoryParent();
    private static final List<Video> MOCK_LIST_VIDEO = setUpListVideo();


    private static List<ReferenceArticle> setUpMockReferenceArticles() {
        ReferenceArticle referenceArticle = new ReferenceArticle();
        referenceArticle.setArticleId(3297810);
        referenceArticle.setTitle("Hà Nội huy động thêm 200 cảnh sát cơ động chống ùn tắc");
        referenceArticle.setArticleType(1);
        referenceArticle.setShareUrl("http://vnexpress.net/tin-tuc/thoi-su/giao-thong/ha-noi-huy-dong-them-200-canh-sat-co-dong-chong-un-tac-3297810.html");
        referenceArticle.setThumbnailUrl("http://m.f31.img.vnecdn.net/2015/10/18/tacduong-1200x0-4831-1445171332.jpg");

        List<ReferenceArticle> temp = new ArrayList<>(1);
        temp.add(referenceArticle);
        return temp;
    }

    private static List<Photo> setUpMockPhotos() {
        List<Photo> photos = new ArrayList<>(3);

        Photo ph = new Photo(
                23075971, // photoId
                "http://m.f32.img.vnecdn.net/2015/10/19/a-1445221119.jpg", // photo thumbnail url
                "<p class=\"Normal\">\n\t<span>Theo chỉ thị của Giám đốc Công an thành phố Hà Nội, từ nay đến hết ngày 28/2/2016 (sau Tết Nguyên đán), để giảm thiểu tình trạng ùn tắc giao thông, vi phạm trật tự, sẽ huy động thêm 200 cảnh sát cơ động, 100% quân số cảnh sát giao thông, trật tự, công an phường, phối hợp với các lực lượng khác để đảm bảo an toàn giao thông, chống ùn tắc vào giờ cao điểm.</span></p>\n<p class=\"Normal\">\n\tTừ 6h30 sáng 19/10 tại các nút giao trọng điểm thường xuyên xảy ra ùn tắc đã có thêm cảnh sát trật tự, cảnh sát cơ động tới điều tiết giao thông.</p>"); // photo caption
        photos.add(ph);

        ph = new Photo(
                23075972, // photoId
                "http://m.f31.img.vnecdn.net/2015/10/19/IMG-7170-1445224956.jpg", // photo thumbnail url
                "<p class=\"Normal\">\n\tCông an Hà Nội huy động khoảng 200 cán bộ, chiến sĩ thuộc Trung đoàn cảnh sát cơ động tham gia hướng dẫn giao thông. Sáng nay, riêng tại nút giao Nguyễn Trãi - Khuất Duy Tiến, nơi được xem là ùn tắc nhất thủ đô, 8 chiến sĩ cảnh sát cơ động đã tới hỗ trợ cảnh sát giao thông làm nhiệm vụ. </p>"); // photo caption
        photos.add(ph);

        ph = new Photo(
                23075973, // photoId
                "http://m.f31.img.vnecdn.net/2015/10/19/a4-1445221121.jpg", // photo thumbnail url
                "<p class=\"Normal\">\n\tNgoài việc phân luồng, ngăn chặn hành vi vi phạm, cảnh sát cơ động còn phối hợp với cảnh sát giao thông giúp đỡ những người không may bị va chạm giao thông, đưa xe vào khu vực an toàn.</p>"); // photo caption
        photos.add(ph);

        return photos;
    }

    private static Category setUpCategoryParent() {
        return new Category(
                1001005, // categoryId
                "Thời sự", // category name
                "thoi-su", // category code
                1000000, // parent id
                1000000, // full parent
                1, // show folder
                1 // display order
        );
    }

    private static List<Video> setUpListVideo() {
        List<Video> videos = new ArrayList<>(1);

        Map<String, String> sizeFormat = new HashMap<>();
        sizeFormat.put("240", "http://video.vnecdn.net/video/web/mp4/240p/2015/10/19/200-canh-sat-co-dong-ha-noi-ra-duong-chong-un-tac-1445226171.mp4");
        sizeFormat.put("360", "http://video.vnecdn.net/video/web/mp4/360p/2015/10/19/200-canh-sat-co-dong-ha-noi-ra-duong-chong-un-tac-1445226171.mp4");
        sizeFormat.put("480", "http://video.vnecdn.net/video/web/mp4/480p/2015/10/19/200-canh-sat-co-dong-ha-noi-ra-duong-chong-un-tac-1445226171.mp4");
        sizeFormat.put("720", "http://video.vnecdn.net/video/web/mp4/2015/10/19/200-canh-sat-co-dong-ha-noi-ra-duong-chong-un-tac-1445226171.mp4");

        Video v = new Video(
                68963, // video ID
                "http://video.vnecdn.net/video/web/mp4/360p/2015/10/19/200-canh-sat-co-dong-ha-noi-ra-duong-chong-un-tac-1445226171.mp4", // video url
                "200 cảnh sát cơ động Hà Nội ra đường chống ùn tắc", // video caption
                "Cảnh sát cơ động Hà Nội được huy động vào những giờ cao điểm để chống ùn tắc giao thông.", // video description
                "http://img.video.vnecdn.net/web/2015/10/19/200-canh-sat-co-dong-ha-noi-ra-duong-chong-un-tac-1445226220.jpg", // video thumbnail url
                sizeFormat // video size format
        );
        videos.add(v);

        return videos;
    }

    @Before
    public void setUp() {
        jsonHandler = new FullArticleHandler();

        // create mock article;
        mockArticle = new Article(
                MOCK_ARTICLE_ID, // article id
                MOCK_ARTICLE_TYPE, // ARTICLE TYPE
                MOCK_ORIGINAL_CATEGORY, // ORIGINAL CATEGORY
                MOCK_TITLE, // TITLE
                MOCK_LEAD, // LEAD
                MOCK_SHARE_URL, // SHARE URL
                MOCK_THUMBNAIL_URL, // THUMBNAIL URL
                MOCK_PRIVACY, // PRIVACY
                MOCK_TOTAL_PAGE, // TOTAL PAGE
                MOCK_TOTAL_COMMENT, // TOTAL COMMENT
                MOCK_PUBLISH_TIME, // PUBLISH TIME
                MOCK_SITE_ID, // SITE ID
                MOCK_CONTENT, // ARTICLE CONTENT
                MOCK_PHOTOS, // PHOTOS
                MOCK_MODE_VIEW, // MODE VIEW
                MOCK_CATEGORY_PARENT, // CATEGORY PARENT
                MOCK_LIST_VIDEO, // LIST VIDEO
                MOCK_REFERENCE_ARTICLES // LIST REFERENCE ARTICLE
        );
    }

    @Test
    public void parseJson_CorrectFormat_ArticleParsedCorrectly()
            throws JacksonProcessingException, InternalServerErrorException {
        initWithCorrectFormatJsonResource();

        Article article = jsonHandler.process(jsonString);

        assertThat("Expected: The article is not null. But it isn't", article, is(notNullValue()));
        assertThat("Expected: The article is same as the MockArticle, but it isn't", compareTwoArticles(mockArticle, article), is(true));
    }

    @Test(expected = InternalServerErrorException.class)
    public void parseJson_CorrectFormatWithErrorCodeIsNotEqualZero_ThrowsISEE()
            throws JacksonProcessingException, InternalServerErrorException {
        initWithCorrectFormatJsonResourceWithErrorCodeIsNotEqualZero();
        jsonHandler.process(jsonString);
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_InCorrectFormat_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException {
        initWithInCorrectFormatJsonResource();
        jsonHandler.process(jsonString);
    }

    @Test(expected = JacksonProcessingException.class)
    public void parseJson_NullJsonResources_ThrowsJPE()
            throws JacksonProcessingException, InternalServerErrorException {
        initWithANullJsonResource();
        jsonHandler.process(jsonString);
    }

    private void initWithCorrectFormatJsonResource() {
        final String filename = "article_correctFormat.json";
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithCorrectFormatJsonResourceWithErrorCodeIsNotEqualZero() {
        final String filename = "article_correctFormat_withErrorCodeIsNotEqualZero.json";
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithInCorrectFormatJsonResource() {
        final String filename = "article_incorrectFormat.json"; // incorrect format in photos node
        jsonString = FileSystemUtils.loadJsonFile(filename);
    }

    private void initWithANullJsonResource() {
        jsonString = null;
    }

    /**
     * This is helper method in order to compare two {@link Article}.
     * We can use {@link Article#equals(Object)} method. But that method only
     * compares articleId and articleTitle property. In this test, we need to compare
     * all it's properties. So we write own comparison method
     * @return true if they are same, otherwise is false
     */
    private static boolean compareTwoArticles(Article one, Article two) {
        // we will compare each their property respectively. If has any property is not
        // same with other. This method will return false

        return one.getArticleId() == two.getArticleId()
                && one.getArticleType() == two.getArticleType()
                && one.getOriginalCategory() == two.getOriginalCategory()
                && one.getTitle().equals(two.getTitle())
                && one.getLead().equals(two.getLead())
                && one.getShareUrl().equals(two.getShareUrl())
                && one.getThumbnailUrl().equals(two.getThumbnailUrl())
                && one.getPrivacy() == two.getPrivacy()
                && one.getTotalPage() == two.getTotalPage()
                && one.getTotalComment() == two.getTotalComment()
                && one.getPublishTime() == two.getPublishTime()
                && one.getSiteId() == two.getSiteId()
                && one.getContent().equals(two.getContent())
                && CollectionUtils.equalLists(one.getListReference(), two.getListReference())
                && CollectionUtils.equalLists(one.getPhotos(), two.getPhotos())
                && one.getModeView() == two.getModeView()
                && one.getCategoryParent().equals(two.getCategoryParent())
                && CollectionUtils.equalLists(one.getVideos(), two.getVideos());
    }
}
