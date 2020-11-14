package com.cm.pipeline;

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
        System.out.println("obj:"+listLink);
    }
}
