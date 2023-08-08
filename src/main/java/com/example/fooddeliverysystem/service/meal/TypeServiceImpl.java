package com.example.fooddeliverysystem.service.meal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.model.dto.UpdateTypeDTO;
import com.example.fooddeliverysystem.repository.MealTypeRepository;
import com.example.fooddeliverysystem.repository.TypeRepository;

@Service
public class TypeServiceImpl implements TypeService {
    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private MealTypeRepository mealTypeRepository;

    @Override
    public TypeEntity getType(Long id) {
        return typeRepository.findById(id).orElse(null);
    }

    @Override
    public Collection<TypeEntity> getAllTypes() {
        return typeRepository.findAll();
    }

    @Override
    public boolean deleteType(Long id) {
        if (!typeRepository.existsById(id)) {
            return false;
        }

        mealTypeRepository.deleteByTypeEntityId(id);
        typeRepository.deleteById(id);

        return true;
    }

    @Override
    public TypeEntity insertType(TypeEntity typeEntity) {
        TypeEntity result = typeRepository.findFirstByNameIgnoreCaseAndPriceAndReguler(typeEntity.getName(),
                typeEntity.getPrice(), typeEntity.isReguler()).orElse(null);
        if (result != null) {
            System.out.println(result);
            return null;
        }

        return typeRepository.save(typeEntity);
    }

    @Override
    public TypeEntity updateType(UpdateTypeDTO typeDTO) {
        TypeEntity currentTypeEntity = typeRepository.findById(typeDTO.id()).orElse(null);
        if (currentTypeEntity == null) {
            return null;
        }

        TypeEntity result = typeRepository.findFirstByNameIgnoreCaseAndPriceAndReguler(typeDTO.name(),
                typeDTO.price(), typeDTO.reguler()).orElse(null);
        if (result == null) {
            currentTypeEntity.setName(typeDTO.name());
            currentTypeEntity.setPrice(typeDTO.price());
            currentTypeEntity.setReguler(typeDTO.reguler());

            return typeRepository.save(currentTypeEntity);
        }

        return null;
    }

    @Override
    public Collection<TypeEntity> getRegularTypes() {
        return typeRepository.findAllByRegulerTrue();
    }

    @Override
    public Collection<TypeEntity> getTypesWithRegular() {
        List<TypeEntity> typeEntities = typeRepository.findAll();
        if (typeEntities.isEmpty()) {
            return null;
        } else {
            removeSmallAndBig(typeEntities);
            TypeEntity regular = new TypeEntity();
            regular.setName("regular");
            regular.setPrice(0);
            typeEntities.add(regular);
        }

        return typeEntities;
    }

    @Override
    public Collection<TypeEntity> getTypesWithoutRegular() {
        List<TypeEntity> typeEntities = typeRepository.findAll();
        if (typeEntities.isEmpty()) {
            return null;
        } else {
            removeSmallAndBig(typeEntities);
        }

        return typeEntities;
    }

    @Override
    public Collection<TypeEntity> updateRegularTypes(List<TypeEntity> typeEntities) {
        ArrayList<TypeEntity> newTypeEntities = new ArrayList<TypeEntity>();
        if (typeEntities == null || typeEntities.isEmpty()) {
            return newTypeEntities;
        }

        for (TypeEntity typeEntity : typeEntities) {
            TypeEntity newType = typeRepository.findById(typeEntity.getId()).orElse(null);
            if (newType == null) {
                return new ArrayList<>();
            }
            if (newType.getPrice() < 1) {
                return new ArrayList<>();
            }

            newType.setPrice(typeEntity.getPrice());
            newType = typeRepository.save(newType);

            newTypeEntities.add(newType);
        }

        return newTypeEntities;
    }

    private void removeSmallAndBig(Collection<TypeEntity> typeEntities) {
        for (TypeEntity typeEntity : typeEntities) {
            String typeName = typeEntity.getName().toLowerCase();
            if (typeName.equals("small")) {
                typeEntities.remove(typeEntity);
                break;
            }
        }
        for (TypeEntity typeEntity : typeEntities) {
            String typeName = typeEntity.getName().toLowerCase();
            if (typeName.equals("big")) {
                typeEntities.remove(typeEntity);
                break;
            }
        }
    }
}
