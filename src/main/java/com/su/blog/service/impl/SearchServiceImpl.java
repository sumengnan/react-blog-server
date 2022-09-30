package com.su.blog.service.impl;

import com.su.blog.dao.ArticleDao;
import com.su.blog.entity.Article;
import com.su.blog.service.ArticleService;
import com.su.blog.service.SearchService;
import com.su.blog.util.Result.ResultPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ArticleServiceImpl
 * @Description:
 * @Author: liuxiaoxiang
 * @Date: 2022/5/10 22:25
 * @Version:
 */
@Service
@Slf4j
public class SearchServiceImpl implements SearchService {

    @Resource(name = "testThreadPool")
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private ArticleService articleService;



    @Async
    @Override
    public void init() throws Exception {
        //删除所有数据
        elasticsearchRestTemplate.delete(new NativeSearchQuery(QueryBuilders.matchAllQuery()),Article.class);
        //添加数据
        articleDao.listAll().stream().forEach(article -> {
            log.info("保存es数据:{}",article);
            elasticsearchRestTemplate.save(article);
        });

    }


    @Override
    public ResultPage list(String keyword, Integer page) throws Exception {

        //构建Search对象
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(page-1, 10));
        //排序
        nativeSearchQueryBuilder.withSorts(SortBuilders.fieldSort("id").order(SortOrder.DESC));

        //如果没有关键字，默认查询所有
        if(StringUtils.isBlank(keyword)){
            nativeSearchQueryBuilder.withQuery(QueryBuilders.matchAllQuery());
        }else {
            //高亮显示
            String pre = "<span style='color:red'>";
            String post = "</span>";
            //指定要高亮的字段将其加上头尾标签
            HighlightBuilder.Field title = new HighlightBuilder.Field("title").preTags(pre).postTags(post);
            HighlightBuilder.Field content = new HighlightBuilder.Field("content").preTags(pre).postTags(post);
            title.numOfFragments(0);
            content.numOfFragments(0);
            //高亮
            nativeSearchQueryBuilder.withHighlightFields(title, content);
            //查询条件
            nativeSearchQueryBuilder.withQuery(QueryBuilders.multiMatchQuery(URLDecoder.decode(keyword,"UTF-8"),"title","content"));
        }

        //es查询
        SearchHits<Article> searchHits = elasticsearchRestTemplate.search(nativeSearchQueryBuilder.build(), Article.class);

        //如果存在高亮字段，则设置高亮
        List<Article> list =searchHits.stream().parallel().map(searchHit->{
            Article article = articleService.getInfo(searchHit.getContent().getId() + "");

            searchHit.getHighlightFields().forEach((k,v)->{
                try {
                    log.info("反射设置高亮字段");
                    article.getClass().getDeclaredMethod("set"+StringUtils.capitalize(k),String.class).invoke(article,v.get(0));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return article;
        }).collect(Collectors.toList());

        return new ResultPage((int)searchHits.getTotalHits(), list);
    }

}
