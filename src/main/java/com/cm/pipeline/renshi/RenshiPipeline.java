package com.cm.pipeline.renshi;

import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * @author 陈萌
 * @version 1.0
 * @date 2020/11/14 10:27
 */
@Component
public class RenshiPipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        Object listLink = resultItems.get("listLink");
        Object summaryList = resultItems.get("summaryList");

        Object title = resultItems.get("title");
        Object time = resultItems.get("time");
            System.out.println("listLink:"+listLink);
            System.out.println("title:"+title);
            System.out.println("time:"+time);
    }
}
