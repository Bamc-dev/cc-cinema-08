package com.cinema.cinema_gestion.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import com.cinema.cinema_gestion.dto.BaseDTO;
import com.cinema.cinema_gestion.dto.PageDTO;
import com.cinema.cinema_gestion.service.GenericService;

public abstract class GenericCRUDController<D extends BaseDTO, V extends Record, S extends GenericService<?, D, V, ?, ?>>
        extends GenericController<D, V, S> {

    protected GenericCRUDController(S service) {
        super(service);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<V> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/admin/create")
    public ResponseEntity<V> create(@RequestBody D dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @PutMapping("/admin/update/{id}")
    public ResponseEntity<V> update(@PathVariable Long id, @RequestBody D dto) {
        service.update(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/list/{page}/{size}")
    public ResponseEntity<PageDTO<V>> list(@PathVariable int page, @PathVariable int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(service.findAll(search, page, size));
    }
}
