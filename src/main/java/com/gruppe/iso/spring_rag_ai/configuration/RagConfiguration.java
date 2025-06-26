package com.gruppe.iso.spring_rag_ai.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

@Slf4j
@Configuration
public class RagConfiguration {


    @Value("classpath:/docs/spring-boot-reference.pdf")
    private Resource pdfResource;

    @Bean
    CommandLineRunner initDb(VectorStore vectorStore, JdbcClient jdbcClient) {
        return args -> {
            Integer count = jdbcClient.sql("select count(*) from vector_store")
                    .query(Integer.class)
                    .single();

            log.info("Current count of the Vector Store: {}", count);
            if (count == 0) {
                log.info("Loading Spring Boot Reference PDF into Vector Store");
                var config = PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder().withNumberOfBottomTextLinesToDelete(0)
                                .withNumberOfTopPagesToSkipBeforeDelete(0)
                                .build())
                        .withPagesPerDocument(1)
                        .build();

                var pdfReader = new PagePdfDocumentReader(pdfResource, config);
                var textSplitter = new TokenTextSplitter();
                vectorStore.accept(textSplitter.apply(pdfReader.get()));

                log.info("Application is ready");
            }
        };
    }
}
