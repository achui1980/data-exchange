package com.ehi.batch;

import cn.hutool.core.lang.UUID;
import com.ehi.batch.core.context.FetchContext;
import com.ehi.batch.kafka.KafkaSender;
import com.ehi.batch.listener.FetchFileEventListener;
import com.google.common.eventbus.AsyncEventBus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
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
    private WritetoNFS writer;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private AsyncEventBus asyncEventBus;

    @GetMapping("/invokejob")
    public String invokeBatchJob() throws Exception {
        return "Batch job has been invoked";
    }

    @GetMapping("/download")
    public String download() throws Exception {
        String requestToken = UUID.randomUUID().toString();
        URL url = SpringBatchJobController.class.getResource("/demo/demoSftpProperties.properties");
        FetchContext fetchCtx = FetchContext.builder()
                .actionId("actionId")
                .requestToken(requestToken)
                .build();
        FetchFileEventListener listener = context.getBean(FetchFileEventListener.class);
        asyncEventBus.register(listener);
        asyncEventBus.post(fetchCtx);
        return "download success";
    }
}
