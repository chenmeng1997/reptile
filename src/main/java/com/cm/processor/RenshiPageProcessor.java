package com.cm.processor;

import com.cm.pipeline.renshi.RenshiPipeline;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;

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
     * 数据处理类，如插入数据库
     */
    @Autowired
    RenshiPipeline renshiPipeline;

    /**
     * 页面解析 数据存储
     * @param page
     */
    @Override
    public void process(Page page) {
        //栏目页面 获取连接，加入待执行多列； 获取标签数据放入resultItems
        if (!page.getHtml().regex("http://renshi.people.com.cn/w{2}/d{4}/d{4}/[a-zA-Z]\\d{6}\\-\\d{8}\\.html").match()) {
            //栏目页文章标题标签定位
            page.addTargetRequests(page.getHtml().xpath("//*[@id=\"p2Ab_1\"]/div[@class=\"hdNews clearfix\"]/p/strong/a").links().all());
            //栏目页页码标签定位 过滤
            page.addTargetRequests(page.getHtml().xpath("//*[@id=\"p2Ab_1\"]/div[@class=\"page\"]/a").links().regex("http://renshi.people.com.cn/index\\[1-7]\\.html").all());

            handleChannel(page);
        } else {
            //获取正文内容 放入resultItems
            handleText(page);
        }

    }

    /**
     * 栏目数据封装
     */
    public void handleChannel(Page page) {
        //正文标题标签定位
        List<String> titleList = page.getHtml().xpath("//*[@id=\"p2Ab_1\"]/div[@class=\"hdNews clearfix\"]/p/strong/a").all();
        //简介标签定位
        List<String> summaryList = page.getHtml().xpath("//*[@id=\"p2Ab_1\"]/div[@class=\"hdNews clearfix\"]/p/em/a").all();
        page.putField("titleList", titleList);
        page.putField("summaryList", summaryList);
    }

    /**
     * 栏目数据封装
     */
    public void handleText(Page page) {
        //正文标题标签定位
        String title = page.getHtml().xpath("//*[@class=\"text_c\"]/h1/text()").get();
        page.putField("title", title);
        //文章发布时间标签定位
        String time = page.getHtml().xpath("//*[@class=\"text_c\"]/p[@class=\"sou\"]/text()").get();
        page.putField("time", StringUtils.substring(time, 0,16));
        //正文标签定位
        String context = page.getHtml().xpath("//*div/div/div/div[@class=\"show_text\"]").get();
        page.putField("context", context);
    }

    /**
     * 启动参数
     * @return
     */
    @Override
    public Site getSite() {
        return Site.me()//编码 与网页一致
                .setCharset("GBK")
                //抓取间隔时间,可以解决一些反爬限制
                .setSleepTime(1000 * 90)
                //超时时间
                .setTimeOut(1000 * 10)
                //重试时间
                .setRetrySleepTime(1000*3)
                //重试次数
                .setRetryTimes(3);
    }

//    @Scheduled(initialDelay = 1000, fixedDelay = 1000 * 90)
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
