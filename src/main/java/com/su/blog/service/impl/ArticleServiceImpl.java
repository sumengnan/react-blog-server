package com.su.blog.service.impl;

import com.su.blog.dao.ArticleDao;
import com.su.blog.dao.CategoryDao;
import com.su.blog.dao.CommentDao;
import com.su.blog.dao.TagDao;
import com.su.blog.entity.Article;
import com.su.blog.entity.Category;
import com.su.blog.entity.Tag;
import com.su.blog.entity.vo.ArticleBo;
import com.su.blog.entity.vo.ArticleFileAddVo;
import com.su.blog.entity.vo.ArticleFileVo;
import com.su.blog.entity.vo.CommentBo;
import com.su.blog.service.ArticleService;
import com.su.blog.util.ParamUtil;
import com.su.blog.util.Result.ResultPage;
import com.su.blog.util.Result.Tips;
import com.su.blog.util.constants.FileConstants;
import com.su.blog.util.exception.AppRuntimeException;
import com.su.blog.util.file.FileUtil;
import com.su.blog.util.file.FileZipUtil;
import com.su.blog.util.file.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName: ArticleServiceImpl
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:25
 * @Version:
 */
@Service
@Slf4j
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleDao articleDao;
    @Resource
    private CategoryDao categoryDao;
    @Resource
    private TagDao tagDao;
    @Resource
    private CommentDao commentDao;
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Override
    public ResultPage list(ArticleBo bo) {
        int offset = (bo.getPage() - 1) * bo.getPageSize();
        bo.setOffset(offset);
        List<Integer> countList = articleDao.getListCount(bo);
        List list = new LinkedList();
        int count = 0;
        if (countList != null && countList.size() > 0) {
            if (!ParamUtil.isEmpty(bo.getOrder())) {
                String[] order = bo.getOrder().split(" ");
                bo.setOrder(order[0]);
                bo.setOrderKey(order[1]);
            }
            list = articleDao.getList(bo);
            count = countList.size();
        }
        return new ResultPage(count, list);
    }

    @Override
    public Article getInfo(String id) {
        Article article = articleDao.getInfo(Integer.valueOf(id));
        if(Objects.isNull(article)){
            return new Article();
        }
        List<CommentBo> comList = commentDao.getInfoByArticleId(article.getId());
        article.setComments(comList);
        log.info("获取详情:{}", article);

        return article;
    }

    @Override
    public Article create(ArticleBo bo) {
        Article article = new Article(bo.getTitle(), bo.getContent());
        int num = articleDao.insert(article);
        if (num > 0) {
            if (bo.getCategoryList() != null && bo.getCategoryList().size() > 0) {
                categoryDao.insertBatch(bo.getCategoryList(), article.getId());
            }
            if (bo.getTagList() != null && bo.getTagList().size() > 0) {
                tagDao.insertBatch(bo.getTagList(), article.getId());
            }

        }
        //添加es
        elasticsearchRestTemplate.save(article);
        return article;
    }

    @Override
    public void delete(String id) {
        int res = articleDao.deleteByPrimaryKey(Integer.valueOf(id));
        if (res <= 0) {
            throw new AppRuntimeException(Tips.DELETE_ERROR);
        }
        //删除es
        log.info("删除es数据，id:{}", id);
        elasticsearchRestTemplate.delete(id, Article.class);
    }

    @Override
    public void deleteList(String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        int res = articleDao.deleteByIdList(idList);
        if (res <= 0) {
            throw new AppRuntimeException(Tips.DELETE_ERROR);
        }
        //删除es
        idList.parallelStream().forEach(id -> {
            log.info("删除es数据，id:{}", id);
            elasticsearchRestTemplate.delete(id, Article.class);
        });
    }

    @Override
    public boolean update(ArticleBo bo) {
        int num = articleDao.updateByPrimaryKey(bo);
        if (num > 0) {
            if (bo.getCategoryList() != null && bo.getCategoryList().size() > 0) {
                categoryDao.deleteByArticleId(bo.getId());
                categoryDao.insertBatch(bo.getCategoryList(), bo.getId());
            }
            if (bo.getTagList() != null && bo.getTagList().size() > 0) {
                tagDao.deleteByArticleId(bo.getId());
                tagDao.insertBatch(bo.getTagList(), bo.getId());
            }

        }
        //添加es
        elasticsearchRestTemplate.save(bo);
        return true;
    }

    @Override
    public List<ArticleFileVo> checkExist(List<String> fileNameList) {
        List<ArticleFileVo> result = new LinkedList<>();
        for (String fileName : fileNameList) {
            String title = fileName.split("\\.")[0];
            ArticleFileVo vo = new ArticleFileVo(fileName, title);
            //查看数据库是否已经有title
            Article article = articleDao.getInfoByTitle(title);
            if (article != null) {
                vo.setArticleId(article.getId());
                vo.setExist(true);
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public void upload(MultipartFile[] files) {
        String path = PathUtil.getPath(PathUtil.PathType.UPLOAD);
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            File targetFile = new File(path, name);
            try {
                file.transferTo(targetFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ArticleFileAddVo confirm(Integer authorId, List<ArticleFileVo> uploadList) {
        List insertList = new LinkedList();
        List updateList = new LinkedList();
        for (ArticleFileVo info : uploadList) {
            //获取文章内容
            String path = PathUtil.getPath(PathUtil.PathType.UPLOAD) + info.getFileName();
            String content = FileUtil.readToString(path);
            if (info.getExist() != null && info.getExist()) { //更新
                Article article = new Article(info.getArticleId(), info.getTitle(), content);
                int num = articleDao.updateByPrimaryKey(article);
                if (num > 0) {
                    updateList.add(articleDao.getArticleInfo(article.getId()));
                }
            } else {//添加
                ArticleBo article = new ArticleBo(info.getTitle(), content);
                insertList.add(create(article));
            }
            //删除文件
            FileUtil.deleteFile(path);
        }
        ArticleFileAddVo result = new ArticleFileAddVo();
        result.setInsertList(insertList);
        result.setUpdateList(updateList);
        result.setMessage(FileConstants.CONFIRM_SUCCESS);
        return result;
    }

    @Override
    public void outputAll(HttpServletRequest request, HttpServletResponse response) {
        List<Article> list = articleDao.getListByMdFile();
        long l = System.currentTimeMillis();
        String filePath = PathUtil.getPath(PathUtil.PathType.SOURCE) + l + File.separator;
        for (Article info : list) {
            //把生成的markdown代码写入到系统剪贴板中
            String fileName = info.getTitle() + FileConstants.FILE_SUFFIX_MD;
            String body = getBody(info);
            FileUtil.saveAsFileWriter(filePath + fileName, body);
        }
        FileZipUtil.exportZip(response, filePath, FileConstants.FILE_FILENAME_ZIP);
    }

    @Override
    public void output(HttpServletRequest request, HttpServletResponse response, String id) {
        Article info = articleDao.getInfo(Integer.valueOf(id));
        String body = getBody(info);

        //把生成的markdown代码写入到系统剪贴板中
        String filePath = System.getProperty("user.dir") + File.separator + "source" + File.separator;
        String fileName = info.getTitle() + ".md";
        FileUtil.saveAsFileWriter(filePath + fileName, body);
        FileUtil.export(response, filePath + fileName, fileName);
    }

    /**
     * 获取md信息
     *
     * @return
     */
    private String getBody(Article info) {
        StringBuffer body = new StringBuffer();
        body.append("--\n");
        body.append("title:" + info.getTitle() + "\n");
        body.append("date:" + info.getCreatedAt() + "\n");
        if (info.getCategories() != null && info.getCategories().size() > 0) {
            body.append("categories:\n");
            for (Category category : info.getCategories()) {
                body.append(" - " + category.getName() + "\n");
            }
        }
        if (info.getTags() != null && info.getTags().size() > 0) {
            body.append("tags:\n");
            for (Tag tag : info.getTags()) {
                body.append(" - " + tag.getName() + "\n");
            }
        }

        body.append("---\n\n");
        body.append(info.getContent());
        return body.toString();
    }


}
