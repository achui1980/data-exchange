package com.ehi.batch.producer;

import cn.hutool.core.lang.UUID;
import cn.hutool.setting.dialect.Props;
import com.ehi.batch.producer.core.context.FetchContext;
import com.ehi.batch.producer.kafka.KafkaSender;
import com.ehi.batch.producer.listener.FetchFileEventListener;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;

/**
 * @author portz
 * @date 04/24/2022 16:53
 */
@RestController
@Slf4j
public class SpringBatchJobController {

    @Autowired
    private KafkaSender sender;


    @Autowired
    private ApplicationContext context;

    @Autowired
    private AsyncEventBus asyncEventBus;

    @GetMapping("/invokejob")
    public String invokeBatchJob() throws Exception {
        return "Batch job has been invoked";
    }

    @GetMapping("/{actionId}/trigger")
    public String download(@PathVariable("actionId") String actionId) throws Exception {
        String requestToken = UUID.randomUUID().toString();
        URL url = this.getClass().getResource("/demo/" + actionId + ".properties");
        Props props = new Props(url.getFile());
        FetchContext fetchCtx = FetchContext.builder()
                .actionId(actionId)
                .actionProps(props)
                .requestToken(requestToken)
                .build();
        FetchFileEventListener listener = context.getBean(FetchFileEventListener.class);
        asyncEventBus.register(listener);
        asyncEventBus.post(fetchCtx);
        asyncEventBus.unregister(listener);
        return "download success";
    }
}
