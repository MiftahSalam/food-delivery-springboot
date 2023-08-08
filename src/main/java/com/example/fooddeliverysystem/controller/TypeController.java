package com.example.fooddeliverysystem.controller;

import java.io.IOException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.fooddeliverysystem.entity.TypeEntity;
import com.example.fooddeliverysystem.model.dto.TypeDTO;
import com.example.fooddeliverysystem.model.dto.UpdateTypeDTO;
import com.example.fooddeliverysystem.model.response.BaseResponse;
import com.example.fooddeliverysystem.model.response.TypeResponse;
import com.example.fooddeliverysystem.service.meal.TypeService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("types")
public class TypeController {
    private TypeService typeService;

    @Autowired
    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Collection<TypeResponse>>> getTypes() {
        Collection<TypeEntity> allTypes = typeService.getAllTypes();
        if (allTypes.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<Collection<TypeResponse>>builder()
                    .data(null)
                    .message("empty types")
                    .status("no content")
                    .build(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(BaseResponse.<Collection<TypeResponse>>builder()
                .data(TypeResponse.fromTypeEntities(allTypes))
                .message("Success get all types")
                .status("ok")
                .build(), HttpStatus.OK);
    }

    @GetMapping("/regular")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<Collection<TypeResponse>>> getRegularTypes() {
        Collection<TypeEntity> allTypes = typeService.getRegularTypes();
        if (allTypes.isEmpty()) {
            return new ResponseEntity<>(BaseResponse.<Collection<TypeResponse>>builder()
                    .data(null)
                    .message("empty types")
                    .status("no content")
                    .build(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(BaseResponse.<Collection<TypeResponse>>builder()
                .data(TypeResponse.fromTypeEntities(allTypes))
                .message("Success get all types")
                .status("ok")
                .build(), HttpStatus.OK);
    }

    @GetMapping("/{typeId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BaseResponse<TypeResponse>> getType(@PathVariable("typeId") Long typeId) {
        TypeEntity type = typeService.getType(typeId);
        if (type == null) {
            return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                    .data(null)
                    .message("empty types")
                    .status("no content")
                    .build(), HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                .data(TypeResponse.fromEntity(type))
                .message("Success get all types")
                .status("ok")
                .build(), HttpStatus.OK);
    }

    @CrossOrigin
    @DeleteMapping("/{typeId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<TypeResponse>> deleteType(@PathVariable("typeId") Long typeId) {
        if (typeService.deleteType(typeId)) {
            return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                    .data(null)
                    .message("success to delete type")
                    .status("deleted")
                    .build(), HttpStatus.OK);
        }

        return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                .data(null)
                .message("failed to delete type")
                .status("not modified")
                .build(), HttpStatus.NOT_MODIFIED);
    }

    @CrossOrigin
    @PostMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<TypeResponse>> insertType(@RequestBody @Valid TypeDTO type) throws IOException {
        TypeEntity insertType = typeService.insertType(type.toEntity());
        if (insertType != null) {
            return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                    .data(TypeResponse.fromEntity(insertType))
                    .message("success to create type")
                    .status("ok")
                    .build(), HttpStatus.CREATED);
        }

        return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                .data(null)
                .message("failed to insert type")
                .status("not modified")
                .build(), HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin
    @PutMapping()
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<BaseResponse<TypeResponse>> updateType(@RequestBody @Valid UpdateTypeDTO type)
            throws IOException {
        TypeEntity updateType = typeService.updateType(type);
        if (updateType != null) {
            return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                    .data(TypeResponse.fromEntity(updateType))
                    .message("success to update type")
                    .status("ok")
                    .build(), HttpStatus.OK);
        }

        return new ResponseEntity<>(BaseResponse.<TypeResponse>builder()
                .data(null)
                .message("failed to update type")
                .status("not modified")
                .build(), HttpStatus.BAD_REQUEST);
    }
}
