package ru.bank.bsp.customerinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Список bis идентификаторов клиента
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BisIdDtoFeign {
    private String bisUpdateDate;
    private String branch;
    private String clientType;
    private String department;
    private String id;
    private String marketSegment;
    private String payrollCode;
    private String payrollName;
    private Boolean premium;
    private String secureWord;
    private String servicePackage;
    private String servicePackageStartDate;
    private String servicePackageName;
    private String servicePackageTechDescription;
    private String subsegment;
    private Boolean vip;
}
