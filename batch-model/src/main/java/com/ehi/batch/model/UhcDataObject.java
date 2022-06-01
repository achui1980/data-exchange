package com.ehi.batch.model;

import lombok.Data;

/**
 * @author portz
 * @date 05/25/2022 17:41
 */
@Data
public class UhcDataObject {
    private String recordType;
    private String medicareNumberOnApplication;
    private String confirmationNumber;
    private String policyId;
    private String planType;
    private String firstName;
    private String lastName;
    private String middleName;
    private String applicationStatus;
    private String lastModifiedDate;
    private String terminationReasonName;
    private String insuredPlanStartDate;
    private String insuredPlanTerminationDate;
    private String planDescription;
    private String recentEnrollmentStatus;
    private String contractNumber;
    private String pbp;
}
