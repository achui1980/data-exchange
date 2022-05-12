package com.ehi.batch;

import com.ehi.batch.core.connector.sftp.SftpTemplate;
import com.ehi.batch.core.processor.Processor;
import com.ehi.batch.kafka.KafkaSender;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.UUID;

/**
 * @author portz
 * @date 04/24/2022 16:53
 */
@RestController
public class SpringBatchJobController {
    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private KafkaSender sender;

    @Autowired
    private WritetoNFS writer;

    @Autowired
    private ApplicationContext context;

    @GetMapping("/invokejob")
    public String invokeBatchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                .toJobParameters();
        Processor processor = context.getBean("CSVBatchProcessor", Processor.class);
        jobLauncher.run(processor.processJob(), jobParameters);

        sender.send(UUID.randomUUID().toString());
        writer.writetoNFS();
        return "Batch job has been invoked";
    }

    @GetMapping("/download")
    public String download() throws Exception {
        URL url = SpringBatchJobController.class.getResource("/demo/demoSftpProperties.properties");
        SftpTemplate sftpTemplate = new SftpTemplate(url.getFile());
        sftpTemplate.download();
        return "download success";
    }
}
