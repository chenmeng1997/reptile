package com.cm.processor;

import com.cm.pipeline.RenshiPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * @author 陈萌
 * @version 1.0
 * @date 2020/11/14 10:11
 */
@Component
public class RenshiPageProcessor implements PageProcessor {

    /**
     * 基础路径
     */
    private static final String BASICS_PATH = "http://renshi.people.com.cn/";

    /**
     * 列表页URL正则
     */
    private String columnPageRegular = "";

    /**
     * 数据处理类，如插入数据库
     */
    @Autowired
    RenshiPipeline renshiPipeline;

    /**
     * 站点
     */
    private Site site = Site.me();

    /**
     * 页面解析 数据存储
     * @param page
     */
    @Override
    public void process(Page page) {
        //获取页面链接
        Selectable links = page.getHtml().links();
        //正文页URL正则
        List<String> list = links.regex("http://renshi.people.com.cn/w{2}/d{4}/d{4}/[a-zA-Z]\\d{6}\\-\\d{8}\\.html").all();
        page.putField("listLink", links);
        //        page.addTargetRequests(list);
        //栏目页面 获取连接，加入待执行多列； 获取标签数据放入resultItems
        if (links.regex("http://renshi.people.com.cn/w{2}/d{4}/d{4}/[a-zA-Z]\\d{6}\\-\\d{8}\\.html").match()) {
            //栏目页文章标题标签定位

            //栏目页文章简介标签定位

            //栏目页页码标签定位 过滤
        } else {
            //获取正文内容 放入resultItems
        }

    }

    /**
     * 数据封装
     */
    public void handle(Page page) {

        //正文标题标签定位

        //文章发布时间标签定位

        //正文标签定位
    }

    /**
     * 启动参数
     * @return
     */
    @Override
    public Site getSite() {
        site    //编码 与网页一致
                .setCharset("GBK")
                //抓取间隔时间,可以解决一些反爬限制
                .setSleepTime(1000 * 90)
                //超时时间
                .setTimeOut(1000 * 10)
                //重试时间
                .setRetrySleepTime(1000*3)
                //重试次数
                .setRetryTimes(3);
        return site;
    }

//    public void run(){
public static void main(String[] args) {
        Spider.create(new RenshiPageProcessor())
                .addUrl(BASICS_PATH)
                //布隆过滤 路径去重
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(100000)))
                // 使用自定义的PipeLine保存数据，可设置多个
                .addPipeline(new RenshiPipeline())
                .thread(5)
                .run();
    }

}
