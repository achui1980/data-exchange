package com.ehi.batch.model;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author portz
 * @date 06/02/2022 16:28
 */
@Data
public class GTLDataObject {
    @ExcelProperty("policy_nbr")
    private String policyNbr;
    @ExcelProperty("cntrct_code")
    private String cntrctCode;
    @ExcelProperty("campaign_code")
    private String appId;

}
