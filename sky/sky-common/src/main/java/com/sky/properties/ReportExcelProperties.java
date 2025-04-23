package com.sky.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;

@Component
@ConfigurationProperties(prefix = "sky.excel")
@Data
public class ReportExcelProperties {
    private String filePath;
    private String[] sheet;

}
