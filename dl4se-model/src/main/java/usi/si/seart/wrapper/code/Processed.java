package usi.si.seart.wrapper.code;

import com.fasterxml.jackson.annotation.JsonProperty;

public interface Processed {

    @JsonProperty(value = "processed_content")
    String getProcessedContent();
    void setProcessedContent(String processedContent);

    @JsonProperty(value = "processed_hash")
    String getProcessedAst();
    void setProcessedAst(String processedAst);
}
