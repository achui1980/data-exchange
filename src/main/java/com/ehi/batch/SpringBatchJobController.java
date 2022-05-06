package com.ehi.batch;

import com.ehi.batch.kafka.KafkaSender;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private Job javadevjournaljob;

    @Autowired
    private KafkaSender sender;

    @Autowired
    private WritetoNFS writer;

    @GetMapping("/invokejob")
    public String invokeBatchJob() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                .toJobParameters();
        jobLauncher.run(javadevjournaljob, jobParameters);
        sender.send(UUID.randomUUID().toString());
        writer.writetoNFS();
        return "Batch job has been invoked";
    }
}
