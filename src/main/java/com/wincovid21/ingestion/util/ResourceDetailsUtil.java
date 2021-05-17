package com.wincovid21.ingestion.util;

import com.wincovid21.ingestion.domain.AvailabilityType;
import com.wincovid21.ingestion.domain.ResourceDetailDTO;
import com.wincovid21.ingestion.domain.VerificationType;
import com.wincovid21.ingestion.entity.ResourceAvailabilityDetails;
import com.wincovid21.ingestion.entity.ResourceDetails;
import com.wincovid21.ingestion.entity.ResourceRequestEntry;
import com.wincovid21.ingestion.entity.ResourceSubCategory;
import com.wincovid21.ingestion.repository.CityRepository;
import com.wincovid21.ingestion.repository.ResourceCategoryRepository;
import com.wincovid21.ingestion.repository.ResourceSubcategoryRepository;
import com.wincovid21.ingestion.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


public class ResourceDetailsUtil {

    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private StateRepository stateRepository;
    @Autowired
    private ResourceCategoryRepository resourceCategoryRepository;
    @Autowired
    private ResourceSubcategoryRepository resourceSubcategoryRepository;


    public ResourceDetails convertToEntity(List<Object> objectList) {
        ResourceDetails resourceDetails = new ResourceDetails();
        stampCategoryResourceCityState(objectList, resourceDetails);
        resourceDetails.setName(String.valueOf(objectList.get(2)));
        resourceDetails.setDescription(String.valueOf(objectList.get(5)));
        resourceDetails.setPhone1(String.valueOf(objectList.get(6)));
        resourceDetails.setPhone2(String.valueOf(objectList.get(7)));
        resourceDetails.setEmail(String.valueOf(objectList.get(8)));
        resourceDetails.setValid(true);
        resourceDetails.setAddress(String.valueOf(objectList.get(11)));
        if (!objectList.get(12).toString().isEmpty())
            resourceDetails.setPinCode(Long.valueOf(objectList.get(12).toString()));
        if (!objectList.get(13).toString().isEmpty())
            resourceDetails.setQuantityAvailable(objectList.get(13).toString());
        if (!objectList.get(14).toString().isEmpty())
            resourceDetails.setPrice(objectList.get(14).toString());
        resourceDetails.setCreatedBy(String.valueOf(objectList.get(16)));
        resourceDetails.setCreatedOn(System.currentTimeMillis());
        resourceDetails.setUpdatedOn(System.currentTimeMillis());
        resourceDetails.setValid(true);
        if (String.valueOf(objectList.get(20)).equalsIgnoreCase(VerificationType.VERIFIED.getValue())) {
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

    public ResourceRequestEntry convertToRREntry(ResourceDetails resourceDetails) {
        ResourceRequestEntry resourceRequestEntry = new ResourceRequestEntry();
        resourceRequestEntry.setCategory(resourceDetails.getCategory().getCategoryName());
        resourceRequestEntry.setId(resourceDetails.getId().toString());
        resourceRequestEntry.setName(resourceDetails.getName());
        resourceRequestEntry.setAddress(resourceDetails.getAddress());
        resourceRequestEntry.setDescription(resourceDetails.getDescription());
        resourceRequestEntry.setEmail(resourceDetails.getEmail());
        if (Objects.nonNull(resourceDetails.getPinCode())) {
            resourceRequestEntry.setPincode(resourceDetails.getPinCode().toString());
        }
        resourceRequestEntry.setCity(resourceDetails.getCity().getCityName());
        resourceRequestEntry.setState(resourceDetails.getState().getStateName());
        resourceRequestEntry.setPhone1(resourceDetails.getPhone1());
        resourceRequestEntry.setPhone2(resourceDetails.getPhone2());
        resourceRequestEntry.setVerified(resourceDetails.isVerified());
        resourceRequestEntry.setUpdatedAt(resourceDetails.getUpdatedOn().toString());
        resourceRequestEntry.setUpdatedBy("");
        resourceRequestEntry.setCreatedBy(resourceDetails.getCreatedBy());
        resourceRequestEntry.setCreatedAt(resourceDetails.getCreatedOn().toString());
        resourceRequestEntry.setSubcategory(resourceDetails.getResourceType().getSubCategoryName());
        resourceRequestEntry.setCategoryId(resourceDetails.getCategory().getId());
        resourceRequestEntry.setSubcategoryId(resourceDetails.getResourceType().getId());
        resourceRequestEntry.setCityId(resourceDetails.getCity().getId());
        resourceRequestEntry.setStateId(resourceDetails.getState().getId());
        resourceRequestEntry.setValid(resourceDetails.isValid());
        if (AvailabilityType.AVAILABLE.getValue().equalsIgnoreCase(resourceDetails.getQuantityAvailable())) {
            resourceRequestEntry.setAvailable(true);
        } else {
            resourceRequestEntry.setAvailable(false);
        }
        return resourceRequestEntry;
    }

    public ResourceDetails updateEntity(List<Object> objectList, ResourceDetails resourceDetails) {
        stampCategoryResourceCityState(objectList, resourceDetails);
        resourceDetails.setName(String.valueOf(objectList.get(2)));
        resourceDetails.setDescription(String.valueOf(objectList.get(5)));
        resourceDetails.setPhone1(String.valueOf(objectList.get(6)));
        resourceDetails.setPhone2(String.valueOf(objectList.get(7)));
        resourceDetails.setEmail(String.valueOf(objectList.get(8)));
        resourceDetails.setAddress(String.valueOf(objectList.get(11)));
        if (!objectList.get(12).toString().isEmpty())
            resourceDetails.setPinCode(Long.valueOf(objectList.get(12).toString()));
        if (!objectList.get(13).toString().isEmpty())
            resourceDetails.setQuantityAvailable(objectList.get(13).toString());
        if (!objectList.get(14).toString().isEmpty())
            resourceDetails.setPrice(objectList.get(14).toString());
        resourceDetails.setUpdatedOn(System.currentTimeMillis());
        resourceDetails.setValid(true);
        if (String.valueOf(objectList.get(20)).equalsIgnoreCase(VerificationType.VERIFIED.getValue())) {
            resourceDetails.setVerified(true);
        } else {
            resourceDetails.setVerified(false);
        }
        return resourceDetails;
    }

    private ResourceDetails stampCategoryResourceCityState(List<Object> objectList, ResourceDetails resourceDetails) {
        resourceDetails.setCategory(resourceCategoryRepository.fetchCategoryIdForName(String.valueOf(objectList.get(3))));
        resourceDetails.setResourceType(resourceSubcategoryRepository.fetchResourceTypeIdForName(String.valueOf(objectList.get(4))));
        resourceDetails.setCity(cityRepository.fetchCityIdForName(String.valueOf(objectList.get(9))));
        resourceDetails.setState(stateRepository.fetchStateIdForName(String.valueOf(objectList.get(10))));
        return resourceDetails;
    }

    public List<ResourceDetails> transformEntryToEntity(ResourceDetailDTO resourceDetailDTO) {
        final List<Long> resourceTypeIds = resourceDetailDTO.getResourceTypeIds();
        List<ResourceDetails> resourceDetailsList = Collections.synchronizedList(new ArrayList<>());

        resourceTypeIds.forEach(typeId -> {
            Optional<ResourceSubCategory> resourceSubcategoryRepositoryByIdOptional = resourceSubcategoryRepository.findById(typeId);

            if (resourceSubcategoryRepositoryByIdOptional.isPresent()) {
                ResourceSubCategory resourceSubCategory = resourceSubcategoryRepositoryByIdOptional.get();
                ResourceDetails resourceDetails = new ResourceDetails();
                resourceDetails.setCategory(resourceSubCategory.getCategory());
                resourceDetails.setResourceType(resourceSubCategory);
                resourceDetails.setState(stateRepository.findStateById(resourceDetailDTO.getStateId()));
                resourceDetails.setCity(cityRepository.findCityById(resourceDetailDTO.getCityId()));
                resourceDetails.setName(resourceDetailDTO.getName());
                resourceDetails.setPhone1(resourceDetailDTO.getPhone1());
                resourceDetails.setCreatedBy("External User");
                resourceDetails.setCreatedOn(System.currentTimeMillis());
                resourceDetails.setUpdatedOn(System.currentTimeMillis());
                resourceDetails.setPhone2(resourceDetailDTO.getPhone2());
                resourceDetails.setQuantityAvailable(AvailabilityType.OUT_OF_STOCK.getValue());
                resourceDetails.setVerified(false);

                resourceDetailsList.add(resourceDetails);
            }
        });

        return resourceDetailsList;
    }


}
