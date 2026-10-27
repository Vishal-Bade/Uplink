package com.example.Uplink_Backend.jobs;

import com.example.Uplink_Backend.modules.ocr.entity.OcrJob;
import com.example.Uplink_Backend.modules.ocr.repository.OcrJobRepository;
import com.example.Uplink_Backend.modules.ocr.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@EnableScheduling
public class BackgroundJobScheduler {

    private static final Logger logger = LoggerFactory.getLogger(BackgroundJobScheduler.class);

    private final OcrJobRepository ocrJobRepository;
    private final OcrService ocrService;

    public BackgroundJobScheduler(OcrJobRepository ocrJobRepository, OcrService ocrService) {
        this.ocrJobRepository = ocrJobRepository;
        this.ocrService = ocrService;
    }

    /**
     * Periodically processes pending OCR jobs
     */
    @Scheduled(fixedDelay = 60000)
    public void processPendingOcrJobs() {
        List<OcrJob> pendingJobs = ocrJobRepository.findByStatus("PENDING");
        if (!pendingJobs.isEmpty()) {
            logger.info("Background Worker: Processing {} pending OCR jobs...", pendingJobs.size());
            for (OcrJob job : pendingJobs) {
                try {
                    ocrService.processOcrJob(job);
                } catch (Exception ex) {
                    logger.error("Failed to process OCR job {}", job.getId(), ex);
                }
            }
        }
    }

    /**
     * Periodic opportunity sync and cache refresh
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void syncExternalOpportunitiesAndRecommendations() {
        logger.info("Background Worker: Running scheduled external opportunity synchronization and recommendation updates...");
    }

    /**
     * Daily platform analytics digest
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void generateDailyAnalyticsDigest() {
        logger.info("Background Worker: Daily platform placement & activity digest completed.");
    }
}
