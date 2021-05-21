package com.example.demo.config;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.tr.ApostropheFilterFactory;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class AnalysisConfigurer implements LuceneAnalysisConfigurer{
    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {
        //Simple analyzer for username
        context.analyzer("NAME").custom()
                .tokenizer(StandardTokenizerFactory.class)
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(ASCIIFoldingFilterFactory.class);
        
        context.analyzer("PROJECT").custom()
                .tokenizer(StandardTokenizerFactory.class)
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(ASCIIFoldingFilterFactory.class)
                .tokenFilter(SnowballPorterFilterFactory.class)
                .param("language","English");
        
        context.analyzer("ISSUE").custom()
                .tokenizer(StandardTokenizerFactory.class)
                .tokenFilter(LowerCaseFilterFactory.class)
                .tokenFilter(ASCIIFoldingFilterFactory.class)
                .tokenFilter(ApostropheFilterFactory.class)
                .tokenFilter(SnowballPorterFilterFactory.class)
                .param("language","English");
                
    }
}
