package com.example.demo.mapper;

import com.example.demo.dto.AktorDTO;
import com.example.demo.model.Aktor;
import org.springframework.stereotype.Component;

@Component
public class AktorMapper {

    public Aktor toEntity(AktorDTO dto) {
        Aktor aktor = new Aktor();
        aktor.setNama(dto.getNama());
        aktor.setNegaraAsal(dto.getNegaraAsal());
        aktor.setFotoUrl(dto.getFotoUrl());
        return aktor;
    }

    public AktorDTO toDto(Aktor aktor) {
        AktorDTO dto = new AktorDTO();
        dto.setNama(aktor.getNama());
        dto.setNegaraAsal(aktor.getNegaraAsal());
        dto.setFotoUrl(aktor.getFotoUrl());
        return dto;
    }

    public void updateEntity(Aktor aktor, AktorDTO dto) {
        aktor.setNama(dto.getNama());
        aktor.setNegaraAsal(dto.getNegaraAsal());
        if (dto.getFotoUrl() != null) {
            aktor.setFotoUrl(dto.getFotoUrl());
        }
    }
}