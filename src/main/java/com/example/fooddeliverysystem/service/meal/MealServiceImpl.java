package com.example.fooddeliverysystem.service.meal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.fooddeliverysystem.entity.MealEntity;
import com.example.fooddeliverysystem.entity.MealTypeEntity;
import com.example.fooddeliverysystem.entity.MealTypePK;
import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.model.dto.AddMealDTO;
import com.example.fooddeliverysystem.model.dto.EditMealDTO;
import com.example.fooddeliverysystem.model.dto.TypeDTO;
import com.example.fooddeliverysystem.repository.MealRepository;
import com.example.fooddeliverysystem.repository.MealTypeRepository;
import com.example.fooddeliverysystem.repository.TypeRepository;

@Service
public class MealServiceImpl implements MealService {
    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private MealTypeRepository mealTypeRepository;

    @Override
    public MealEntity editMeal(EditMealDTO mealDTO) {
        MealEntity currentMealEntity = mealRepository.findById(mealDTO.id()).orElse(null);

        if (currentMealEntity == null) {
            return null;
        }

        currentMealEntity.setName(mealDTO.name());
        currentMealEntity.setDescription(mealDTO.description());
        currentMealEntity.setEarlyOrder(mealDTO.earlyOrder());

        return mealRepository.save(currentMealEntity);
    }

    @Override
    public MealEntity getMeal(Long id) {
        return mealRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteMeal(Long id) {
        if (!mealRepository.existsById(id)) {
            return false;
        }

        mealRepository.deleteById(id);

        return true;
    }

    @Override
    public List<MealEntity> getMeals() {
        return mealRepository.findAll();
    }

    @Override
    public MealEntity insertMeal(AddMealDTO mealDTO) {
        MealEntity mealEntity = new MealEntity();
        mealEntity.setDescription(mealDTO.description());
        mealEntity.setEarlyOrder(mealDTO.earlyOrder());
        mealEntity.setName(mealDTO.name());
        mealEntity.setReguler(mealDTO.isReguler());

        mealEntity = mealRepository.save(mealEntity);
        if (mealEntity == null) {
            return null;
        }

        for (TypeDTO typeDTO : mealDTO.typeEntities()) {
            TypeEntity newType = typeRepository
                    .findFirstByNameIgnoreCaseAndPrice(typeDTO.getName(), typeDTO.getPrice()).orElse(null);
            if (newType == null) {
                TypeEntity inserTypeEntity = new TypeEntity();
                inserTypeEntity.setName(typeDTO.getName());
                inserTypeEntity.setPrice(typeDTO.getPrice());
                inserTypeEntity.setReguler(typeDTO.isReguler());

                newType = typeRepository.save(inserTypeEntity);
            }

            // MealTypePK mealTypePK = new MealTypePK();
            // mealTypePK.setTypeId(newType.getId().intValue());
            // mealTypePK.setMealId(mealEntity.getId().intValue());

            MealTypeEntity mealTypeEntity = new MealTypeEntity();
            // mealTypeEntity.setId(mealTypePK);
            mealTypeEntity.setMeal(mealEntity);
            mealTypeEntity.setTypeEntity(newType);

            mealTypeRepository.save(mealTypeEntity);

        }

        return mealEntity;
    }

}
