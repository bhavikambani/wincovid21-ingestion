package com.covimyn.ingestion.util;

import com.covimyn.ingestion.domain.VerificationType;
import com.covimyn.ingestion.entity.ResourceAvailabilityDetails;
import com.covimyn.ingestion.entity.ResourceDetails;

import java.util.List;


public class ResourceDetailsUtil {

    public ResourceDetails convertToEntity(List<Object> objectList) {
        ResourceDetails resourceDetails = new ResourceDetails();
        resourceDetails.setName(String.valueOf(objectList.get(2)));
        resourceDetails.setDescription(String.valueOf(objectList.get(5)));
        resourceDetails.setPhone1(String.valueOf(objectList.get(6)));
        resourceDetails.setPhone2(String.valueOf(objectList.get(7)));
        resourceDetails.setEmail(String.valueOf(objectList.get(8)));
        resourceDetails.setDistrict(String.valueOf(objectList.get(9)));
        resourceDetails.setState(String.valueOf(objectList.get(10)));
        resourceDetails.setAddress(String.valueOf(objectList.get(11)));
        if (!objectList.get(12).toString().isEmpty())
            resourceDetails.setPinCode(Long.valueOf(objectList.get(12).toString()));
        if (!objectList.get(13).toString().isEmpty())
            resourceDetails.setQuantityAvailable(Long.valueOf(objectList.get(13).toString()));
        if (!objectList.get(14).toString().isEmpty())
            resourceDetails.setPrice(Double.valueOf(objectList.get(14).toString()));
        resourceDetails.setCreatedBy(String.valueOf(objectList.get(16)));
        resourceDetails.setCreatedOn(System.currentTimeMillis());
        if (String.valueOf(objectList.get(20)).equalsIgnoreCase(VerificationType.VERIFIED_AND_DONOR_AVAILABLE.getValue()) ||
                String.valueOf(objectList.get(20)).equalsIgnoreCase(VerificationType.VERIFIED_AND_STOCK_AVAILABLE.getValue()) ||
                String.valueOf(objectList.get(20)).equalsIgnoreCase(VerificationType.VERIFIED_BUT_DONOR_UNAVAILBALE.getValue()) ||
                String.valueOf(objectList.get(20)).equalsIgnoreCase(VerificationType.VERIFIED_BUT_STOCK_UNAVIALABLE.getValue())) {
            resourceDetails.setVerified(true);
        } else {
            resourceDetails.setVerified(false);
        }
        return resourceDetails;
    }

    public ResourceAvailabilityDetails convertToRADEntity(List<Object> objectList) {
        ResourceAvailabilityDetails resourceAvailabilityDetails = new ResourceAvailabilityDetails();
        resourceAvailabilityDetails.setCategory(String.valueOf(objectList.get(3)));
        resourceAvailabilityDetails.setResourceType(String.valueOf(objectList.get(4)));
        resourceAvailabilityDetails.setAvailable(true);
        return resourceAvailabilityDetails;
    }


}
